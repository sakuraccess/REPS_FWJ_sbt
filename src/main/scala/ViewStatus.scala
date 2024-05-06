import scala.util.Try
import java.time.format.DateTimeFormatter
import java.time.LocalDateTime
import org.jfree.chart.ChartFactory
import org.jfree.chart.ChartUtils
import org.jfree.data.time.{TimeSeries, TimeSeriesCollection, Minute}
import java.io.File
import java.text.SimpleDateFormat

object ViewStatus {

  private val formatter =
    DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")

  def viewStatus(entireDataset: List[List[String]]): Unit = {
    println("Generating graph for the dataset...")
    // Placeholder for graph generation logic
    val plottingData = dataProcessing(entireDataset)

    val chart = generatePlot(plottingData)
    ChartUtils.saveChartAsPNG(new File("chart.png"), chart, 600, 400)
    println("Chart has been saved as PNG.")
//    println(plottingData)
  }

  private def dataProcessing(
      entireDataset: List[List[String]]
  ): List[(String, Double)] = {
    val pastDayData = entireDataset.slice(-96, 96)

    val plottingData = pastDayData.flatMap {
      case List(_, endTime, powerGeneration) =>
        val cleanPowerGeneration =
          powerGeneration.replace("\"", "") // Remove double quotes
        Try(cleanPowerGeneration.toDouble).toOption match {
          case Some(pg) =>
//            Some((LocalDateTime.parse(endTime, formatter), pg))
            Some((endTime, pg))
          case None =>
            println(
              s"Failed to parse cleaned power generation value from: '$cleanPowerGeneration'"
            )
            None
        }
      case _ =>
        println("Warning: Data format does not match expected pattern")
        None
    }

    plottingData
  }

  private def generatePlot(plottingData: List[(String, Double)]) = {
    val series = new TimeSeries("Data Series")
    val dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")

    plottingData.foreach { case (timestamp, value) =>
      println(timestamp.stripMargin('"'))
      val date = dateFormat.parse(timestamp.stripMargin('"'))
      series.add(new Minute(date), value)
    }

    val dataset = new TimeSeriesCollection()
    dataset.addSeries(series)

    ChartFactory.createTimeSeriesChart(
      "Time Series Data", // Title
      "Date", // X-axis Label
      "Value", // Y-axis Label
      dataset, // Dataset
      true, // Legend
      true, // Tooltips
      false // URLs
    )
  }

}
