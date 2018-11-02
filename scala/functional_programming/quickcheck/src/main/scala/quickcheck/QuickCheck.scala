package quickcheck

import common._

import org.scalacheck._
import Arbitrary._
import Gen._
import Prop._

abstract class QuickCheckHeap extends Properties("Heap") with IntHeap {

  lazy val genHeap: Gen[H] = oneOf(
    const(empty),
    for (
      i <- arbitrary[A];
      h <- oneOf(const(empty), genHeap)
    ) yield insert(i, h)
  )
  implicit lazy val arbHeap: Arbitrary[H] = Arbitrary(genHeap)

  property("gen1") = forAll { (h: H) =>
    val m = if (isEmpty(h)) 0 else findMin(h)
    findMin(insert(m, h)) == m
  }

  property("gen2") = forAll{ (a: A, b: A) =>
    findMin(insert(a, (insert(b, empty)))) == a.min(b)
  }

  property("gen3") = forAll { (a: A) =>
    isEmpty(deleteMin(insert(a, empty)))
  }

  property("gen4") = forAll { (h: H) =>
    def heapSort(h: H): List[A] = {
      if (isEmpty(h)) Nil
      else findMin(h) :: heapSort(deleteMin(h))
    }
    heapSort(h) == heapSort(h).sorted
  }

  property("gen5") = forAll { (h1: H, h2: H) =>
    (h1 != empty && h2 != empty) ==> (findMin(meld(h1, h2)) == findMin(h1).min(findMin(h2)))
  }

  property("gen6") = forAll { (h1: H, h2: H) =>
    def heapSort(h: H): List[A] = {
      if (isEmpty(h)) Nil
      else findMin(h) :: heapSort(deleteMin(h))
    }
    heapSort(meld(h1, h2)) == (heapSort(h1) ::: heapSort(h2)).sorted
  }

}
