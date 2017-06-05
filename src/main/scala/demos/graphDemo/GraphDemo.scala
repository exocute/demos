package demos.graphDemo

import java.io.File
import java.nio.file.Files

import api.{Collector, Injector}
import clifton.graph.{ExocuteConfig, StarterExoGraph}
import exonode.clifton.node.SpaceCache

import scala.util.{Failure, Success}

/**
  * Created by #GrowinScala
  */
object GraphDemo {

  private val demosData = new File("demosData", "graph")
  private val file = new File(demosData, "graph.grp")
  private val jars = List(new File(demosData, "graphClasses.jar"))
  private val graphFile = new File(demosData, "power2.gml")

  def main(args: Array[String]): Unit = {

    ExocuteConfig.setHosts(SpaceCache.signalHost, SpaceCache.dataHost, SpaceCache.jarHost)
    StarterExoGraph.addGraphFile(file, jars, 60 * 60 * 1000) match {
      case Failure(e) =>
        val msg = e.getMessage
        println(s"Error loading grp file:\n${if (msg == null) e else msg}")
      case Success(exoGraph) =>
        val inj: Injector = exoGraph.injector
        val col: Collector = exoGraph.collector

        injectAndCollect(inj, col)
        exoGraph.closeGraph()
    }
  }

  private def injectAndCollect(inj: Injector, col: Collector): Unit = {
    val bytes: Array[Byte] = Files.readAllBytes(graphFile.toPath)
    val amount = 10

    val initTime = System.currentTimeMillis()
    println("Sending graphs as input...")
    inj.inject(amount, bytes)
    val maxTime = 60 * 60 * 1000
    println(s"Collecting results... (maximum waiting time: $maxTime)")
    val results = col.collectMany(amount, maxTime)
    println(results)
    println("All results collected!")
    println("Time Spent: " + (System.currentTimeMillis() - initTime))
  }

}
