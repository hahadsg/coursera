package observatory

import com.sksamuel.scrimage.{Image, Pixel}

/**
  * 5th milestone: value-added information visualization
  */
object Visualization2 {

  val zoomLevel = 8
  val imageWidth: Int = math.pow(2, zoomLevel).toInt
  val imageHeight: Int = math.pow(2, zoomLevel).toInt
  val pixelAlpha = 127

  /**
    * @param point (x, y) coordinates of a point in the grid cell
    * @param d00 Top-left value
    * @param d01 Bottom-left value
    * @param d10 Top-right value
    * @param d11 Bottom-right value
    * @return A guess of the value at (x, y) based on the four known values, using bilinear interpolation
    *         See https://en.wikipedia.org/wiki/Bilinear_interpolation#Unit_Square
    */
  def bilinearInterpolation(
    point: CellPoint,
    d00: Temperature,
    d01: Temperature,
    d10: Temperature,
    d11: Temperature
  ): Temperature = {
    val CellPoint(x, y) = point
    val dTop = (1 - x) * d00 + x * d10
    val dDown = (1 - x) * d01 + x * d11
    (1 - y) * dTop + y * dDown
  }

  def locationTemperature(loc: Location, grid: GridLocation => Temperature): Temperature = {
    val x0 = loc.lat.toInt
    val y0 = loc.lon.toInt
    val x1 = x0 + 1
    val y1 = y0 + 1
    val point = CellPoint(loc.lat - x0, loc.lon - y0)
    val d00 = grid(GridLocation(x0, y0))
    val d01 = grid(GridLocation(x0, y1))
    val d10 = grid(GridLocation(x1, y0))
    val d11 = grid(GridLocation(x1, y1))
    bilinearInterpolation(point, d00, d01, d10, d11)
  }

  /**
    * @param grid Grid to visualize
    * @param colors Color scale to use
    * @param tile Tile coordinates to visualize
    * @return The image of the tile at (x, y, zoom) showing the grid using the given color scale
    */
  def visualizeGrid(
    grid: GridLocation => Temperature,
    colors: Iterable[(Temperature, Color)],
    tile: Tile
  ): Image = {
    val coords = for {
      j <- 0 until imageHeight
      i <- 0 until imageWidth
    } yield (j, i)

    val baseX = tile.x << zoomLevel
    val baseY = tile.y << zoomLevel

    val pixels = coords.par
      .map{ case (j, i) => Tile(baseX + i, baseY + j, tile.zoom + zoomLevel) }
      .map(Interaction.tileLocation)
      .map(locationTemperature(_, grid))
      .map(Visualization.interpolateColor(colors, _))
      .map(color => Pixel(color.red, color.green, color.blue, pixelAlpha))
      .toArray
    
    Image(imageWidth, imageHeight, pixels)
  }

}
