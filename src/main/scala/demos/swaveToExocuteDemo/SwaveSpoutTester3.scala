package demos.swaveToExocuteDemo

import swave.core.Spout

/**
  * Created by #ScalaTeam on 07-03-2017.
  */
object SwaveSpoutTester3 {

  def main(args: Array[String]): Unit = {
    def spoutGenerator = Spout.fromIterable(List(1, 2, 3)).fanOutBroadcast()
      .sub.map(x => -x).end
      .sub.map(SwaveSpoutTester3_aux.f).end
      .fanInToTuple

    SwaveSpoutTesterUtil.test(spoutGenerator)
  }

}
