package demos.swaveToExocuteDemo

import demos.swaveToExocuteDemo.SwaveSpoutTesterUtil._
import swave.core.Spout

/**
  * Created by #GrowinScala
  *
  * Solution to problem 38 of projecteuler: <br>
  * https://projecteuler.net/problem=38
  */
object SwaveSpoutEuler38 {

  def main(args: Array[String]): Unit = {
    def spoutGenerator =
      Spout(List(10000))
        .flatMap { n => for (number <- 2 to n) yield number }
        .flatMap { number =>
          for {
            n <- 2 to 9
            multiples = (1 to n).map(_ * number).mkString
            if multiples.length == 9 && multiples.sorted == "123456789"
          } yield multiples.toInt
        }

    SwaveSpoutTesterUtil.testSwave(spoutGenerator, _.asInstanceOf[Seq[Int]].max)
  }

}
