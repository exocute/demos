package demos

import java.awt.image.BufferedImage
import java.io.{File, IOException, PrintStream}
import java.nio.file.{Files, Path, Paths}
import java.util.UUID
import javax.imageio.ImageIO

import io.humble.video._
import io.humble.video.awt.{MediaPictureConverter, MediaPictureConverterFactory}
import org.jcodec.api.awt.AWTSequenceEncoder8Bit

/**
  * Created by #GrowinScala
  *
  * Library that Decodes and Encodes Video and Audio
  * Libraries used: JCodec(Encode), HumbleVideo(Decode) and FFMPEG(Encode and Decode Audio)
  */
object VideoAudioEncodeDecode {

  //folders and files to save temporary information
  val FRAMESPATH = "temp\\image"
  val AUDIOPATH = "temp\\audio.mp3"
  val TEMPPATH = "temp"

  /**
    * Receives a moviePath and generates all the frames of the video
    *
    * @param moviePath the full path to the video you want to decode
    * @return (fps,frames)
    */
  @throws[InterruptedException]
  @throws[IOException]
  def convertVideoToImages(moviePath: String): (Int, Int) = {

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
    var frame: Int = 0
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
          frame += 1
          if (frame % 100 == 0) println("Decoded " + frame + " frames")
        }
        offset += bytesRead
      } while (offset < packet.getSize)
    }

    do {
      videoDecoder.decode(picture, null, 0)
      if (picture.isComplete) {
        image = converter.toImage(image, picture)
        savePicture(FRAMESPATH + frame, image)
        frame += 1
        if (frame % 100 == 0) println("Decoded " + frame + " frames")
      }
    } while (picture.isComplete)

    //gets the duration of the movie to calculate the fps
    val seconds: Double = demuxer.getDuration.toDouble / 1000000

    //closes de decoder
    demuxer.close()
    println("Image decoding done!\nStarting to decode audio...")

    //extracts Audio
    extractAudio(moviePath)
    println("Audio decoding done!")
    println("Decoding movie completed!")

    ((frame / seconds).toInt, frame)
  }

  /**
    * Produces a video with a sequence of input images and an audio file
    * Removes all temporary files when process is done
    *
    * @param targetVideoName the path and name of the result video
    * @param frameRate       fps
    * @param keyFrames       Vector of booleans - true --> isKeyFrame and false --> isNotAKeyFrame
    */
  def produceVideo(targetVideoName: String, frameRate: Int, keyFrames: Vector[Boolean]): Unit = {

    def createVideo() = {
      def encodeImage(encoder: AWTSequenceEncoder8Bit, index: Int, isKeyFrame: Boolean) = {
        val image: BufferedImage = ImageIO.read(new File(FRAMESPATH + index + ".png"))
        encoder.encodeImage(image, isKeyFrame)
      }

      val enc: AWTSequenceEncoder8Bit = AWTSequenceEncoder8Bit.createSequenceEncoder8Bit(new File(targetVideoName), frameRate)

      //first image should always be a keyframe
      encodeImage(enc, 0, isKeyFrame = true)

      //encodes the rest of the frames
      for (frame <- 1 until keyFrames.size) {
        if (frame % 100 == 0) println("Encoded " + frame + " frames")
        encodeImage(enc, frame, keyFrames(frame))
      }

      enc.finish()
    }

    def addAudio() = {
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

    //creates video
    val time: Long = System.currentTimeMillis()
    println("Encoding Video...")
    createVideo()
    println("Encoded in:" + (System.currentTimeMillis() - time) + " Milliseconds")

    //adds audio
    val timeAudio: Long = System.currentTimeMillis()
    println("Adding Audio...")
    addAudio()
    println("Audio added in:" + (System.currentTimeMillis() - timeAudio) + " Milliseconds")

    //removing TempFiles
    for (frame <- keyFrames.indices)
      deleteTempFile(FRAMESPATH + frame + ".png")
    deleteTempFile(TEMPPATH)

    println("Video Compressed in " + (System.currentTimeMillis() - time) + " Milliseconds.")
  }

  /**
    * extracts the audio from a file
    *
    * @param moviePath - movie path source
    */
  def extractAudio(moviePath: String): Int = {
    //removes the audio file if exists
    deleteTempFile(AUDIOPATH)
    val rt: Runtime = Runtime.getRuntime
    val pr: Process = rt.exec("lib\\ffmpeg -i " + moviePath + " " + AUDIOPATH)
    pr.waitFor()
  }

  /**
    * Removes a File from the system if it exists
    */
  def deleteTempFile(path: String): Unit = {
    val filePath: File = new File(path)
    if (filePath.exists()) {
      val pathFile: Path = Paths.get(path)
      Files.delete(pathFile)
    }
  }

}