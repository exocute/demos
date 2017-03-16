package demos.compressDemo.activities

import exocute.Activity

/**
  * Created by #GrowinScala
  */
class Mean extends Activity {
  def process(input: java.io.Serializable, params: Vector[String]): java.io.Serializable = {
    val vector = input.asInstanceOf[Vector[Double]]
    vector.sum / vector.size
  }
}