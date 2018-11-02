import patmat.Huffman._

object Main extends App {
  // val leaflist = List(Leaf('e', 1), Leaf('t', 2), Leaf('x', 4), Leaf('s', 7))
  // println(combine(leaflist))
  println(createCodeTree("test".toList))
}