package demos.graphDemo.activities

import java.io.Serializable

import demos.graphDemo.activities.ImportGML.GraphType
import exocute.Activity

import scala.collection.mutable

/**
  * Created by #GrowinScala
  */
class BetweenessCentrality extends Activity {

  def process(input: Serializable, params: Vector[String]): Serializable = {
    val (graph, _, _) = input.asInstanceOf[(GraphType, Int, Int)]

    val cb = calculateBetweenessCentrality(graph)
    val list = cb.toList.sortBy(_._1).unzip._2.map(_ / 2)
    (list, list.sum / list.size)
  }

  private def calculateBetweenessCentrality(graph: GraphType): Map[Int, Double] = {
    val cb = mutable.HashMap[Int, Double]()

    for (v <- graph.keySet)
      cb.put(v, 0.0)

    for (s <- graph.keySet) {
      val S = mutable.Stack[Int]()
      val P = mutable.HashMap[Int, Vector[Int]]()
      val sigma = mutable.HashMap[Int, Int]()
      val d = mutable.HashMap[Int, Int]()
      val Q = mutable.Queue[Int]()
      for (t <- graph.keySet) {
        P.update(t, Vector())
        sigma.update(t, 0)
        d.update(t, -1)
      }
      sigma.update(s, 1)
      d.update(s, 0)
      Q.enqueue(s)
      while (Q.nonEmpty) {
        val v: Int = Q.dequeue()
        S.push(v)
        for (w <- graph(v)) {
          if (d(w) < 0) {
            Q.enqueue(w)
            d.update(w, d(v) + 1)
          }
          if (d(w) == d(v) + 1) {
            sigma.update(w, sigma(w) + sigma(v))
            P.update(w, P(w) :+ v)
          }
        }
      }
      val delta = mutable.HashMap[Int, Double]()
      for (t <- graph.keySet)
        delta.update(t, 0.0)
      while (S.nonEmpty) {
        val w: Int = S.pop()
        for (v <- P(w)) {
          delta.update(v,
            delta(v) +
              (sigma(v).toDouble / sigma(w).toDouble) * (1.0 + delta(w)))
        }
        if (w != s) {
          cb.update(w, cb(w) + delta(w))
        }
      }
    }

    cb.toMap
  }
}
