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
  * Created by #GrowinScala
  */
object CompressDemoParallel {

  private val DATA_FOLDER = new File("demosData", "compressVideo")

  private val TEMP_PATH = new File(DATA_FOLDER, "temp")
  private val FRAMES_PATH = TEMP_PATH
  private val AUDIO_PATH = new File(TEMP_PATH, "audio.mp3")

  def main(args: Array[String]): Unit = {
    chooseFile("Choose a video file").foreach {
      videoPath =>
        val t = System.currentTimeMillis()
        val resultPath = videoPath.substring(0, videoPath.lastIndexOf('.')) + "_compressed.mp4"
        println("      __  ___ __     __  __      __  __  ___ __  __  __  \n\\  /||  \\|__ /  \\   /  `/  \\|\\/||__)|__)|__ /__`/  \\|__) \n \\/ ||__/|___\\__/   \\__,\\__/|  ||   |  \\|___.__/\\__/|  \\ \n                                                         ")
        //if no fps set default is 25
        val fps = if (args.nonEmpty) args(0).toInt else 25

        val grpFile = new File(DATA_FOLDER, "compressParallel.grp")
        val jarFile = new File(DATA_FOLDER, "classes.jar")
        val TIMETOTAKE = 24 * 60 * 60 * 1000

        val starter = new StarterExoGraph

        val Success(graph) = starter.addGraph(grpFile, List(jarFile), 30 * 60 * 1000)

        //number images decoded
        val images: AtomicInteger = new AtomicInteger()

        //ids of the injects
        val ids: BlockingDeque[Int] = new LinkedBlockingDeque[Int]()

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
            val image1 = ImageIO.read(new File(FRAMES_PATH, (frame - 2) + ".png"))
            val image2 = ImageIO.read(new File(FRAMES_PATH, (frame - 1) + ".png"))
            val ser1: Serializable = convertToImageSerializable(image1)
            val ser2: Serializable = convertToImageSerializable(image2)
            (ser1, ser2)
          }
        }

        //encodes available results from the space and creates a new compressed video
        val encode = new Thread {
          override def run(): Unit = {
            val enc: AWTSequenceEncoder8Bit = AWTSequenceEncoder8Bit.createSequenceEncoder8Bit(new File(resultPath), fps)
            var encoded = 0
            //waits until the first image is decoded
            while (images.get() < 1) ()

            //first image is always a keyframe
            val image: BufferedImage = ImageIO.read(new File(FRAMES_PATH, encoded + ".png"))
            enc.encodeImage(printKeyFrame(image), true)
            println("Encoded Image: " + encoded)
            encoded += 1

            val collector = graph.collector

            while (inject.isAlive || ids.size() > 0) {
              if (ids.size() > 0) {
                val result = collector.collectIndex(ids.poll(), TIMETOTAKE).get.get.asInstanceOf[Boolean]
                val image: BufferedImage = ImageIO.read(new File(FRAMES_PATH, encoded + ".png"))
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
        graph.closeGraph()

        cleanTempDirectory()
        val lines = List(
          " Compressed File: " + resultPath,
          " FPS: " + fps,
          " Total Time: " + (System.currentTimeMillis() - t)
        )
        val maxSize = lines.map(_.length).max + 1
        val tableLines = lines.map(line => "|" + line + " " * (maxSize - line.length) + "|")
        val resumeStr = "RESUME"
        val resumeSize1 = (maxSize - resumeStr.length) / 2
        val resumeSize2 = if (resumeStr.length % 2 == 0) resumeSize1 else resumeSize1 + 1
        val resume = "|" + " " * resumeSize1 + resumeStr + " " * resumeSize2 + "|"
        val finalTable = (List(" " + "_" * maxSize, resume) ++ tableLines ++ List("|" + "_" * maxSize + "|")).mkString("\n")
        println(finalTable)
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
    chooser.setCurrentDirectory(new File(System.getProperty("user.dir")))
    val returnVal = chooser.showOpenDialog(null)
    if (returnVal == JFileChooser.APPROVE_OPTION) {
      Some(chooser.getSelectedFile.getAbsolutePath)
    } else
      None
  }

  /**
    * Removes a File from the system if it exists
    *
    * @param file
    */
  private def deleteTempFile(file: File) = {
    if (file.exists()) {
      val pathFile: Path = Paths.get(file.getPath)
      Files.delete(pathFile)
    }
  }

  /**
    * extracts the audio from a file
    *
    * @param moviePath - movie path source
    */
  private def extractAudio(moviePath: String) = {
    //removes the audio file if exists
    deleteTempFile(AUDIO_PATH)
    val rt: Runtime = Runtime.getRuntime
    val pr: Process = rt.exec("lib\\ffmpeg -i " + moviePath + " " + AUDIO_PATH)
    pr.waitFor()
  }

  /**
    * uses ffmpeg to add audio to a file
    *
    * @param targetVideoName
    * @return
    */
  private def addAudio(targetVideoName: String) = {
    val id: String = UUID.randomUUID().toString
    val rt: Runtime = Runtime.getRuntime
    //uses ffmpeg to add audio
    val pr: Process = rt.exec("lib\\ffmpeg -i " + targetVideoName + " -i " + AUDIO_PATH + " -c:v copy -c:a aac -strict experimental " + id + ".mp4")
    pr.waitFor()

    //deletes temp files
    deleteTempFile(new File(targetVideoName))
    deleteTempFile(AUDIO_PATH)

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
  private def convertVideoToImages(moviePath: String, inj: Injector, frame: AtomicInteger) = {

    def savePicture(path: String, image: BufferedImage) = {
      val file: File = new File(path + ".png")
      file.getParentFile.mkdirs()
      ImageIO.write(image, "png", file)
    }

    val demuxer: Demuxer = Demuxer.make
    demuxer.open(moviePath, null, false, true, null, null)
    val numStreams: Int = demuxer.getNumStreams
    var videoStreamId: Int = -1
    var videoDecoder: Decoder = null

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
    while (demuxer.read(packet) >= 0) {
      if (packet.getStreamIndex == videoStreamId) {
        var offset: Int = 0
        var bytesRead: Int = 0
        do {
          bytesRead += videoDecoder.decode(picture, packet, offset)
          if (picture.isComplete) {
            image = converter.toImage(image, picture)
            savePicture(FRAMES_PATH.getPath + File.separatorChar + frame, image)
            frame.incrementAndGet()
            if (frame.get() % 100 == 0) println("Decoded " + frame.get() + " frames")
          }
          offset += bytesRead
        } while (offset < packet.getSize)
      }
    }

    do {
      videoDecoder.decode(picture, null, 0)
      if (picture.isComplete) {
        image = converter.toImage(image, picture)
        savePicture(FRAMES_PATH.getPath + File.separatorChar + frame, image)
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
  private def printKeyFrame(image: BufferedImage): BufferedImage = {
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
  private def cleanTempDirectory() = {
    Option(TEMP_PATH.listFiles()).foreach(fileList =>
      for (file <- fileList)
        if (!file.isDirectory)
          file.delete()
    )
    TEMP_PATH.delete()
  }
}
