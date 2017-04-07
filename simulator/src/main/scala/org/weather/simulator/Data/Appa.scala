package org.weather.simulator.Data
  
import scala.io.Codec

/**
 * Hello world!
 *
 */
object Appa {
  def main(args: Array[String]) {
  val bomFileCodec = Codec("windows-1252") //Codec of bom data file.
  println( bomFileCodec )
  }
}
