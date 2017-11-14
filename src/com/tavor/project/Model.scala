package com.tavor.project
import scala.xml._
import java.net._
import scala.util.control.Breaks._

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

class Model() extends ModelInterFace {

  val url = new URL("http://www.bankisrael.gov.il/currency.xml")
  val conn = url.openConnection
  val doc = XML.load(conn.getInputStream)

  def Convert(from: String, amount: Double, to: String): Double = {
    var fromRate: Double = 1
    var toRate: Double = 1
    var shekel: Double = 1
    var Result: Double = 1

    breakable {
      for (elm <- (doc \\ "CURRENCY")) {
        if (from == "ILS") {
          fromRate = 1
          break
        }
        if (from == (elm \\ "CURRENCYCODE").text) {
          fromRate = ((elm \\ "RATE").text.toDouble)
          break
        }
      }
    }

    if (from == "JPY")
      fromRate = fromRate / 100
    if (from == "LBP")
      fromRate = fromRate / 10

    breakable {
      for (elm <- (doc \\ "CURRENCY")) {
        if (to == (elm \\ "CURRENCYCODE").text) {
          toRate = ((elm \\ "RATE").text.toDouble)
          break;
        }
      }
    }

    if (from == "JPY")
      fromRate = fromRate / 100
    if (from == "LBP")
      fromRate = fromRate / 10
      
    if (from != "ILS") {
      shekel = fromRate * amount
      Result = shekel / toRate
    }

    if (from == "ILS") {
      Result = amount / toRate
    }
    Result
  }

  def getDate(): String = (doc \\ "LAST_UPDATE").text

  def getData(): Array[String] = {
    var stream: String = ""
    var vec: Array[String] = new Array[String](28)
    var i: Int = 0

    for (elm <- (doc \\ "CURRENCY")) {
      var rate: Double = 0
      rate = (elm \\ "RATE").text.toDouble
      if ((elm \\ "UNIT").text.toDouble == 10) {
        rate = rate / 10
      }
      if ((elm \\ "UNIT").text.toDouble == 100) {
        rate = rate / 100
      }
      vec(i) = ((elm \\ "COUNTRY").text)
      i += 1
      vec(i) = rate.toString()
      i += 1
    }
    vec
  }


}

