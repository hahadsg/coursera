package observatory

import com.sksamuel.scrimage.{Image, Pixel}

/**
  * 2nd milestone: basic visualization
  */
object Visualization {

  val radius = 6371d // the sphere radius
  val pValue = 2d // inverse distance weight p value

  def distance(a: Location, b: Location): Double = {
    val delta_sigma =
      if (a == b) 0
      else if (a.lat == -b.lat && math.abs(a.lon - b.lon) == 180)
        math.Pi
      else {
        math.acos(
          math.sin(math.toRadians(a.lat)) * math.sin(math.toRadians(b.lat)) +
          math.cos(math.toRadians(a.lat)) * math.cos(math.toRadians(b.lat))
            * math.cos(math.toRadians(a.lon) - math.toRadians(b.lon)))
      }
    radius * delta_sigma
  }

  /**
    * @param temperatures Known temperatures: pairs containing a location and the temperature at this location
    * @param location Location where to predict the temperature
    * @return The predicted temperature at `location`
    */
  def predictTemperature(temperatures: Iterable[(Location, Temperature)], location: Location): Temperature = {
    val distanceTemp = temperatures
      .map{ case (loc, temp) => (distance(location, loc), temp) }
    val minDistanceTemp = distanceTemp.toList.sortWith(_._1 < _._1).head
    if (minDistanceTemp._1 >= 1) {
      val tempPair = distanceTemp
        .map{ case (dist, temp) => (temp, 1d / math.pow(dist, pValue)) }
        .map{ case (temp, w) => (w * temp, w) }
        .reduce((a, p) => (a._1 + p._1, a._2 + p._2))
      tempPair._1 / tempPair._2
    } else {
      // too close
      minDistanceTemp._2
    }
  }

  def linearInterpolation(la: Double, ua: Double, lb: Int, ub: Int, v: Double): Int = {
    ((v - la) / (ua - la) * (ub - lb) + lb).round.toInt
  }

  /**
    * @param points Pairs containing a value and its associated color
    * @param value The value to interpolate
    * @return The color that corresponds to `value`, according to the color scale defined by `points`
    */
  def interpolateColor(points: Iterable[(Temperature, Color)], value: Temperature): Color = {
    val sortedPoints: List[(Temperature, Color)] = points.toList.sortWith(_._1 > _._1)
    val temps: List[Temperature] = sortedPoints.map(_._1)
    val colors: List[Color] = sortedPoints.map(_._2)
    val leftSec = sortedPoints ::: List((Double.MinValue, colors.last))
    val rightSec = (Double.MaxValue, colors.head) :: sortedPoints
    val secPair: List[((Temperature, Color), (Temperature, Color))] =
      leftSec.zip(rightSec)
    val ((lowerTemp, lowerColor), (upperTemp, upperColor)) = secPair
      .filter(p => value >= p._1._1 && value < p._2._1)
      .head
    Color(
      linearInterpolation(lowerTemp, upperTemp, lowerColor.red, upperColor.red, value),
      linearInterpolation(lowerTemp, upperTemp, lowerColor.green, upperColor.green, value),
      linearInterpolation(lowerTemp, upperTemp, lowerColor.blue, upperColor.blue, value)
    )
  }

  val imageWidth = 360
  val imageHeight = 180

  /**
    * @param temperatures Known temperatures
    * @param colors Color scale
    * @return A 360×180 image where each pixel shows the predicted temperature at its location
    */
  def visualize(temperatures: Iterable[(Location, Temperature)], colors: Iterable[(Temperature, Color)]): Image = {
    val imageColors = temperatures.map{ case (loc, temp) => (loc, interpolateColor(colors, temp)) }
    val coords = for (
      i <- 0 until imageHeight;
      j <- 0 until imageWidth
    ) yield (i, j)
    val pixels = coords
      .map(coord => Location(-coord._1 + imageHeight / 2, coord._2 - imageWidth / 2))
      .map(predictTemperature(temperatures, _))
      .map(interpolateColor(colors, _))
      .map(color => Pixel(color.red, color.green, color.blue, 255))
      .toArray
    Image(imageWidth, imageHeight, pixels)
  }

}

