package reductions

import scala.annotation._
import org.scalameter._
import common._

object ParallelParenthesesBalancingRunner {

  @volatile var seqResult = false

  @volatile var parResult = false

  val standardConfig = config(
    Key.exec.minWarmupRuns -> 40,
    Key.exec.maxWarmupRuns -> 80,
    Key.exec.benchRuns -> 120,
    Key.verbose -> true
  ) withWarmer(new Warmer.Default)

  def main(args: Array[String]): Unit = {
//    val chars = "(sfags(sg)df)((sdf)gadgads)egeg".toCharArray
//    val res1 = ParallelParenthesesBalancing.balance(chars)
//    val res2 = ParallelParenthesesBalancing.parBalance(chars, 4)
//    println(res1, res2)
    val length = 100000000
    val chars = new Array[Char](length)
    val threshold = 10000
    val seqtime = standardConfig measure {
      seqResult = ParallelParenthesesBalancing.balance(chars)
    }
    println(s"sequential result = $seqResult")
    println(s"sequential balancing time: $seqtime ms")

    val fjtime = standardConfig measure {
      parResult = ParallelParenthesesBalancing.parBalance(chars, threshold)
    }
    println(s"parallel result = $parResult")
    println(s"parallel balancing time: $fjtime ms")
    println(s"speedup: ${seqtime / fjtime}")
  }
}

object ParallelParenthesesBalancing {

  /** Returns `true` iff the parentheses in the input `chars` are balanced.
   */
  def balance(chars: Array[Char]): Boolean = {
    def loop(chars: Array[Char], acc: Int): Boolean = {
      if (acc < 0) false
      else if (chars.isEmpty) acc == 0
      else if (chars.head == '(') loop(chars.tail, acc + 1)
      else if (chars.head == ')') loop(chars.tail, acc - 1)
      else loop(chars.tail, acc)
    }
    loop(chars, 0)
  }

  /** Returns `true` iff the parentheses in the input `chars` are balanced.
   */
  def parBalance(chars: Array[Char], threshold: Int): Boolean = {

    def traverse(idx: Int, until: Int, arg1: Int, arg2: Int): (Int, Int) = {
      if (idx == until) return (arg1, arg2)
      else if (chars(idx) == '(') traverse(idx + 1, until, arg1, arg2 + 1)
      else if (chars(idx) == ')' && arg2 == 0) traverse(idx + 1, until, arg1 - 1, arg2)
      else if (chars(idx) == ')' && arg2 > 0) traverse(idx + 1, until, arg1, arg2 - 1)
      else traverse(idx + 1, until, arg1, arg2)
    }

    def reduce(from: Int, until: Int): (Int, Int) = {
      if (until - from <= threshold) traverse(from, until, 0, 0)
      else {
        val median = from + (until - from) / 2
        val ((lArg1, lArg2), (rArg1, rArg2)) = parallel(
          reduce(from, median),
          reduce(median, until))
        (lArg1 + lArg2 + rArg1, rArg2)
      }
    }

    reduce(0, chars.length) == (0, 0)
  }

  // For those who want more:
  // Prove that your reduction operator is associative!

}
