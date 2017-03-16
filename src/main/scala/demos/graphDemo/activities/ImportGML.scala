package demos.graphDemo.activities

import java.io._

import demos.graphDemo.activities.ImportGML.GraphType
import exocute.Activity

import scala.collection.mutable

class ImportGML extends Activity {

  def process(input: java.io.Serializable, params: Vector[String]): java.io.Serializable = {
    val graph = new CreateGraph(input.asInstanceOf[Array[Byte]])
    val output = (graph.graphRep.toMap: GraphType, graph.nodeCount, graph.edgesCount)
    Vector(output, output, output)
  }

  class CreateGraph(val bytes: Array[Byte]) {
    val br = new BufferedReader(new StringReader(new String(bytes)))
    val graphRep = mutable.HashMap[Int, Vector[Int]]()
    var nodeCount = 0
    var edgesCount = 0
    importGraph(br)

    private def addEdge(v1: Int, v2: Int) {
      graphRep.update(v1, graphRep(v1) :+ v2)
      graphRep.update(v2, graphRep(v2) :+ v1)
      edgesCount += 1
    }

    private def addVertex(v: Int) {
      graphRep.update(v, Vector())
      nodeCount += 1
    }

    def importGraph(br: BufferedReader) {
      var i = 0
      while (i < 4) {
        br.readLine
        i += 1
      }
      var line: String = br.readLine.trim
      var line2: String = ""
      while (line.charAt(0) != ']') {
        //processNode
        if (line.charAt(0) == 'n') {
          br.readLine //skip [
          line = br.readLine.trim //id
          addVertex(line.substring(3).toInt)
          if (br.readLine.trim.charAt(0) == 'l') //skip l
            br.readLine
          line = br.readLine.trim
        }
        //process edge
        if (line.charAt(0) == 'e') {
          br.readLine
          line = br.readLine.trim
          line2 = br.readLine.trim
          addEdge(line.substring(7).toInt, line2.substring(7).toInt)
          line = br.readLine.trim
          if (line.trim.charAt(0) == 'l') br.readLine
          line = br.readLine.trim
        }
      }
      br.close()
    }
  }

}

object ImportGML {
  type GraphType = Map[Int, Vector[Int]]
}