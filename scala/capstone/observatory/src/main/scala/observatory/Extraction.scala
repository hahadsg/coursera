package observatory

import java.nio.file.Paths
import java.time.LocalDate

import scala.io.Source

/**
  * 1st milestone: data extraction
  */
object Extraction {

  /**
    * @param year             Year number
    * @param stationsFile     Path of the stations resource file to use (e.g. "/stations.csv")
    * @param temperaturesFile Path of the temperatures resource file to use (e.g. "/1975.csv")
    * @return A sequence containing triplets (date, location, temperature)
    */
  def locateTemperatures(year: Year, stationsFile: String, temperaturesFile: String):
    Iterable[(LocalDate, Location, Temperature)] = {
    val stationsFilePath = Paths.get(getClass.getResource(stationsFile).toURI).toString
    val temperaturesFilePath = Paths.get(getClass.getResource(temperaturesFile).toURI).toString
    val stationsMap: Map[StationId, Location] = (
        for (line <- Source.fromFile(stationsFilePath).getLines)
          yield line.split(",", -1))
      .filter(row => row(2) != "" && row(3) != "")
      .map(row => (StationId(row(0), row(1)), Location(row(2).toDouble, row(3).toDouble)))
      .toMap

    (
      for (line <- Source.fromFile(temperaturesFilePath).getLines) yield {
        val row = line.split(",", -1)
        val statId = StationId(row(0), row(1))
        val lDate = LocalDate.of(year, row(2).toInt, row(3).toInt)
        val temper: observatory.Temperature = (row(4).toDouble - 32) * 5d / 9d
        val loc = stationsMap.get(statId)
        (lDate, loc, temper)
    })
    .flatMap{ case (lDate, loc, temper) =>
      loc match {
        case Some(x) => List((lDate, x, temper))
        case _ => List()
      }
    }
    .toList
  }

  /**
    * @param records A sequence containing triplets (date, location, temperature)
    * @return A sequence containing, for each location, the average temperature over the year.
    */
  def locationYearlyAverageRecords(records: Iterable[(LocalDate, Location, Temperature)]):
    Iterable[(Location, Temperature)] = {
    records.map(r => (r._2, r._3))
      .groupBy(_._1)
      .mapValues(l => l.map(_._2).sum / l.size)
      .toList
  }

}
