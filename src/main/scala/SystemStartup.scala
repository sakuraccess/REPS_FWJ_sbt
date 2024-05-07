import play.api.libs.json.*

import java.io.{File, PrintWriter}
import java.net.{HttpURLConnection, URL}
import scala.io.Source

object SystemStartup {
  // fetching data
  // https://data.fingrid.fi/api/datasets/{datasetId}/data[?startTime][&endTime][&format][&oneRowPerTimePeriod][&page][&pageSize][&locale][&sortBy][&sortOrder]
  
  def startup(): Unit = {
    fetchCSVData(248, "./datasets/solar.csv", 20000, 1)
    fetchCSVData(75, "./datasets/wind.csv", 20000, 1)
    fetchCSVData(191, "./datasets/hydro1.csv", 20000, 1)
    fetchCSVData(191, "./datasets/hydro2.csv", 20000, 2)
    fetchCSVData(191, "./datasets/hydro3.csv", 20000, 3)
    fetchCSVData(191, "./datasets/hydro4.csv", 20000, 4)
    fetchCSVData(191, "./datasets/hydro5.csv", 20000, 5)
    fetchCSVData(191, "./datasets/hydro6.csv", 20000, 6)
    
    val fileNames = List(
      "./datasets/hydro1.csv",
      "./datasets/hydro2.csv",
      "./datasets/hydro3.csv",
      "./datasets/hydro4.csv",
      "./datasets/hydro5.csv",
      "./datasets/hydro6.csv"
    )
    mergeCsvFiles(fileNames, "./datasets/hydro.csv")
  }
  
  private def fetchCSVData(dataSetID: Int, fileName: String, pageSize: Int, page: Int): Unit = {
    val parameters = Seq(
      "startTime=2023-12-01T00:00:00Z",
      "endTime=2024-05-01T13:00:00Z",
      "format=csv",
      "oneRowPerTimePeriod=true",
      s"pageSize=$pageSize",
      s"page=$page",
      "locale=en"
    ).mkString("&")
    
    val apiPrimaryKey = "3ef07d018a8e494ea8801fc090a3c6b7"
    val requestBaseURL = "https://data.fingrid.fi/api/datasets"
    val requestSolarURL = new URL(s"$requestBaseURL/$dataSetID/data?$parameters")
    
    val connectionSolar = requestSolarURL.openConnection().asInstanceOf[HttpURLConnection]
    connectionSolar.setRequestProperty("x-api-key", s"$apiPrimaryKey")
    connectionSolar.setRequestMethod("GET")
    connectionSolar.getResponseCode match {
      case 200 =>
        val jsonResponse =
          Source.fromInputStream(connectionSolar.getInputStream).mkString
        val json = Json.parse(jsonResponse)
        val csvData = (json \ "data").as[String]
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
  
  private def mergeCsvFiles(fileNames: List[String], outputFileName: String): Unit = {
    val printWriter = new PrintWriter(new File(outputFileName))
    fileNames.zipWithIndex.foreach { case (fileName, index) =>
      val source = Source.fromFile(fileName)
      if (index == 0) {
        printWriter.write(
          source.getLines.mkString("\n") + "\n"
        ) // Write headers and data for the first file
      } else {
        printWriter.write(
          source.getLines.mkString("\n") + "\n"
        ) // Skip headers for subsequent files
      }
      source.close()
    }
    printWriter.close()
  }
  
  def csvToMatrix(filePath: String): List[List[String]] = {
    val lines = Source.fromFile(filePath).getLines().toList
    lines.drop(1).map(_.split(",").toList.map(_.trim))
  }
}
