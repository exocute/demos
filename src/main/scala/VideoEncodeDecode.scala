import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

import it.sauronsoftware.jave._
import org.jcodec.api.awt.{AWTFrameGrab8Bit, AWTSequenceEncoder8Bit}

/**
  * Created by #ScalaTeam on 26/01/2017.
  */
object VideoEncodeDecode {

  def main(args: Array[String]): Unit = {
    produceImages("la-la-land-trailer-4_h480p.mp4", "frameslala\\image", "audio\\", "png", 1760)
    //val keyFrames = Vector(false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, true, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, true, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, true, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, true, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, true, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, true, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, true, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false)
    //val keyFrames2 = Vector(true, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false)
    //produceVideo("result2.mp4", "frames\\frames\\image", 25, "png", keyFrames)
  }

  /**
    * Takes a video as input and generates all the frames as images and saves the audio to mp3 file
    * @param moviePath - The location of the movie
    * @param framesPath - The location to save frames followed by the prefix e.g. frames\\image
    * @param audioPath - The location to save the audio followed by the prefix e.g. e.g. audio\\audioFromFile
    *                  - If you don't to use audio, please type an empty string
    * @param imageType - png,jpg,bmp,...
    * @param frames - fps * duration(seconds) of the original video
    */
  def produceImages(moviePath: String, framesPath: String, audioPath: String, imageType: String, frames: Int) = {

    val time: Long = System.currentTimeMillis()
    println("Decoding Video...")

    val file: File = new File(moviePath)
    for (i <- 0 until frames) {
      val frame: BufferedImage = AWTFrameGrab8Bit.getFrame(file, i)
      ImageIO.write(frame, imageType, new File(framesPath + i + "." + imageType))
    }

    //extracts Audio from the video
    if(!audioPath.isEmpty)
      extractAudio(moviePath, audioPath + ".mp3")

    println("Decoded in:" + (System.currentTimeMillis() - time) + " Milliseconds")
  }


  /**
    * Produces a video with a sequence of input images and an audio file
    *
    * @param targetVideoName the path and name of the result video
    * @param framesPathName the path and the prefix of the images
    * @param audioPathName the path and the prefix of the audio file generated - if no audio audio available
    *                      use and empty string
    * @param frameRate fps
    * @param imageType png, jpeg, bmp, etc..
    * @param keyFrames Vector of booleans - true --> isKeyFrame and false --> isNotAKeyFrame
    */
  def produceVideo(targetVideoName: String, framesPathName: String, audioPathName: String, frameRate: Int, imageType: String, keyFrames: Vector[Boolean]) = {

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
        val image: BufferedImage = ImageIO.read(new File(framesPathName + index + "." + imageType))
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
      val rt: Runtime = Runtime.getRuntime()
      val pr: Process = rt.exec("ffmpeg -i " + targetVideoName + " -i " + audioPathName + ".mp3 -c:v copy -c:a aac -strict experimental compressed.mp4");
      pr.waitFor()
    }
  }

  /**
    * extracts the audio from a file
    * @param sourceFileName - movie path source
    * @param targetFileName - audio path destination
    */
  def extractAudio(sourceFileName: String, targetFileName: String) = {
    val source: File = new File(sourceFileName)
    val target: File = new File(targetFileName)
    val audio: AudioAttributes = new AudioAttributes
    audio.setCodec("libmp3lame")
    audio.setBitRate(new Integer(128000))
    audio.setChannels(new Integer(2))
    audio.setSamplingRate(new Integer(44100))
    val attrs: EncodingAttributes = new EncodingAttributes
    attrs.setFormat("mp3")
    attrs.setAudioAttributes(audio)
    val encoder: Encoder = new Encoder
    try
      encoder.encode(source, target, attrs)
    catch {
      case e: IllegalArgumentException => {
        // TODO Auto-generated catch block
        e.printStackTrace()
      }
      case e: InputFormatException => {
        // TODO Auto-generated catch block
        e.printStackTrace()
      }
      case e: EncoderException => {
        // TODO Auto-generated catch block
        e.printStackTrace()
      }
    }
  }
}
