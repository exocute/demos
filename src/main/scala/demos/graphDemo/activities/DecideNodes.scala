package demos.graphDemo.activities

import java.io.Serializable

import exocute.Activity

/**
  * Created by #ScalaTeam on 06/03/2017.
  */
class DecideNodes extends Activity {
  def process(input: Serializable, params: Vector[String]): Serializable = {
    val Vector((cc, _), (bc, _), _) = input.asInstanceOf[Vector[(Seq[Double], Int)]]

    val res = cc.zip(bc).map(elem => elem._1 + elem._2).toList
    val nodes = params.head.toInt
    println(res.zipWithIndex.mkString(", "))
    res.zipWithIndex.sortBy(-_._1).take(nodes)
  }
}
