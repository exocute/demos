package com.zink.image

import java.awt.image._
import java.awt._
import javax.imageio.ImageIO
import java.io.File

/**
  * This code (c) 2011 Zink Digital Ltd
  */

object ImageOps {

  private val MASK = 0xFF
  private val RED = 16
  private val GREEN = 8
  private val BLUE = 0

  def imageDifference( img1 : BufferedImage, img2 : BufferedImage ) : Double = {

    assert ( img1.getWidth() == img2.getWidth() )
    assert ( img1.getHeight() == img2.getHeight() )
    assert ( img1.getColorModel.equals( img2.getColorModel ) )

    val imgX = img1.getWidth()
    val imgY = img1.getHeight()

    val pointScale = 128
    val xStride = Math.max(1,imgX/pointScale)
    val yStride = Math.max(1,imgY/pointScale)

    val totalDiff : Long = (0 until imgX by xStride).foldLeft(0L) { (acc : Long, x) =>
      val lineDiff : Long = (0 until imgY by yStride).foldLeft(0L) { (acc : Long, y) =>
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


  def averageColor( img1 : BufferedImage) : Color = {

    val imgX = img1.getWidth()
    val imgY = img1.getHeight()
    val totalPix = imgX.toFloat * imgY.toFloat
    println(totalPix)

    // old school - marginally faster than the fold
    var bluTotal = 0.0f
    var grnTotal = 0.0f
    var redTotal = 0.0f

    (0 until imgX).foreach { x =>
      for ( y <- 0 until imgY) {
        val pix1 = img1.getRGB(x, y)
        bluTotal += ((pix1 >> BLUE) & MASK)
        grnTotal += ((pix1 >> GREEN) & MASK)
        redTotal += ((pix1 >> RED) & MASK)
      }
    }
    val red = redTotal / totalPix
    val grn = grnTotal / totalPix
    val blu = bluTotal / totalPix
    new Color(red/256f , grn/256f, blu/256f)
  }

  def main(args: Array[String]): Unit = {
    val image : Image = ImageIO.read(new File("test.jpg"))
    val f1 : BufferedImage = image.asInstanceOf[BufferedImage]
    println(averageColor(f1))

    val image2 : Image = ImageIO.read(new File("test2.jpg"))
    val f2 : BufferedImage = image2.asInstanceOf[BufferedImage]

    println(imageDifference(f2,f1))

    val q1f1 = quarterize(f1,0)
    val q3f1 = quarterize(f1,3)
    val q1f2 = quarterize(f2,0)
    val q3f2 = quarterize(f2,3)

    println((imageDifference(q3f1, q3f2)))
  }


  def cloneImage(image : BufferedImage) : BufferedImage = {
    val w = image.getWidth
    val h = image.getHeight
    val t = image.getType
    val newImage = new BufferedImage(w, h, t)
    val g2 = newImage.getGraphics
    g2.drawImage(image, 0, 0, null)
    newImage
  }


  // Get as near as possible to a quarter of an image
  // quadrant are numbered like this
  //  ---------
  //  | 0 | 1 |
  //  ---------
  //  | 2 | 3 |
  //  ---------
  def quarterize(image : BufferedImage, quadrant : Int) : BufferedImage = {

    val width = image.getWidth
    val height = image.getHeight

    val midX = width/2
    val midY = height/2

    // TODO - Reformulate without vars
    // assume quadrant 0
    var x = 0
    var y = 0
    var w = midX
    var h = midY

    if (quadrant == 1) {
      x = midX
      y = 0
      w = width - midX
      h = midY
    }

    if (quadrant == 2) {
      x = 0
      y = midY
      w = midX
      h = height - midY
    }

    if (quadrant == 3) {
      x = midX
      y = midY
      w = width - midX
      h = height - midY
    }

    cloneImage( image getSubimage(x,y,w,h) )

  }

}
