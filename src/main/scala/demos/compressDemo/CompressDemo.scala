package demos.compressDemo

import java.io._
import javax.imageio.ImageIO
import javax.swing.JFileChooser

import clifton.graph.StarterExoGraph
import demos.VideoAudioEncodeDecode
import demos.compressDemo.activities.ImageConverters._

import scala.util.Success

/**
  * Created by #ScalaTeam on 25-01-2017.
  */
object CompressDemo {

  def main(args: Array[String]): Unit = {
    chooseFile("Choose a video file").foreach {
      videoPath =>
        val t = System.currentTimeMillis()
        val resultPath = videoPath.substring(0, videoPath.lastIndexOf('.')) + "_compressed.mp4"
        println("      __  ___ __     __  __      __  __  ___ __  __  __  \n\\  /||  \\|__ /  \\   /  `/  \\|\\/||__)|__)|__ /__`/  \\|__) \n \\/ ||__/|___\\__/   \\__,\\__/|  ||   |  \\|___.__/\\__/|  \\ \n                                                         ")
        val (fps, frames) = VideoAudioEncodeDecode.convertVideoToImages(videoPath)
        val keyFrames = sendToExocute(VideoAudioEncodeDecode.FRAMESPATH, frames)
        VideoAudioEncodeDecode.produceVideo(resultPath, fps, keyFrames)

        println("Total Time: " + (System.currentTimeMillis() - t))
    }

    //    val fileName = new File("VideoCompression.PNG")
    //    val image = ImageIO.read(fileName)
    //    val newImage = convertToBufferedImage(image)
    //    val outputfile = new File("saved.png")
    //    ImageIO.write(newImage, "png", outputfile)
  }

  private def chooseFile(description: String): Option[String] = {
    val chooser: JFileChooser = new JFileChooser()
    chooser.setCurrentDirectory(new File(System.getProperty("user.home")))
    val returnVal = chooser.showOpenDialog(null)
    if (returnVal == JFileChooser.APPROVE_OPTION) {
      Some(chooser.getSelectedFile.getAbsolutePath)
    } else
      None
  }

  def sendToExocute(framesLocation: String, maxFrames: Int): Vector[Boolean] = {
    val MAX = maxFrames
    val grpFile = new File("compress.grp")
    val jarFile = new File("classes.jar")
    val diffFile = new File("diffTemp.txt")

    //    SpaceCache.dataHost = "52.178.165.169"
    //    SpaceCache.jarHost = "52.178.165.169"
    //    SpaceCache.signalHost = "52.178.165.169"

    val starter = new StarterExoGraph

    val Success(graph) = starter.addGraph(grpFile, List(jarFile), 30 * 60 * 1000)
    val injector = graph.injector
    val collector = graph.collector

    def createPairs(before: Serializable, nextFiles: Stream[File]): Stream[(Serializable, Serializable)] = {
      nextFiles match {
        case Stream() => Stream()
        case fileName #:: others =>
          val image = ImageIO.read(fileName)
          val ser: Serializable = convertToImageSerializable(image)
          (before, ser) #:: createPairs(ser, others)
      }
    }

    val list: Stream[File] = (0 until MAX).toStream.map(n => new File(framesLocation + n + ".png"))

    def allPairs = createPairs(ImageIO.read(list.head), list.tail)

    println(s"Starting to inject inputs...")

    var n = 0
    val ids: Vector[Int] =
      (for {
        pair <- allPairs
      } yield {
        n += 1
        if (n % 100 == 0)
          println(s"Injected $n inputs")
        injector.inject(pair)
      }).toVector


    println(s"Injected all inputs")

    val file: FileWriter = new FileWriter(diffFile, false)
    file.write("0.0\n")

    println("Starting to collect results...")

    val diffs =
      for (index <- ids.indices) yield {
        val diff = collector.collectIndex(ids(index), 24 * 60 * 60 * 1000).get.asInstanceOf[Boolean]
        if (index + 1 % 100 == 0)
          println(s"Collected ${index + 1} results")
        file.write(diff + "\n")
        diff
      }

    file.close()
    println("Collected all results")

    true +: diffs.toVector
  }

}
