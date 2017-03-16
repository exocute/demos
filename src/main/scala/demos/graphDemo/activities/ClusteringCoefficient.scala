package demos.graphDemo.activities

import java.io.Serializable

import demos.graphDemo.activities.ImportGML.GraphType
import exocute.Activity

/**
  * Created by #ScalaTeam on 06/03/2017.
  */
class ClusteringCoefficient extends Activity {

  def process(input: Serializable, params: Vector[String]): Serializable = {
    val (graph, nodeCount, _) = input.asInstanceOf[(GraphType, Int, Int)]

    var total: Double = 0.0
    val totByNode =
      for (nodeIndex <- 0 until nodeCount) yield {
        val res = getClusteringCoefficient(nodeIndex, graph, nodeCount)
        total += res
        res
      }
    (totByNode, total / (nodeCount - 1))
  }

  private def getClusteringCoefficient(node: Int, graph: GraphType, nodeCount: Int): Double = {
    var connects: Int = 0
    val neighbors = for (i <- 0 until nodeCount if hasConnection(i, node, graph)) yield i
    val degree = neighbors.size
    for {
      i <- 0 until neighbors.size
      j <- i until neighbors.size
      if i != j
      if hasConnection(neighbors(i), neighbors(j), graph)
    } {
      connects += 1
    }
    if (connects == 0 || degree == 0)
      0.0
    else
      (2 * connects).toDouble / (degree * (degree - 1)).toDouble
  }

  private def hasConnection(i: Int, node: Int, graph: GraphType) = graph(i).contains(node)

}
