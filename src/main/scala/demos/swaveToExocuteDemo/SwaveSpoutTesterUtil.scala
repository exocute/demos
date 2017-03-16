package demos.swaveToExocuteDemo

import java.io.Serializable

import swave.core.{Spout, StreamEnv}
import toolkit.converters.SwaveToExoGraph._

import scala.language.implicitConversions

/**
  * Created by #ScalaTeam on 07-03-2017.
  */
object SwaveSpoutTesterUtil {

  def test(spoutGenerator: => Spout[Serializable]): Unit = {
    implicit val env = StreamEnv()
    implicit val gl = scala.concurrent.ExecutionContext.Implicits.global

    val swaveSpout = spoutGenerator
    val swaveSpoutToConvert = spoutGenerator

    val future = swaveSpout.drainToVector(999)

    val exoResult: Vector[Serializable] = swaveSpoutToConvert.toExoGraph.result

    val futureResult = future.map {
      swaveResult =>
        println(s"Result from exocute: $exoResult")
        println(s"Result from swave:   $swaveResult")

        print("Result are ")
        println(if (exoResult == swaveResult) "correct" else "incorrect")
        System.console().flush()
    }

    env.shutdownOn(futureResult)
  }

  implicit def convert(spout: Spout[AnyVal]): Spout[java.io.Serializable] = spout.asInstanceOf[Spout[java.io.Serializable]]

  implicit def convert(v: AnyVal): java.io.Serializable = v.asInstanceOf[java.io.Serializable]

}
