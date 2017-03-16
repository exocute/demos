package demos.compressDemo.activities

import exocute.Activity

/**
  * Created by #GrowinScala
  */
class IsKeyFrame extends Activity {
  def process(input: java.io.Serializable, params: Vector[String]): java.io.Serializable = {
    val diffValue = input.asInstanceOf[Double]
    val perc = params(0).toDouble
    diffValue > (perc * (255 * 3) / 100)
  }
}