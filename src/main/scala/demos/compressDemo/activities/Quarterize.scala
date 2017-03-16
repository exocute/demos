package demos.compressDemo.activities

import java.awt.{Graphics2D, Image}
import java.awt.image.BufferedImage
import javax.swing.ImageIcon

import ImageConverters._
import exocute.Activity

/**
  * Created by #GrowinScala
  */
class Quarterize extends Activity {
  def process(input: java.io.Serializable, params: Vector[String]): java.io.Serializable = {
    val (image1, image2) = input.asInstanceOf[(ImageIcon, ImageIcon)]
    val q1 = Vector(0, 1, 2, 3).map(q => quarterize(image1.getImage, q)).map(convertToImageSerializable)
    val q2 = Vector(0, 1, 2, 3).map(q => quarterize(image2.getImage, q)).map(convertToImageSerializable)
    q1.zip(q2)
  }

  def quarterize(image: BufferedImage, quadrant: Int): BufferedImage = {
    val width = image.getWidth
    val height = image.getHeight

    val midX = width / 2
    val midY = height / 2

    val qImage =
      if (quadrant == 0) {
        image.getSubimage(0, 0, midX, midY)
      } else if (quadrant == 1) {
        image.getSubimage(midX, 0, width - midX, midY)
      } else if (quadrant == 2) {
        image.getSubimage(0, midY, midX, height - midY)
      } else {
        //if (quadrant == 3) {
        image.getSubimage(midX, midY, width - midX, height - midY)
      }
    cloneImage(qImage)
  }

  def cloneImage(image: BufferedImage): BufferedImage = {
    val w = image.getWidth
    val h = image.getHeight
    val t = image.getType
    val newImage = new BufferedImage(w, h, t)
    val g2 = newImage.getGraphics
    g2.drawImage(image, 0, 0, null)
    newImage
  }

}
