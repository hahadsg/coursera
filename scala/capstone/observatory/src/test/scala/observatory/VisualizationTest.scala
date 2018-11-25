package observatory


import org.scalatest.FunSuite
import org.scalatest.prop.Checkers

trait VisualizationTest extends FunSuite with Checkers {

  val points = List(
    (32d, Color(255,0,0)),
    (12d, Color(255,255,0)),
    (0d, Color(0,255,255)),
    (-15d, Color(0,0,255)),
    (-27d, Color(255,0,255)),
    (-50d, Color(33,255,107)),
    (-60d, Color(0,0,0)),
    (60d, Color(255,255,255))
  )

  test("interpolateColor: Temperature -12") {
    val value = -12d
    val res = Visualization.interpolateColor(points, value)
    val ans = Color(0, 51, 255)
    assert(res === ans)
  }
  test("interpolateColor: Temperature 70") {
    val value = 70d
    val res = Visualization.interpolateColor(points, value)
    val ans = Color(255, 255, 255)
    assert(res === ans)
  }
  test("interpolateColor: Temperature -70") {
    val value = -70d
    val res = Visualization.interpolateColor(points, value)
    val ans = Color(0, 0, 0)
    assert(res === ans)
  }
}
