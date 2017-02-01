import java.awt.image.BufferedImage
import java.io.{File, IOException}
import java.nio.file.{Files, Path, Paths}
import java.util.UUID
import javax.imageio.ImageIO

import io.humble.video._
import io.humble.video.awt.{MediaPictureConverter, MediaPictureConverterFactory}
import org.jcodec.api.awt.AWTSequenceEncoder8Bit

/**
  * Created by #ScalaTeam on 01/02/2017.
  */
object VideoAudioEncodeDecode {

  def main(args: Array[String]): Unit = {
    val (fps,frames) = VideoAudioEncodeDecode.convertVideoToImages("la-la-land-trailer-4_h480p.mp4","frames\\test\\image", "audio.mp3")
    println("converted")
    val keyFrames = Vector(false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, true, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, true, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, true, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, true, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, true, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, true, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, true, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, true, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, true, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, true, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, true, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, true, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, true, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, true, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, true, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, true, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, true, true, false, false, false, false, false, false, false, false, false, true, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, true, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, true, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, true, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, true, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, true, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, true, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, true, false, false, false, false, false, false, false, true, true, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, true, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, true, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, true, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, true, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, true, false, false, false, false, false, false, false, false, false, false, false, false, false, false, true, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, true, false, false, false, false, false, false, false, false, true, false, false, false, false, false, false, true, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, true, false, false, false, false, true, false, false, true, false, true, false, false, false, true, false, false, false, false, false, false, false, true, false, false, false, false, false, false, false, true, false, true, true, true, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, true, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, true, false, false, false, false, false, false, false, false, false, false, false, true, false, false, false, false, false, false, false, false, false, false, false, false, true, false, false, false, false, false, false, false, false, false, false, false, false, false, false, true, false, false, false, false, false, false, false, false, false, false, false, false, false, false, true, false, false, false, false, false, false, false, false, false, false, false, true, false, false, false, false, false, false, false, false, false, false, false, false, false, true, false, false, false, false, false, false, false, false, false, false, false, false, false, false, true, false, false, false, false, false, false, true, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, true, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, true, false, false, false, false, false, false, false, false, false, false, true, false, false, false, false, false, false, false, false, false, false, false, true, false, false, false, false, false, false, false, false, false, true, false, false, false, false, false, false, false, false, false, false, false, false, true, false, false, false, false, false, false, false, false, false, false, false, false, false, true, false, false, false, false, false, false, false, false, false, false, false, true, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false)
    VideoAudioEncodeDecode.produceVideo("result.mp4","frames\\test\\image","audio.mp3",fps,keyFrames)
  }

  @throws[InterruptedException]
  @throws[IOException]
  def convertVideoToImages(moviePath: String, framesTarget: String, audioTarget : String): (Int,Int) = {

    val demuxer: Demuxer = Demuxer.make
    demuxer.open(moviePath, null, false, true, null, null)
    val numStreams: Int = demuxer.getNumStreams

    var frame: Int = 0
    var videoStreamId: Int = -1
    var videoDecoder: Decoder = null
    println("Started to decode...")
    for (i <- 0 until numStreams) {
      {
        val stream: DemuxerStream = demuxer.getStream(i)
        val decoder: Decoder = stream.getDecoder
        if (decoder != null && (decoder.getCodecType eq MediaDescriptor.Type.MEDIA_VIDEO)) {
          videoStreamId = i
          videoDecoder = decoder
        }
      }
    }
    if (videoStreamId == -1) throw new RuntimeException("could not find video stream in container: " + moviePath)
    videoDecoder.open(null, null)
    val picture: MediaPicture = MediaPicture.make(videoDecoder.getWidth, videoDecoder.getHeight, videoDecoder.getPixelFormat)
    val converter: MediaPictureConverter = MediaPictureConverterFactory.createConverter(MediaPictureConverterFactory.HUMBLE_BGR_24, picture)
    var image: BufferedImage = null
    val packet: MediaPacket = MediaPacket.make


    while (demuxer.read(packet) >= 0) if (packet.getStreamIndex == videoStreamId) {
      var offset: Int = 0
      var bytesRead: Int = 0
      do {
        bytesRead += videoDecoder.decode(picture, packet, offset)
        if (picture.isComplete) {
          image = converter.toImage(image, picture)
          savePicture(framesTarget+frame,image)
          println("Image Processed "+frame)
          frame+=1
        }
        offset += bytesRead
      } while (offset < packet.getSize)
    }


    do {
      videoDecoder.decode(picture, null, 0)
      if (picture.isComplete) {
        image = converter.toImage(image, picture)
        savePicture(framesTarget+frame,image)
        println("Image Processed "+frame)
        frame+=1
      }
    } while (picture.isComplete)

    val seconds: Int = (demuxer.getDuration / 1000000).toInt

    demuxer.close()

    if(!audioTarget.isEmpty)
      extractAudio(moviePath,audioTarget)

    (frame / seconds,frame)
  }

  def savePicture(path : String, image : BufferedImage) = {
    val file : File = new File(path+".png");
    file.getParentFile().mkdirs();
    ImageIO.write(image, "png", file)
  }

  /**
    * Produces a video with a sequence of input images and an audio file
    *
    * @param targetVideoName the path and name of the result video
    * @param framesPathName the path and the prefix of the images
    * @param audioPathName the path and the prefix of the audio file generated - if no audio audio available
    *                      use and empty string
    * @param frameRate fps
    * @param keyFrames Vector of booleans - true --> isKeyFrame and false --> isNotAKeyFrame
    */
  def produceVideo(targetVideoName: String, framesPathName: String, audioPathName: String, frameRate: Int, keyFrames: Vector[Boolean]) = {

    //creates video
    val time: Long = System.currentTimeMillis()
    println("Encoding Video...")
    createVideo()
    println("Encoded in:" + (System.currentTimeMillis() - time) + " Milliseconds")


    if(!audioPathName.isEmpty) {
      //adds audio
      val timeAudio: Long = System.currentTimeMillis()
      println("Adding Audio...")
      addAudio()
      println("Audio added in:" + (System.currentTimeMillis() - timeAudio) + " Milliseconds")
    }

    def createVideo() = {
      def encodeImage(encoder: AWTSequenceEncoder8Bit, index: Int, isKeyFrame: Boolean) = {
        println("Encoded image: "+index)
        val image: BufferedImage = ImageIO.read(new File(framesPathName + index + ".png"))
        encoder.encodeImage(image, isKeyFrame)
      }

      val enc: AWTSequenceEncoder8Bit = AWTSequenceEncoder8Bit.createSequenceEncoder8Bit(new File(targetVideoName), frameRate)

      //first image should always be a keyframe
      encodeImage(enc, 0, true)

      for (index <- 1 until keyFrames.size) {
        encodeImage(enc, index, keyFrames(index))
      }
      enc.finish()
    }

    def addAudio() = {
      //uses ffmpeg to add audio
      val rt: Runtime = Runtime.getRuntime
      val id : String = UUID.randomUUID().toString
      val pr: Process = rt.exec("lib\\ffmpeg -i " + targetVideoName + " -i " + audioPathName + " -c:v copy -c:a aac -strict experimental "+id+".mp4");
      pr.waitFor()
      val path : Path = Paths.get(targetVideoName)
      Files.delete(path)
      new File(id+".mp4").renameTo(new File(targetVideoName))
    }
  }

  /**
    * extracts the audio from a file
    * @param moviePath - movie path source
    * @param audioPath - audio path destination
    */
  def extractAudio(moviePath: String, audioPath: String) = {
    val rt: Runtime = Runtime.getRuntime
    val pr: Process = rt.exec("lib\\ffmpeg -i " + moviePath + " "+ audioPath)
    pr.waitFor()
  }


}
