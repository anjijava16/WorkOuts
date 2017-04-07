package org.weather.simulator.Process

import org.weather.simulator.Data.GetData
import org.weather.simulator.Data.ConstData
import scala.io.Source
import java.io.PrintWriter
import java.io.File
import java.io.PrintWriter
import java.io.FileNotFoundException
import org.joda.time.{ DateTime, DateTimeComparator, DateTimeZone }
import org.joda.time.Period
import org.joda.time.format.DateTimeFormat

object ProcessUtil {

  private def filePath(implicit bomCode: String) = bomCode
  private val dataPattern = "^,[0-9]{4}-[0-9]{2}-[0-9]{1,2},".r
  /*
   * From the input year and month, construct the date format as (year + month) which is required to download
   * observations from bom and return date as Int.
   * 
   */
  def getDate(year: String, month: String) = {

    require((year + month).forall(_.isDigit) && //Check all are digits in date
      (1 to 12).contains(month.toInt),
      "Year or Month is not numeric or month is not in valid range") //Check month is between 1 & 12

    val date = "%04d".format(year.toInt) + "%02d".format(month.toInt)

    val dateRange = GetData.getMaxMinDate(ConstData.defaultDateRange)

    //Check if the date is between date ranges specified
    require(date.toInt >= dateRange.min && date.toInt <= dateRange.max,
      s"Input date is not between min ($dateRange.min) & max($dateRange.max) range")

    println(s"Date Sucessfully parsed & validated. $date")

    date
  }
  
  def predictOutlook(RH: Int, cloudCover: Int, sunShine: Double, averageTemp: Double) = {
    //Rain. When highly humid, high cloud cover and less sunshine.
    if (RH > 80 && cloudCover > 6 && sunShine < 3) "Rain" 
    else if (sunShine > 5) "Sunny" //When sunshine is for more than 5 hours then, Sunny.
    else if (cloudCover > 6) "Cloudy" //When high cloud cover then cloudy.
    else if (averageTemp < 15) "Cold" //If temperature is below 15. Then Cold.
    else "Partly Cloudy" //If not everything else, then "Partly Cloudy"

  }

   /*
   * Determines the t+1th day for which the forecast is being performed. Here, n is the max date in the observations.
   * The date is converted from Australia Date to UTC format as the time is suffixed by 'Z' in the sampled output file.
   */
  def calculateDateTime(dateTime : String) = {
    dateTime.split('-')
      .map(t => new DateTime(t(0).toInt, t(1).toInt, t(2).toInt, 12, 0, 0, 0))
      .head.withZone(DateTimeZone.UTC)
      .toString(DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss'Z"))
  }
  def downlaodObservationData(cityToCodeMapping: Map[String, ConstData.codes], date: Int) = {

    for (city <- cityToCodeMapping) {
      GetData.fileDownloader(date, city._2)
      val cityName = city._1
      println(s"$cityName -> Sucessfully downloaded observations !!")
    }
  }

  /*
   * From the file read with below structure. It extracts the required fileds.
   * The files are defined in trait CommonData.fields
   * 
   * Structure of file downloaded from bom.
   * (,0)
   * ("Date",1)
   * ("Minimum temperature (°C)",2)
   * ("Maximum temperature (°C)",3)
   * ("Rainfall (mm)",4)
   * ("Evaporation (mm)",5)
   * ("Sunshine (hours)",6)
   * ("Direction of maximum wind gust ",7)
   * ("Speed of maximum wind gust (km/h)",8)
   * ("Time of maximum wind gust",9)
   * ("9am Temperature (°C)",10)
   * ("9am relative humidity (%)",11)
   * ("9am cloud amount (oktas)",12)
   * ("9am wind direction",13)
   * ("9am wind speed (km/h)",14)
   * ("9am MSL pressure (hPa)",15)
   * ("3pm Temperature (°C)",16)
   * ("3pm relative humidity (%)",17)
   * ("3pm cloud amount (oktas)",18)
   * ("3pm wind direction",19)
   * ("3pm wind speed (km/h)",20)
   * ("3pm MSL pressure (hPa)",21)
   * 
   */
  def getBomObservations(implicit bomCode: String) = {

    try {
      Source.fromFile(filePath)(ConstData.fileCodec).getLines
      .filter(x => dataPattern.findFirstIn(x).isDefined)
        .map(x => {
          val y = x.split(ConstData.bomFileSep)
          //If values missing, the assume zero.
          ConstData.fields(y(1),
            if (y(2) == "") 0.0D else y(2).toDouble,
            if (y(3) == "") 0.0D else y(3).toDouble,
            if (y(4) == "") 0.0D else y(4).toDouble,
            if (y(6) == "") 0.0D else y(6).toDouble,
            if (y(11) == "") 0 else y(11).toInt,
            if (y(12) == "") 4 else y(12).toInt + 1,
            if (y(15) == "") 0.0D else y(15).toDouble)

        }).toList

    } catch {
      case ex: FileNotFoundException => throw new FileNotFoundException(ex.getMessage
        + ". Data source is not bom and files not found in the current folder")
    }
  }
  def filterForDay(cityToCodeMapping: Map[String, ConstData.codes],dateFilter: Int, dayOfMonth: Int) = {
    val filteredList = scala.collection.mutable.MutableList[String]()
    for (city <- cityToCodeMapping) {
      val listOfrecords = getBomObservations(city._2.fileCode)
      val geoCordinates = GetData.getLatLong(city._1)
      val filteredRecord =  (listOfrecords(dayOfMonth))
      val conditions = predictOutlook(filteredRecord.relative_humidity, filteredRecord.cloud_amount, filteredRecord.sunshine, (filteredRecord.max_temp + filteredRecord.min_temp )/2)
      val dateInUTC = (filteredRecord.date)+("T09:00:00Z") //Data is captured for 9AM in BOM

      filteredList += (city._1+'|'+geoCordinates+'|'+dateInUTC+'|'+conditions+'|'+filteredRecord.max_temp+'|'+filteredRecord.min_temp +'|'+filteredRecord.pressure+'|'+filteredRecord.relative_humidity).toString
    }
    
    println("Location|Position|date|Conditions|Maximum Temperature|minimum Temperature|Pressure|Humidity")
    
    val title : String = "Location|Position|date|Conditions|Maximum Temperature|minimum Temperature|Pressure|Humidity"
    val allRecordsString = filteredList
    val printFile : String = title + "\n" + allRecordsString.toString
    println(printFile)
  }

}