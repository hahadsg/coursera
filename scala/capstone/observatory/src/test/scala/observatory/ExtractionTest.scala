package observatory

import java.time.LocalDate

import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner

trait ExtractionTest extends FunSuite {

  test("compute yearly average by location") {
    val yearlyrecords = List(
      (LocalDate.of(1992, 6, 25), Location(1.0,-1.0), 15.0),
      (LocalDate.of(1992, 6, 25), Location(1.0,-1.0), 15.0),
      (LocalDate.of(1992, 6, 25), Location(2.0,-2.0), 10.0)
    )
    val avgRecords = Extraction.locationYearlyAverageRecords(yearlyrecords)
    val ans = List(
      (Location(1.0,-1.0),15.0),
      (Location(2.0,-2.0),10.0)
    )
    assert(avgRecords === ans)
  }

  test("locateTemperatures") {
    val locateTemp = Extraction.locateTemperatures(1975, "/stations.csv", "/1975.csv")
    assert(locateTemp.size === 2177190)
  }
  
}