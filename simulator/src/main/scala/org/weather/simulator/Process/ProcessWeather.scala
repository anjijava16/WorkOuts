package org.weather.simulator.Process

import org.weather.simulator.Data.GetData

object ProcessWeather {
  def main(args: Array[String]) {

    //args - 3 arguments are passes. args(0) - Year, args(1) - month, args (2) - day f month

    //Get the cities to perform forecast for. It also acquires the bom observation file name for the city and
    //area Codes. As these are fixed & won't change, they are hard coded in the ConstData Object.
    val cityToCodeMapping = GetData.getMappingData

    //Using the input year & month argument, for the date and validate that is between give range.
    val date = ProcessUtil.getDate(args(0), args(1)).toInt

    //Initial processing to download the bom (Bureau of Meteorology) 
    //observation data for the given date and the cities in scope
    ProcessUtil.downlaodObservationData(cityToCodeMapping, date)

    //Extract the observations from the download data.
    val processedValue = ProcessUtil.filterForDay(cityToCodeMapping, date, args(2).toInt)

  }

}