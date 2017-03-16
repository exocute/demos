package demos.graphDemo.activities

import java.io.Serializable

import demos.graphDemo.activities.ImportGML.GraphType
import exocute.Activity

import scala.collection.mutable

/**
  * Created by #GrowinScala
  */
class AveragePathLength extends Activity {

  def process(input: Serializable, params: Vector[String]): Serializable = {
    val (graph, nodeCount, _) = input.asInstanceOf[(GraphType, Int, Int)]
    getAveragePathLength(nodeCount, graph)
  }

  private def getAveragePathLength(nodeCount: Int, graph: GraphType): Double = {
    var dist: Int = 0
    var aux: Int = 0
    var count: Int = 0
    var max: Int = 0
    val nodes = graph.keys.toVector
    for (i <- 0 until nodeCount; j <- i + 1 until nodeCount) {
      aux = nodeDistance(nodes(i), nodes(j), graph, nodeCount)
      if (aux > max) {
        max = aux
      }
      if (aux == -1) {
        count += 1
      } else
        dist += aux
    }
    for (i <- 0 until count)
      dist += max
    (dist * 2).toDouble / (nodeCount * (nodeCount - 1)).toDouble
  }

  private def bfs(from: Int, to: Int, graph: GraphType, nodeCount: Int): Int = {
    val distTo = mutable.Seq[Int]() ++ (0 until nodeCount).map(_ => Int.MaxValue)
    val marked = mutable.Seq[Boolean]() ++ (0 until nodeCount).map(_ => false)
    val q = mutable.Queue[Int]()

    distTo(from) = 0 // set
    marked(from) = true
    q.enqueue(from)
    while (q.nonEmpty) {
      val v = q.dequeue()

      for (child <- graph(v)) {
        if (!marked(child)) {
          distTo(child) = distTo(v) + 1 //set
          marked(child) = true
          q.enqueue(child)
        }
      }
    }
    distTo(to)
  }

  private def nodeDistance(v1: Int, v2: Int, graph: GraphType, nodeCount: Int): Int = {
    if (v1 == v2) 0
    else if (graph(v1).contains(v2)) 1
    else {
      val res = bfs(v1, v2, graph, nodeCount)
      if (res == Int.MaxValue) -1 else res
    }
  }

}
