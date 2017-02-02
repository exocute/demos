package demos.compressDemo.activities

import exocute.Activity

/**
  * Created by #ScalaTeam on 25-01-2017.
  */
class Mean extends Activity {
  def process(input: java.io.Serializable, params: Vector[String]): java.io.Serializable = {
    val vector = input.asInstanceOf[Vector[Double]]
    vector.sum / vector.size
  }
}