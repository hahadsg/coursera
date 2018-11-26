package observatory

/**
  * 4th milestone: value-added information
  */
object Manipulation {

  class Grid(temperature: Iterable[(Location, Temperature)]) {
//    private var tempMap: Map[GridLocation, Temperature] = temperature
//      .map{ case (loc, temp) => (GridLocation(loc.lat.toInt, loc.lon.toInt), (loc, temp)) }
//      .groupBy(_._1)
//      .map{ case (gridLoc, iter) =>
//        (gridLoc, Visualization.predictTemperature(iter.map(_._2), Location(gridLoc.lat, gridLoc.lon)))}
    private var tempMap: Map[GridLocation, Temperature] = {
      (
        for {
          lat <- 90 until -90 by -1
          lon <- -180 until 180
        } yield (
          GridLocation(lat, lon),
          Visualization.predictTemperature(temperature, Location(lat, lon)))
      ).toMap
    }

    def get(gridLoc: GridLocation): Temperature = tempMap.getOrElse(gridLoc, 0d)

    def +=(that: Grid): this.type = {
      tempMap = that.tempMap.map{ case (gridLoc, temp) =>
        gridLoc -> (tempMap(gridLoc) + temp)
      }
      this
    }

    def /=(d: Double): this.type = {
      tempMap = tempMap.mapValues(_ / d)
      this
    }

    def -=(that: GridLocation => Temperature): this.type = {
      tempMap = tempMap.map{ case (gridLoc, temp) =>
        gridLoc -> (tempMap(gridLoc) - that(gridLoc))
      }
      this
    }
  }

  /**
    * @param temperatures Known temperatures
    * @return A function that, given a latitude in [-89, 90] and a longitude in [-180, 179],
    *         returns the predicted temperature at this location
    */
  def makeGrid(temperatures: Iterable[(Location, Temperature)]): GridLocation => Temperature = {
    val grid = new Grid(temperatures)
    grid.get
  }

  /**
    * @param temperaturess Sequence of known temperatures over the years (each element of the collection
    *                      is a collection of pairs of location and temperature)
    * @return A function that, given a latitude and a longitude, returns the average temperature at this location
    */
  def average(temperaturess: Iterable[Iterable[(Location, Temperature)]]): GridLocation => Temperature = {
    val grid = temperaturess.par
      .map(new Grid(_))
      .reduce((a, b) => a += b)
    grid /= temperaturess.size
    grid.get
  }

  /**
    * @param temperatures Known temperatures
    * @param normals A grid containing the “normal” temperatures
    * @return A grid containing the deviations compared to the normal temperatures
    */
  def deviation(temperatures: Iterable[(Location, Temperature)], normals: GridLocation => Temperature): GridLocation => Temperature = {
    val grid = new Grid(temperatures)
    grid -= normals
    grid.get
  }


}

