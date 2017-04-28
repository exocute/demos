package demos.swaveToExocuteDemo

import java.io.Serializable

import swave.core.{Spout, StreamEnv}
import toolkit.converters.SwaveToExoGraph._

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.language.implicitConversions

/**
  * Created by #GrowinScala
  */
object SwaveSpoutTesterUtil {

  def testSwave(spoutGenerator: => Spout[Serializable]): Unit = {
    testSwave(spoutGenerator, x => x)
  }

  def testSwave[T](spoutGenerator: => Spout[Serializable], joinFunction: Vector[Serializable] => T): Unit = {
    implicit val env = StreamEnv()
    implicit val gl = scala.concurrent.ExecutionContext.Implicits.global

    val swaveSpout, swaveSpoutToConvert = spoutGenerator

    val future: Future[T] = swaveSpout.drainToVector(Int.MaxValue).map(joinFunction)

    val exoResult: T = joinFunction(swaveSpoutToConvert.toExoGraph.result)

    val futureResult = future.map {
      swaveResult =>
        println(s"Result from exocute: $exoResult")
        println(s"Result from swave:   $swaveResult")

        print("Result are ")
        println(if (exoResult == swaveResult) "correct" else "incorrect")
    }

    env.shutdownOn(futureResult)

    Await.result(futureResult, 1.hour)
  }

  implicit def convert(spout: Spout[AnyVal]): Spout[java.io.Serializable] = spout.asInstanceOf[Spout[java.io.Serializable]]

  implicit def convert(v: AnyVal): java.io.Serializable = v.asInstanceOf[java.io.Serializable]

}
