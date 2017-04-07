package org.weather.simulator.Data

import scala.io.Codec

object ConstData {

  val fileCodec = Codec("windows-1252")
  val siteUrl : String = "http://www.bom.gov.au/climate/dwo/"

  val defaultFieldSep = "\\|" //psv field separator when reading & splitting.
  val defaultWriteSep = "|" //pipe separator when writing 
  val bomFileSep = "," //Field separator for bom observations data.

  //Date range where the bom data is available.
  val defaultDateRange = "201602|201703"

  //Case classes to hold related data together.
  case class codes(areaCode: String, fileCode: String)
  case class dateRange(min: Int, max: Int) //Max & Min date ranges.

  //Fileds used in the forecast. 
  case class fields(date: String, //Field 1
                    min_temp: Double, //Field 2
                    max_temp: Double, //Field 3
                    rainfall: Double, //Field 4
                    sunshine: Double, //Field 6
                    relative_humidity: Integer, //Field 11
                    cloud_amount: Integer, //Field 12
                    pressure: Double) //Field 15

//Array of AREA|AREACODE|FILECODE

  val cities = Array(
    "CANBERRA|CBR|IDCJDW2801",
    "SYDNEY|SYD|IDCJDW2124",
    "WOLLONGONG|WOL|IDCJDW2146",
    "MELBOURNE|MEL|IDCJDW3033",
    "BRISBANE|BNE|IDCJDW4019",
    "CAIRNS|CNS|IDCJDW4024",
    "GOLD COAST|OOL|IDCJDW4050",
    "ADELAIDE|ADL|IDCJDW5002",
    "PERTH|PER|IDCJDW6111",
    "HOBART|HBA|IDCJDW7021",
    "DARWIN|DRW|IDCJDW8014")

}