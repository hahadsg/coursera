package calculator

object Polynomial {
  def computeDelta(a: Signal[Double], b: Signal[Double],
      c: Signal[Double]): Signal[Double] = {
    Signal(b() * b() - 4 * a() * c())
  }

  def computeSolutions(a: Signal[Double], b: Signal[Double],
      c: Signal[Double], delta: Signal[Double]): Signal[Set[Double]] = {
//    val sqrt_delta = Signal(math.sqrt(computeDelta(a, b, c)()))
//    Signal(Set(sqrt_delta(), -sqrt_delta()).map(x => (x - b()) / 2 / a()))
    Signal {
      Set(computeDelta(a, b, c)())
        .filter(x => x >= 0)
        .map(x => math.sqrt(x))
        .flatMap(x => Set(x, -x))
        .map(x => (x - b()) / 2 / a())
    }
  }
}
