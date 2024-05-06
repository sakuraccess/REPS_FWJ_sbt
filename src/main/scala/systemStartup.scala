import java.net.{HttpURLConnection, URL}
import scala.io.Source

object systemStartup {
  // fetching data
  //  https://data.fingrid.fi/api/datasets/{datasetId}/data[?startTime][&endTime][&format][&oneRowPerTimePeriod][&page][&pageSize][&locale][&sortBy][&sortOrder]

  private val parameterSolar = Seq(
    "startTime=2023-12-01T00:00:00Z",
    "endTime=2024-04-30T23:59:59Z",
    "format=csv",
    "oneRowPerTimePeriod=true"
  ).mkString("&")

  private val parameterWind = Seq(
    "startTime=2023-12-01T00:00:00Z",
    "endTime=2024-04-30T23:59:59Z",
    "format=csv",
    "oneRowPerTimePeriod=true"
  ).mkString("&")

  private val parameterHydro = Seq(
    "startTime=2023-12-01T00:00:00Z",
    "endTime=2024-04-30T23:59:59Z",
    "format=csv",
    "oneRowPerTimePeriod=true"
  ).mkString("&")

  def startup(): Unit = {
    fetchCSVData(248, parameterSolar, "solar.csv")
    fetchCSVData(191, parameterHydro, "Hydro.csv")
    fetchCSVData(75, parameterWind, "wind.csv")
  }

  private def fetchCSVData(dataSetID: Int, parameters: String, fileName: String): Unit = {
    val apiPrimaryKey = "3ef07d018a8e494ea8801fc090a3c6b7"
    val requestBaseURL = "https://data.fingrid.fi/api/datasets"
    val requestSolarURL = new URL(s"$requestBaseURL/$dataSetID/data?$parameters")

    val connectionSolar = requestSolarURL.openConnection().asInstanceOf[HttpURLConnection]
    connectionSolar.setRequestProperty("x-api-key", s"$apiPrimaryKey")
    connectionSolar.setRequestMethod("GET")
    connectionSolar.getResponseCode match {
      case 200 =>
        val csvData = Source.fromInputStream(connectionSolar.getInputStream).mkString
        val printWriter = new java.io.PrintWriter(new java.io.File(fileName))
        try {
          printWriter.write(csvData)
          println(s"CSV data has been saved to $fileName")
        } finally {
          printWriter.close()
        }
      case 422 =>
        println("422 Unprocessable Entity.")
      case code =>
        println(s"HTTP error code: $code")
    }
    connectionSolar.disconnect()
  }

  def csvToMatrix(filePath: String): List[List[String]] = {
    val lines = Source.fromFile(filePath).getLines().toList
    lines.drop(4).map(_.split(",").toList.map(_.trim))
  }
}