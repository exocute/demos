package demos.compressDemo.activities

import java.awt.image.BufferedImage
import javax.swing.ImageIcon

import exocute.Activity
import ImageConverters._

/**
  * Created by #GrowinScala
  */
class Diff extends Activity {
  def process(input: java.io.Serializable, params: Vector[String]): java.io.Serializable = {
    val pointScale = params(0).toInt
    val (img1, img2) = input.asInstanceOf[(ImageIcon, ImageIcon)]
    imageDifference(img1.getImage, img2.getImage, pointScale)
  }

  private val MASK = 0xFF
  private val RED = 16
  private val GREEN = 8
  private val BLUE = 0

  def imageDifference(img1: BufferedImage, img2: BufferedImage, pointScale: Int): Double = {

    assert(img1.getWidth() == img2.getWidth())
    assert(img1.getHeight() == img2.getHeight())
    assert(img1.getColorModel.equals(img2.getColorModel))

    val imgX = img1.getWidth()
    val imgY = img1.getHeight()

    val xStride = Math.max(1, imgX / pointScale)
    val yStride = Math.max(1, imgY / pointScale)

    val totalDiff: Long = (0 until imgX by xStride).foldLeft(0L) { (acc: Long, x) =>
      val lineDiff: Long = (0 until imgY by yStride).foldLeft(0L) { (acc: Long, y) =>
        val pix1 = img1.getRGB(x, y)
        val pix2 = img2.getRGB(x, y)

        val dBlu = ((pix1 >> BLUE) & MASK) - ((pix2 >> BLUE) & MASK)
        val dGrn = ((pix1 >> GREEN) & MASK) - ((pix2 >> GREEN) & MASK)
        val dRed = ((pix1 >> RED) & MASK) - ((pix2 >> RED) & MASK)

        acc + Math.abs(dRed) + Math.abs(dGrn) + Math.abs(dBlu)
      }
      acc + lineDiff
    }
    totalDiff.toDouble / (imgX.toDouble * imgY.toDouble)
  }
}
