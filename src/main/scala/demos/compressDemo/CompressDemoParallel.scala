package demos.compressDemo

import java.awt.image.BufferedImage
import java.awt.{Color, Font, Graphics2D}
import java.io._
import java.nio.file.{Files, Path, Paths}
import java.util.UUID
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.{BlockingDeque, LinkedBlockingDeque}
import javax.imageio.ImageIO
import javax.swing.JFileChooser

import api.Injector
import clifton.graph.StarterExoGraph
import demos.compressDemo.activities.ImageConverters._
import io.humble.video._
import io.humble.video.awt.{MediaPictureConverter, MediaPictureConverterFactory}
import org.jcodec.api.awt.AWTSequenceEncoder8Bit

import scala.util.Success

/**
  * Created by #ScalaTeam on 25-01-2017.
  */
object CompressDemoParallel {

  val FRAMESPATH = "temp\\image"
  val AUDIOPATH = "temp\\audio.mp3"
  val TEMPPATH = "temp"


  def main(args: Array[String]): Unit = {
    chooseFile("Choose a video file").foreach {
      videoPath =>
        val t = System.currentTimeMillis()
        val resultPath = videoPath.substring(0, videoPath.lastIndexOf('.')) + "_compressed.mp4"
        println("      __  ___ __     __  __      __  __  ___ __  __  __  \n\\  /||  \\|__ /  \\   /  `/  \\|\\/||__)|__)|__ /__`/  \\|__) \n \\/ ||__/|___\\__/   \\__,\\__/|  ||   |  \\|___.__/\\__/|  \\ \n                                                         ")
        //if no fps set default is 25
        val fps = if (args.nonEmpty) args(0).toInt else 25

        val grpFile = new File("compressParallel.grp")
        val jarFile = new File("classes.jar")
        val TIMETOTAKE = 24 * 60 * 60 * 1000

        val starter = new StarterExoGraph

        val Success(graph) = starter.addGraph(grpFile, List(jarFile), 30 * 60 * 1000)

        //number images decoded
        val images: AtomicInteger = new AtomicInteger()

        //ids of the injects
        val ids: BlockingDeque[String] = new LinkedBlockingDeque[String]()

        //starts to inject when two images are available
        var lastInjected = 2

        //decodes the video to images and extracts the audio
        val decode = new Thread {
          override def run(): Unit = {
            convertVideoToImages(videoPath, graph.injector, images)
          }
        }

        //injects available input into the space
        val inject = new Thread {
          override def run(): Unit = {
            val injector = graph.injector
            while (decode.isAlive || lastInjected < images.get() + 1) {
              //just start when there is two available images
              if (lastInjected < images.get() + 1) {
                val (img1, img2) = getPairImage(lastInjected)
                ids.put(injector.inject((img1, img2)))
                lastInjected += 1
                if (lastInjected % 100 == 0) println("Injected " + lastInjected + " Images")
                //sleeps some time to free the flySpace
                Thread.sleep(100)
              }
            }
          }

          def getPairImage(frame: Int): (Serializable, Serializable) = {
            val image1 = ImageIO.read(new File(FRAMESPATH + (frame - 2) + ".png"))
            val image2 = ImageIO.read(new File(FRAMESPATH + (frame - 1) + ".png"))
            val ser1: Serializable = convertToImageSerializable(image1)
            val ser2: Serializable = convertToImageSerializable(image2)
            (ser1, ser2)
          }
        }

        //encodes available results from the space and creates a new compressed video
        val encode = new Thread {
          override def run = {
            val enc: AWTSequenceEncoder8Bit = AWTSequenceEncoder8Bit.createSequenceEncoder8Bit(new File(resultPath), fps)
            var encoded = 0
            //waits until the first image is decoded
            while (images.get() < 1) ()

            //first image is always a keyframe
            val image: BufferedImage = ImageIO.read(new File(FRAMESPATH + encoded + ".png"))
            enc.encodeImage(printKeyFrame(image), true)
            println("Encoded Image: " + encoded)
            encoded += 1

            val collector = graph.collector

            while (inject.isAlive || ids.size() > 0) {
              if (ids.size() > 0) {
                val result = collector.collect(ids.poll(), TIMETOTAKE).get.asInstanceOf[Boolean]
                val image: BufferedImage = ImageIO.read(new File(FRAMESPATH + encoded + ".png"))
                enc.encodeImage(if (result) printKeyFrame(image) else image, result)
                if (encoded % 100 == 0) println("Encoded " + encoded + " Images")
                encoded += 1
              }
            }

            println("Finished")
            enc.finish()

            //add the extracted audio to the video
            addAudio(resultPath)
          }
        }

        decode.start()
        inject.start()
        encode.start()
        encode.join()

        //clean TempDirectory
        cleanTempDirectory()
        val tableSize = 58
        println(" " + "_" * tableSize + "\n|\t\t\t\t\t\tRESUME")
        println("|Compressed File: " + resultPath)
        println("|FPS: " + fps)
        println("|Frames: " + images.get())
        println("|Total Time: " + (System.currentTimeMillis() - t))
        println("|" + "_" * tableSize)
    }
  }

  /**
    * graphic implementation to choose a file
    *
    * @param description
    * @return Path's File
    */
  private def chooseFile(description: String): Option[String] = {
    val chooser: JFileChooser = new JFileChooser()
    chooser.setCurrentDirectory(new File(System.getProperty("user.home")))
    val returnVal = chooser.showOpenDialog(null)
    if (returnVal == JFileChooser.APPROVE_OPTION) {
      Some(chooser.getSelectedFile.getAbsolutePath)
    } else
      None
  }

  /**
    * Removes a File from the system if it exists
    *
    * @param path
    */
  def deleteTempFile(path: String) = {
    val filePath: File = new File(path)
    if (filePath.exists()) {
      val pathFile: Path = Paths.get(path)
      Files.delete(pathFile)
    }
  }

  /**
    * extracts the audio from a file
    *
    * @param moviePath - movie path source
    */
  def extractAudio(moviePath: String) = {
    //removes the audio file if exists
    deleteTempFile(AUDIOPATH)
    val rt: Runtime = Runtime.getRuntime
    val pr: Process = rt.exec("lib\\ffmpeg -i " + moviePath + " " + AUDIOPATH)
    pr.waitFor()
  }

  /**
    * uses ffmpeg to add audio to a file
    *
    * @param targetVideoName
    * @return
    */
  def addAudio(targetVideoName: String) = {
    val id: String = UUID.randomUUID().toString
    val rt: Runtime = Runtime.getRuntime
    //uses ffmpeg to add audio
    val pr: Process = rt.exec("lib\\ffmpeg -i " + targetVideoName + " -i " + AUDIOPATH + " -c:v copy -c:a aac -strict experimental " + id + ".mp4")
    pr.waitFor()

    //deletes temp files
    deleteTempFile(targetVideoName)
    deleteTempFile(AUDIOPATH)

    //rename the temp file to the final result Video
    new File(id + ".mp4").renameTo(new File(targetVideoName))
  }

  /**
    * Receives a moviePath and generates all the frames of the video
    *
    * @param moviePath the full path to the video you want to decode
    */
  @throws[InterruptedException]
  @throws[IOException]
  def convertVideoToImages(moviePath: String, inj: Injector, frame: AtomicInteger) = {

    def savePicture(path: String, image: BufferedImage) = {
      val file: File = new File(path + ".png")
      file.getParentFile.mkdirs()
      ImageIO.write(image, "png", file)
    }

    val err = System.err
    val file = new File(TEMPPATH + "\\log.txt")
    file.getParentFile.mkdirs()
    val stream = new PrintStream(file)
    System.setErr(stream)

    val demuxer: Demuxer = Demuxer.make
    demuxer.open(moviePath, null, false, true, null, null)
    val numStreams: Int = demuxer.getNumStreams
    var videoStreamId: Int = -1
    var videoDecoder: Decoder = null

    System.setErr(err)
    stream.close()
    deleteTempFile(file.getAbsolutePath)

    println("Started to decode...")
    //gets the decoder to videoDecoder
    for (i <- 0 until numStreams) {
      val stream: DemuxerStream = demuxer.getStream(i)
      val decoder: Decoder = stream.getDecoder
      if (decoder != null && (decoder.getCodecType eq MediaDescriptor.Type.MEDIA_VIDEO)) {
        videoStreamId = i
        videoDecoder = decoder
      }
    }
    //if no decoder available for the format exception thrown
    if (videoStreamId == -1) throw new RuntimeException("Could not find video stream in container: " + moviePath)

    //opens the videoDecoder to start processing; sets the size of the frames ; creates the converterMediaPicture
    videoDecoder.open(null, null)
    val picture: MediaPicture = MediaPicture.make(videoDecoder.getWidth, videoDecoder.getHeight, videoDecoder.getPixelFormat)
    val converter: MediaPictureConverter = MediaPictureConverterFactory.createConverter(MediaPictureConverterFactory.HUMBLE_BGR_24, picture)
    var image: BufferedImage = null
    val packet: MediaPacket = MediaPacket.make

    //starts decoding frame by frame reading packet by packet
    while (demuxer.read(packet) >= 0) if (packet.getStreamIndex == videoStreamId) {
      var offset: Int = 0
      var bytesRead: Int = 0
      do {
        bytesRead += videoDecoder.decode(picture, packet, offset)
        if (picture.isComplete) {
          image = converter.toImage(image, picture)
          savePicture(FRAMESPATH + frame, image)
          frame.incrementAndGet()
          if (frame.get() % 100 == 0) println("Decoded " + frame.get() + " frames")
        }
        offset += bytesRead
      } while (offset < packet.getSize)
    }

    do {
      videoDecoder.decode(picture, null, 0)
      if (picture.isComplete) {
        image = converter.toImage(image, picture)
        savePicture(FRAMESPATH + frame, image)
        frame.incrementAndGet()
        if (frame.get() % 100 == 0) println("Decoded " + frame.get() + " frames")
      }
    } while (picture.isComplete)

    //closes de decoder
    demuxer.close()
    println("Image decoding done!\nStarting to decode audio...")

    //extracts Audio
    extractAudio(moviePath)
    println("Audio decoding done!")
    println("Decoding movie completed!")
  }

  /**
    * prints "KEYFRAME" in a bufferedImage
    *
    * @param image
    * @return
    */
  def printKeyFrame(image: BufferedImage): BufferedImage = {
    val g: Graphics2D = image.createGraphics()
    g.drawImage(image, 0, 0, null)
    g.setFont(new Font("Arial", Font.BOLD, 20))
    g.setColor(Color.RED)
    g.drawString("KEYFRAME", 25, 25)
    g.dispose()
    image
  }

  /**
    * removes all temp files and temp dir
    *
    * @return
    */
  def cleanTempDirectory() = {
    val temp: File = new File(TEMPPATH)
    for (file <- temp.listFiles())
      if (!file.isDirectory)
        file.delete()
    temp.delete()
  }
}
