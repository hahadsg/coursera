package observatory

import com.sksamuel.scrimage.{Image, Pixel}

/**
  * 3rd milestone: interactive visualization
  */
object Interaction {

  val zoomLevel = 8
  val imageWidth: Int = math.pow(2, zoomLevel).toInt
  val imageHeight: Int = math.pow(2, zoomLevel).toInt
  val pixelAlpha = 127

  /**
    * @param tile Tile coordinates
    * @return The latitude and longitude of the top-left corner of the tile, as per http://wiki.openstreetmap.org/wiki/Slippy_map_tilenames
    */
  def tileLocation(tile: Tile): Location = {
    val lon = tile.x.toDouble / (1 << tile.zoom) * 360d - 180d
    val lat = math.toDegrees(math.atan(math.sinh(
      math.Pi * (1.0 - 2.0 * tile.y.toDouble / (1 << tile.zoom))
    )))
    Location(lat, lon)
  }

  /**
    * @param temperatures Known temperatures
    * @param colors Color scale
    * @param tile Tile coordinates
    * @return A 256×256 image showing the contents of the given tile
    */
  def tile(temperatures: Iterable[(Location, Temperature)], colors: Iterable[(Temperature, Color)], tile: Tile): Image = {
    val coords = for (
      i <- 0 until imageWidth;
      j <- 0 until imageHeight
    ) yield (i, j)
    val baseX = tile.x * (1 << zoomLevel)
    val baseY = tile.y * (1 << zoomLevel)
    val imageArray = coords.par
      .map{ case (i, j) => Tile(baseX + j, baseY + i, tile.zoom + zoomLevel) }
      .map(tileLocation)
      .map(Visualization.predictTemperature(temperatures, _))
      .map(Visualization.interpolateColor(colors, _))
      .map(color => Pixel(color.red, color.green, color.blue, pixelAlpha))
      .toArray
    Image(imageWidth, imageHeight, imageArray)
  }

  /**
    * Generates all the tiles for zoom levels 0 to 3 (included), for all the given years.
    * @param yearlyData Sequence of (year, data), where `data` is some data associated with
    *                   `year`. The type of `data` can be anything.
    * @param generateImage Function that generates an image given a year, a zoom level, the x and
    *                      y coordinates of the tile and the data to build the image from
    */
  def generateTiles[Data](
    yearlyData: Iterable[(Year, Data)],
    generateImage: (Year, Tile, Data) => Unit
  ): Unit = {
    for (
      zoom <- 0 to 3;
      i <- 0 until (1 << zoom);
      j <- 0 until (1 << zoom);
      yearly <- yearlyData
    ) yield generateImage(yearly._1, Tile(i, j, zoom), yearly._2)
  }

}
