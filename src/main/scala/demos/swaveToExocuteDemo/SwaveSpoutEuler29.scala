package demos.swaveToExocuteDemo

import swave.core.Spout

/**
  * Created by #GrowinScala
  *
  * Solution to problem 29 of projecteuler: <br>
  * https://projecteuler.net/problem=29
  */
object SwaveSpoutEuler29 {

  def main(args: Array[String]): Unit = {
    def spoutGenerator =
      Spout(List(100))
        .flatMap { n => for (a <- 2 to n; b <- 2 to n) yield (a, b) }
        .map { case (a, b) => BigInt(a).pow(b) }

    SwaveSpoutTesterUtil.testSwave(spoutGenerator, _.distinct.size)
  }

}
