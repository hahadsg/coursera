package recfun

object Main {
  def main(args: Array[String]) {
    println("Pascal's Triangle")
    for (row <- 0 to 10) {
      for (col <- 0 to row)
        print(pascal(col, row) + " ")
      println()
    }
  }

  /**
   * Exercise 1
   */
    def pascal(c: Int, r: Int): Int = {
      if (c < 0 || r < 0 || c > r) 0
      else if (c == 0 || c == r) 1
      else pascal(c, r - 1) + pascal(c - 1, r - 1)
    }
  
  /**
   * Exercise 2
   */
    def balance(chars: List[Char]): Boolean = {
      def loop(flag: Int, chars: List[Char]): Boolean = {
        if (chars.isEmpty) flag == 0
        else if (chars.head == '(') loop(flag + 1, chars.tail)
        else if (chars.head == ')') flag > 0 && loop(flag - 1, chars.tail)
        else loop(flag, chars.tail)
      }
      loop(0, chars)
    }
  
  /**
   * Exercise 3
   */
    def countChange(money: Int, coins: List[Int]): Int = {
      if (money == 0) 1
      else if (coins.isEmpty) 0
      else {
        var cnt: Int = 0
        var cur_coin: Int = coins.head
        for (consume_money <- (0 to money).by(cur_coin))
          cnt += countChange(money - consume_money, coins.tail)
        cnt
      }
    }
  }
