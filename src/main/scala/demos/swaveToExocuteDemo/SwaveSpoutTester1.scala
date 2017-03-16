package demos.swaveToExocuteDemo

import demos.swaveToExocuteDemo.SwaveSpoutTesterUtil._
import swave.core.Spout

/**
  * Created by #GrowinScala
  */
object SwaveSpoutTester1 {

  def main(args: Array[String]): Unit = {
    implicit val gl = scala.concurrent.ExecutionContext.Implicits.global

    def spoutGenerator = Spout(1, 2, 3, 4, 5).map(_ * 5).filter(_ >= 10)

    SwaveSpoutTesterUtil.test(spoutGenerator)
  }

}
