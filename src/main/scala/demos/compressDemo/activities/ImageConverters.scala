package demos.compressDemo.activities

import java.awt.image.BufferedImage
import java.awt.{Graphics2D, Image}
import javax.swing.ImageIcon

import scala.language.implicitConversions

/**
  * Created by #ScalaTeam on 25-01-2017.
  */
object ImageConverters {

  implicit def convertToBufferedImage(image: Image): BufferedImage = {
    val newImage: BufferedImage = new BufferedImage(
      image.getWidth(null), image.getHeight(null),
      BufferedImage.TYPE_INT_ARGB)
    val g: Graphics2D = newImage.createGraphics()
    g.drawImage(image, 0, 0, null)
    g.dispose()
    newImage
  }

  implicit def convertToImageSerializable(image: BufferedImage): ImageIcon = {
    new ImageIcon(image)
  }

}
