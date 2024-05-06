import scala.io.StdIn.readLine

object Alert {
  def detectErrors(): Unit = {
    println("Detecting errors in the data...")
    println("What type of data do you want to detect:\n1. Wind\n2. Solar\n3. Hydro")
    val datatype = readLine().trim
    val data = datatype match {
      case "1" => SystemStartup.csvToMatrix("wind.csv")
      case "2" => SystemStartup.csvToMatrix("solar.csv")
      case "3" => SystemStartup.csvToMatrix("hydro.csv")
      case _ =>
        println("Invalid choice, defaulting to Wind Power data.")
        SystemStartup.csvToMatrix("wind.csv")
    }

    val selectedData = DataFilter.dataHourly(data)
    if (selectedData.nonEmpty) {
      val lastEntries = selectedData.takeRight(5)
      datatype match {
        case "1" => checkWind(lastEntries)
        case "2" => checkSolar(lastEntries)
        case "3" => checkHydro(lastEntries)
        case _ => checkWind(lastEntries)
      }
    } else {
      println("No data available for analysis.")
    }
  }

  private def checkWind(data: List[(String, Double)]): Unit = {
    if (data.length >= 5) {
      val stabilityCheck = data.take(4).map(_._2).sliding(2).forall(pair => math.abs(pair(1) - pair(0)) <= 100)
      val lastZero = data.last._2 == 0.0
      if (stabilityCheck && lastZero) {
        println("Alert: Wind power data shows unusual zero in the latest entry with prior stable readings.")
      } else {
        println("No issues detected with Wind power data.")
      }
    }
  }

  private def checkSolar(data: List[(String, Double)]): Unit = {
    if (data.nonEmpty && data.last._2 == 0.0) {
      println("Alert: Solar power data shows zero at the afternoon, it is not normally situation")
    } else {
      println("No issues detected with Solar power data.")
    }
  }

  private def checkHydro(data: List[(String, Double)]): Unit = {
    val threshold = 10000.0
    if (data.nonEmpty && data.last._2 > threshold) {
      println(s"Alert: Hydro power data exceeds the threshold of $threshold in the latest entry, please check the equipment of hydropower or escape the flood!")
    } else {
      println("No issues detected with Hydro power data.")
    }
  }
}
