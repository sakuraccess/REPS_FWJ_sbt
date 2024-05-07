import org.jfree.chart.ChartUtils

import java.io.File

object SimulateIssues {
  def detectErrors(dataType: String): String = {
    //    println("Detecting errors in the data...")
    //    println("What type of data do you want to detect:\n1. Wind\n2. Solar\n3. Hydro")
    
    //    val dataType = readLine().trim
    val data = dataType match {
      case "1" => SystemStartup.csvToMatrix("./datasets/wind.csv")
      case "2" => SystemStartup.csvToMatrix("./datasets/solar.csv")
      case "3" => SystemStartup.csvToMatrix("./datasets/hydro.csv")
      case "4" => SystemStartup.csvToMatrix("./datasets/overview.csv")
      case _ => println("Invalid choice, defaulting to Wind Power data.")
        SystemStartup.csvToMatrix("./datasets/wind.csv")
    }
    
    //    val selectedData = DataFilter.dataHourly(data)
    
    val lastEntries = DataFilter.extractDateTimeAndPower(data).takeRight(30)
    //    println(lastEntries)
    val message = dataType match {
      case "1" => checkWind(lastEntries)
      case "2" => checkSolar(lastEntries)
      case "3" => checkHydro(lastEntries)
      //      case "4" => checkOverall(lastEntries)
      case _ => checkWind(lastEntries)
    }
    message
  }
  
  private def checkWind(data: List[(String, Double)]): String = {
    val dataUnusual = data ::: List(("2024-05-01T13:30:00.000Z", 0.00))
    val stabilityCheck = dataUnusual.take(dataUnusual.length - 1).map(_._2).sliding(2).forall(pair => math.abs(pair(1) - pair.head) != 0)
    //      println(stabilityCheck)
    
    if (stabilityCheck && dataUnusual.last._2 == 0.0) {
      val chart = ViewsGenerate.generatePlot(dataUnusual, "5")
      ChartUtils.saveChartAsPNG(new File("./charts/errorWind.png"), chart, 600, 400)
      "Alert: The wind power generation equipment is abnormal " +
        "and the power generation reading drops sharply to 0."
    } else {
      "No issues detected with Wind power data."
    }
  }
  
  private def checkSolar(data: List[(String, Double)]): String = {
    val dataUnusual = data ::: List(("2024-05-01T13:30:00.000Z", 0.00))
    if (dataUnusual.nonEmpty && dataUnusual.last._2 == 0.0) {
      val chart = ViewsGenerate.generatePlot(dataUnusual, "6")
      ChartUtils.saveChartAsPNG(new File("./charts/errorSolar.png"), chart, 600, 400)
      "Alert: The solar power generation equipment is abnormal, because the power generation " +
        "reading drops to 0 at the afternoon which is abnormally."
    } else {
      "No issues detected with Solar power data."
    }
  }
  
  private def checkHydro(data: List[(String, Double)]): String = {
    val dataUnusual = data ::: List(("2024-05-01T13:30:00.000Z", 10020.0))
    val threshold = 10000.0
    if (dataUnusual.last._2 > threshold) {
      val chart = ViewsGenerate.generatePlot(dataUnusual, "7")
      ChartUtils.saveChartAsPNG(new File("./charts/errorHydro.png"), chart, 600, 400)
      s"Alert: Hydro power data exceeds the threshold of $threshold in the latest entry, please check the equipment of hydropower or escape the flood!"
    } else {
      "No issues detected with Hydro power data."
    }
  }
  
  //  private def checkOverall(data: List[(String, Double)]): String =
  //
  
}
