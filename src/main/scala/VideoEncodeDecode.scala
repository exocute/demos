import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

import org.jcodec.api.FrameGrab
import org.jcodec.api.awt.AWTSequenceEncoder
import org.jcodec.common.model.Picture
import org.jcodec.scale.AWTUtil

/**
  * Created by #ScalaTeam on 26/01/2017.
  */
object VideoEncodeDecode {

  def produceImage(fileName: String, imageType: String, frames: Int) = {
    println("Decoding Video...")
    val time: Long = System.currentTimeMillis()
    val file: File = new File(fileName)
    for (i <- 0 to frames) {
      val frame: Picture = FrameGrab.getFrameFromFile(file, i)
      val framebuf: BufferedImage = AWTUtil.toBufferedImage(frame)
      ImageIO.write(framebuf, imageType, new File("frames\\image" + i + "." + imageType))
    }
    println("Decoded in:" + (System.currentTimeMillis() - time) + " Milliseconds")
  }

  def produceVideo(fileName: String, frameRate: Int,, imageType: String, keyFrames: Vector[Boolean]) = {
    println("Encoding Video...")
    val time: Long = System.currentTimeMillis()
    val enc: AWTSequenceEncoder = AWTSequenceEncoder.createSequenceEncoder(new File(fileName), frameRate)

    for (i <- 0 to keyFrames.size) {
      val image: BufferedImage = ImageIO.read(new File("frames\\image" + i + "." + imageType))
      enc.encodeImage(image, keyFrames(i))
    }
    enc.finish()
    println("Encoded in:" + (System.currentTimeMillis() - time) + " Milliseconds")
  }

}
