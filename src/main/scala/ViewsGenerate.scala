// FWJ_Project_Part A
import org.jfree.chart.{ChartFactory, ChartUtils, JFreeChart}
import org.jfree.data.time.{Minute, TimeSeries, TimeSeriesCollection}

import java.io.File
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import scala.util.Try

object ViewsGenerate {
  
  private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
  
  def viewStatus(dataType: String): Unit = {
    //    println("Generating graph for the dataset...")
    //
    //    println("What type of data do you want to detect:\n1. Wind\n2. Solar\n3. Hydro")
    
    //    val datatype = readLine().trim
    val fileName = dataType match {
      case "1" => "./charts/windChart.png"
      case "2" => "./charts/solarChart.png"
      case "3" => "./charts/hydroChart.png"
      case "4" => "./charts/overviewChart.png"
      case _ => "./charts/windChart.png"
    }
    
    val entireDataset = dataType match {
      case "1" => SystemStartup.csvToMatrix("./datasets/wind.csv")
      case "2" => SystemStartup.csvToMatrix("./datasets/solar.csv")
      case "3" => SystemStartup.csvToMatrix("./datasets/hydro.csv")
      case "4" => SystemStartup.csvToMatrix("./datasets/overview.csv")
      case _ =>
        println("Invalid choice, defaulting to Wind Power data.")
        SystemStartup.csvToMatrix("./datasets/wind.csv")
    }
    
    // Placeholder for graph generation logic
    val plottingData = dataProcessing(entireDataset, dataType)
    
    val chart = generatePlot(plottingData, dataType)
    ChartUtils.saveChartAsPNG(new File(fileName), chart, 600, 400)
    //    println("Chart has been saved as PNG.") //    println(plottingData)
  }
  
  private def dataProcessing(entireDataset: List[List[String]], dataType: String): List[(String, Double)] = {
    val pastDayData = if dataType != "3" then entireDataset.slice(-96, 96) else entireDataset.slice(-480, 480)
    
    val plottingData = pastDayData.flatMap {
      case List(_, endTime, powerGeneration) =>
        val cleanPowerGeneration: String = powerGeneration.replace("\"", "") // Remove double quotes
        Try(cleanPowerGeneration.toDouble).toOption match {
          case Some(pg) => //            Some((LocalDateTime.parse(endTime, formatter), pg))
            Some((endTime, pg))
          case None => println(s"Failed to parse cleaned power generation value from: '$cleanPowerGeneration'")
            None
        }
      case _ => println("Warning: Data format does not match expected pattern")
        None
    }
    
    plottingData
  }
  
  def generatePlot(plottingData: List[(String, Double)], dataType: String): JFreeChart = {
    val title = dataType match {
      case "1" => "Wind Power Generation in last 24h"
      case "2" => "Solar Power Generation in last 24h"
      case "3" => "Hydro Power Production in last 24h"
      case "4" => "Overview of current total production capacity in last 24h"
      case "5" => "Wind Power Generation in last 1h"
      case "6" => "Solar Power Generation in last 1h"
      case "7" => "Hydro Power Production in last 1h"
      case "8" => "Overview of current total production capacity in last 1h"
      case _ => "Wind Power Generation"
    }
    val series = new TimeSeries(title)
    val dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    
    plottingData.foreach { case (timestamp, value) => //      println(timestamp.stripMargin('"'))
      val date = dateFormat.parse(timestamp.stripMargin('"'))
      series.add(new Minute(date), value)
    }
    
    val dataset = new TimeSeriesCollection()
    dataset.addSeries(series)
    
    ChartFactory.createTimeSeriesChart(title, // Title
      "Hour", // X-axis Label
      "Power Generation/Production, Unit MW", // Y-axis Label
      dataset, // Dataset
      true, // Legend
      true, // Tooltips
      false // URLs
    )
  }
}
