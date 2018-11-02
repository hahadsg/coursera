package streams

object Main extends App {
  // abstract class Test extends Solver with StringParserTerrain 

  object Test0 extends GameDef with Solver with StringParserTerrain {
  val level =
      """ooo-------
        |oSoooo----
        |ooooooooo-
        |-ooooooooo
        |-----ooToo
        |------ooo-""".stripMargin

//    val pos = new Pos(1, 1)
  }

  println(Test0.solution)
}