import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

import it.sauronsoftware.jave._
import org.jcodec.api.{FrameGrab, FrameGrab8Bit}
import org.jcodec.api.awt.AWTSequenceEncoder
import org.jcodec.common.model.{Picture, Picture8Bit}
import org.jcodec.scale.AWTUtil

/**
  * Created by #ScalaTeam on 26/01/2017.
  */
object VideoEncodeDecode {

  def produceImage(fileName: String, targetName: String, imageType: String, frames: Int) = {
    println("Decoding Video...")
    val time: Long = System.currentTimeMillis()
    val file: File = new File(fileName)
    for (i <- 0 until frames) {
      val frame: Picture8Bit = FrameGrab8Bit.getFrameFromFile(file, i)
      val framebuf: BufferedImage = AWTUtil.toBufferedImage8Bit(frame)
      ImageIO.write(framebuf, imageType, new File(targetName + i + "." + imageType))
    }
    //extractAudio(fileName, "audio\\audio.mp3")
    println("Decoded in:" + (System.currentTimeMillis() - time) + " Milliseconds")
  }

  def main(args: Array[String]): Unit = {
    produceImage("la-la-land-trailer-4_h480p.mp4","frames\\image","jpg", 100)
  }

  def produceVideo(fileName: String, targetName: String, frameRate: Int, imageType: String, keyFrames: Vector[Boolean]) = {

    def encodeImage(encoder: AWTSequenceEncoder, index: Int) = {
      val image: BufferedImage = ImageIO.read(new File(targetName + index + "." + imageType))
      encoder.encodeImage(image, keyFrames(index))
    }

    println("Encoding Video...")
    val time: Long = System.currentTimeMillis()
    val enc: AWTSequenceEncoder = AWTSequenceEncoder.createSequenceEncoder(new File(fileName), frameRate)

    //first image should always be a keyframe
    encodeImage(enc, 0)

    for (index <- 1 until keyFrames.size) {
      encodeImage(enc, index)
    }
    enc.finish()

    /*val rt: Runtime = Runtime.getRuntime()
    val pr: Process = rt.exec("ffmpeg -i " + fileName + " -i audio\\audio.mp3 -c:v copy -c:a aac -strict experimental compressed.mp4");
    pr.waitFor()*/
    println("Encoded in:" + (System.currentTimeMillis() - time) + " Milliseconds")
  }

  /*def extractAudio(sourceFileName: String, targetFileName: String) = {
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
  }*/

}
