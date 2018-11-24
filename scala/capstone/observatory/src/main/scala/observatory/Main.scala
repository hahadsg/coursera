package observatory

object Main extends App {

  val locateTemp = Extraction.locateTemperatures(1975, "/stations.csv", "/1975.csv")
}
