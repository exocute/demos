package demos.compressDemo

import java.awt.image.BufferedImage
import java.awt.{Color, Font, Graphics2D}
import java.io.{File, IOException, PrintStream, Serializable}
import java.nio.file.{Files, Path, Paths}
import java.util.UUID
import java.util.concurrent.atomic.AtomicInteger
import javax.imageio.ImageIO
import javax.swing.JFileChooser

import clifton.graph.{ExoGraph, ExocuteConfig}
import demos.compressDemo.activities.ImageConverters.convertToImageSerializable
import io.humble.video._
import io.humble.video.awt.{MediaPictureConverter, MediaPictureConverterFactory}
import org.jcodec.api.awt.AWTSequenceEncoder8Bit
import swave.core.{Drain, Spout, StreamEnv}

import scala.concurrent.Future
import scala.util.Success

/**
  * Created by #GrowinScala
  */
object CompressDemoUsingSwave {

  private val DATA_FOLDER = new File("demosData", "compressVideo")

  private val TEMP_PATH = new File(DATA_FOLDER, "temp")
  private val FRAMES_PATH = TEMP_PATH
  private val AUDIO_PATH = new File(TEMP_PATH, "audio.mp3")
  private val LOG_PATH = new File(TEMP_PATH, "log.txt")

  private val grpFile = new File(DATA_FOLDER, "compressParallel.grp")
  private val jarFile = new File(DATA_FOLDER, "classes.jar")

  private val LONG_TIME = 60 * 60 * 1000
  private val SHOW_AT = 50

  def main(args: Array[String]): Unit = {
    chooseFile("Choose a video file").foreach {
      videoPath => run(videoPath, args)
    }
  }

  private def run(videoPath: String, args: Array[String]): Unit = {
    val t = System.currentTimeMillis()
    val resultPath = videoPath.substring(0, videoPath.lastIndexOf('.')) + "_compressed.mp4"
    println("      __  ___ __     __  __      __  __  ___ __  __  __  \n\\  /||  \\|__ /  \\   /  `/  \\|\\/||__)|__)|__ /__`/  \\|__) \n \\/ ||__/|___\\__/   \\__,\\__/|  ||   |  \\|___.__/\\__/|  \\ \n                                                         ")
    //if no fps set default is 25
    val fps = if (args.nonEmpty) args(0).toInt else 25

    implicit val env = StreamEnv()
    implicit val gl = scala.concurrent.ExecutionContext.Implicits.global

    val Success(exoGraph) = ExocuteConfig.setHosts().addGraph(grpFile, List(jarFile), 5 * 60 * 1000)

    //decodes the video to images and extracts the audio
    val decodeSpout: Spout[Int] = convertVideoToImages(videoPath)

    def getPairImage(frame: Int): (Serializable, Serializable) = {
      val image1 = ImageIO.read(new File(FRAMES_PATH, (frame - 1) + ".png"))
      val image2 = ImageIO.read(new File(FRAMES_PATH, frame + ".png"))
      val ser1: Serializable = convertToImageSerializable(image1)
      val ser2: Serializable = convertToImageSerializable(image2)
      (ser1, ser2)
    }

    val imagePairsSpout: Spout[(Serializable, Serializable)] =
      decodeSpout.filter(_ > 0).map {
        (imageIndex: Int) => getPairImage(imageIndex)
      }

    //encodes available results from the space and creates a new compressed video
    val enc: AWTSequenceEncoder8Bit = AWTSequenceEncoder8Bit.createSequenceEncoder8Bit(new File(resultPath), fps)

    val drainEncode: Drain[Serializable, Unit] = {
      var encoded = 0

      Drain.foreach { resultSerializable: Serializable =>
        val result = encoded == 0 || resultSerializable.asInstanceOf[Boolean]

        val image: BufferedImage = ImageIO.read(new File(FRAMES_PATH, encoded + ".png"))
        enc.encodeImage(if (result) printKeyFrame(image) else image, result)
        encoded += 1
        if (encoded % SHOW_AT == 0) println("Encoded " + encoded + " Images")
      }.dropResult
    }

    //    val promiseInject = Promise[Unit]()
    val drainShowInjected: Drain[Int, Unit] =
      Drain.foreach {
        index: Int =>
          if ((index + 1) % SHOW_AT == 0)
            println("Injected " + (index + 1) + " Images")
      }.dropResult

    val sendToExocuteSpout: Spout[Int] =
      new ExocuteDrain[Serializable](exoGraph).createSpout(imagePairsSpout)

    val exocuteSpout = new ExocuteSpout(exoGraph)

    sendToExocuteSpout
      .buffer(50)
      .fanOutBroadcast()
      .sub.to(drainShowInjected)
      .sub.map(exocuteSpout.get).end
      .continue
      .drainTo(drainEncode)

    enc.finish()

    // add the extracted audio to the video
    addAudio(new File(resultPath))
    exoGraph.closeGraph()
    env.shutdown()

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

  /**
    * graphic implementation to choose a file
    *
    * @return Path's File
    */
  private def chooseFile(description: String): Option[String] = {
    val chooser: JFileChooser = new JFileChooser()
    //    chooser.setCurrentDirectory(new File(System.getProperty("user.home")))
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
    * @param file file to delete
    */
  private def deleteTempFile(file: File): Unit = {
    if (file.exists()) {
      val pathFile: Path = Paths.get(file.getPath)
      Files.delete(pathFile)
    }
  }

  /**
    * extracts the audio from a file
    *
    * @param moviePath movie path source
    */
  private def extractAudio(moviePath: String): Unit = {
    //removes the audio file if exists
    deleteTempFile(AUDIO_PATH)
    val rt: Runtime = Runtime.getRuntime
    val pr: Process = rt.exec("lib" + File.separator + "ffmpeg -i " + moviePath + " " + AUDIO_PATH)
    pr.waitFor()
  }

  /**
    * uses ffmpeg to add audio to a file
    *
    * @return
    */
  private def addAudio(targetVideoFile: File): Unit = {
    val id: String = UUID.randomUUID().toString
    val rt: Runtime = Runtime.getRuntime
    //uses ffmpeg to add audio
    val pr: Process = rt.exec("lib" + File.separator + "ffmpeg -i " + targetVideoFile + " -i " + AUDIO_PATH + " -c:v copy -c:a aac -strict experimental " + id + ".mp4")
    pr.waitFor()

    //deletes temp files
    deleteTempFile(targetVideoFile)
    deleteTempFile(AUDIO_PATH)

    //rename the temp file to the final result Video
    new File(id + ".mp4").renameTo(targetVideoFile)
  }

  /**
    * Receives a moviePath and generates all the frames of the video
    *
    * @param videoPath the full path to the video you want to decode
    */
  @throws[InterruptedException]
  @throws[IOException]
  private def convertVideoToImages(videoPath: String): Spout[Int] = {

    def start(): Stream[Int] = {
      val frame: AtomicInteger = new AtomicInteger(0)

      def savePicture(path: String, image: BufferedImage) = {
        val file: File = new File(path + ".png")
        file.getParentFile.mkdirs()
        ImageIO.write(image, "png", file)
      }

      val err = System.err
      val file = LOG_PATH
      file.getParentFile.mkdirs()
      val stream = new PrintStream(file)
      System.setErr(stream)

      val demuxer: Demuxer = Demuxer.make
      demuxer.open(videoPath, null, false, true, null, null)
      val numStreams: Int = demuxer.getNumStreams
      var videoStreamId: Int = -1
      var videoDecoder: Decoder = null

      System.setErr(err)
      stream.close()
      deleteTempFile(file)

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
      if (videoStreamId == -1) throw new RuntimeException("Could not find video stream in container: " + videoPath)

      //opens the videoDecoder to start processing; sets the size of the frames ; creates the converterMediaPicture
      videoDecoder.open(null, null)
      val picture: MediaPicture = MediaPicture.make(videoDecoder.getWidth, videoDecoder.getHeight, videoDecoder.getPixelFormat)
      val converter: MediaPictureConverter = MediaPictureConverterFactory.createConverter(MediaPictureConverterFactory.HUMBLE_BGR_24, picture)
      val packet: MediaPacket = MediaPacket.make

      //starts decoding frame by frame reading packet by packet
      def decode1(): Stream[Int] = {
        if (demuxer.read(packet) >= 0) {
          if (packet.getStreamIndex == videoStreamId) {
            var offset: Int = 0

            def decode1Aux(): Stream[Int] = {
              offset += videoDecoder.decode(picture, packet, offset)
              if (picture.isComplete) {
                val image: BufferedImage = converter.toImage(null, picture)
                savePicture(FRAMES_PATH.getPath + File.separatorChar + frame, image)
                frame.incrementAndGet()
                if (frame.get() % SHOW_AT == 0) println("Decoded " + frame.get() + " frames")
                (frame.get() - 1) #:: (if (offset < packet.getSize) decode1Aux() else decode1())
              } else if (offset < packet.getSize) decode1Aux() else decode1()
            }

            decode1Aux()
          } else
            decode1()
        } else
          decode2()
      }

      def decode2(): Stream[Int] = {
        videoDecoder.decode(picture, null, 0)
        if (picture.isComplete) {
          val image: BufferedImage = converter.toImage(null, picture)
          savePicture(FRAMES_PATH.getPath + File.separatorChar + frame, image)
          frame.incrementAndGet()
          if (frame.get() % SHOW_AT == 0) println("Decoded " + frame.get() + " frames")
          frame.get #:: decode2()
        } else {
          demuxer.close()
          println("Image decoding done!")

          extractAudio(videoPath)
          println("Audio decoding done!")

          Stream()
        }
      }

      decode1()
    }

    Spout.fromIterable(start())
  }

  /**
    * prints "KEYFRAME" in a bufferedImage
    *
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

  private implicit def convert(spout: Spout[AnyVal]): Spout[java.io.Serializable] = spout.asInstanceOf[Spout[java.io.Serializable]]

  private implicit def convert(v: AnyVal): java.io.Serializable = v.asInstanceOf[java.io.Serializable]

  /**
    * We need to set up a Drain that can act as Injector i.e ExocuteDrain is an injector.
    * then we can attach the Drain to the end of a local input pipeline.
    *
    *
    * Next we need to make a Swave spout that is a Collector i.e ExocuteSpout is a Collector,
    * then we can attach the Spout the start of a local pipeline
    *
    */
  private class ExocuteDrain[-T <: Serializable](exoGraph: ExoGraph) {

    def create(): Drain[T, Future[Vector[Int]]] = {
      Drain.fold(Vector[Int]()) { (vec, elem) =>
        val index = exoGraph.injector.inject(elem)
        vec :+ index
      }
    }

    def createSpout(spout: Spout[T]): Spout[Int] = {
      spout.map(elem => exoGraph.injector.inject(elem))
    }

  }

  private class ExocuteSpout(exoGraph: ExoGraph) {

    def get(injIndex: Int): Serializable = {
      exoGraph.collector.collectIndex(injIndex, LONG_TIME).get.get
    }

  }

}
