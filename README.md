# Weather Data Simulator
This scala application is designed to emit the weather for the pre-specified 10 locations in Australia using the data fetched from the Bureau of Meteorology, Commonwealth of Australia website.

#Input

Three arguments are neededfor fetching the records. 
args - 3 arguments. args(0) - Year, args(1) - month, args (2) - day of month
The cities where the weather forecast processed are CANBERRA, SYDNEY, WOLLONGONG, MELBOURNE, BRISBANE, CAIRNS, GOLD COAST, ADELAIDE, PERTH, HOBART, DARWIN.

#Build and Test

The application is designed as a Maven Scala project. The project can be built using Maven build and unit testing automation can be done using Maven too (running the project as Maven test will run all the test suits before building the project)
The dependencies are added in the POM.xml.

#Project Organization

All the data handling operations are grouped under the package org.weather.simulator.Data
The main processing logic and the utilities for the processing logics are grouped under org.weather.simulator.Process
The main class of the application is org.weather.simulator.Process.ProcessWeather

#Project Flow

1. The default constant data items from the ConstData are fetched and saved as map.
2. The date passed through the arguments is checked for the range and it should be in range of "201602|201703" for which the data is available in the BOM website at this point of time.
3. The weather files are downloaded from the BOM site for all the cities taken into consideration and the given month and year.
4. The records are filtered for a day and the output is constructed. The location are fetched using google API.
