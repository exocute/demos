package demos.swaveToExocuteDemo

import demos.swaveToExocuteDemo.SwaveSpoutTesterUtil._
import swave.core.Spout

/**
  * Created by #GrowinScala
  */
object SwaveSpoutTester2 {

  def main(args: Array[String]): Unit = {
    def spoutGenerator = Spout(1, 2, 3, 4, 5, 6, 7).map(_ * 5).filter(_ > 10).fanOutBroadcast()
      .sub.map(x => x * 2).end
      .sub.map(x => x * 3).end
      .fanInToTuple

    SwaveSpoutTesterUtil.test(spoutGenerator)
  }

}
