import scala.util.Try
import java.time.format.DateTimeFormatter
import java.time.LocalDateTime

object ViewStatus {

  private val formatter =
    DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")

  def viewStatus(entireDataset: List[List[String]]): Unit = {
    println("Generating graph for the dataset...")
    // Placeholder for graph generation logic
    val plottingData = dataProcessing(entireDataset)

    generatePlot(plottingData)
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
            Some((LocalDateTime.parse(endTime, formatter), pg))
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

  private def generatePlot(plottingData: List[(String, Double)]): Unit = {
    
  }
}
