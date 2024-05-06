import scala.io.StdIn.readLine

object Alert {
  def detectErrors(data: List[List[String]]): Unit = {
    println("Detecting errors in the data...")
    //    val data = CSVconvertMatrix.csvToMatrix("wind.csv")
    println(
      "Choose the time frame for error detection:\n1. Hourly\n2. Daily\n3. Weekly\n4. Monthly"
    )
    val timeFrame = readLine()

    val selectedData = timeFrame match {
      case "1" => DataFilter.dataHourly(data)
      case "2" => DataFilter.dataDaily(data)
      case "3" => DataFilter.dataWeekly(data)
      case "4" => DataFilter.dataMonthly(data)
      case _ =>
        println("Invalid choice, defaulting to Daily data.")
        DataFilter.dataDaily(data)
    }
    

    val minValue = DataAnalysis.median(selectedData)
    val avgValue = DataAnalysis.average(selectedData)
    val error = avgValue - minValue

    println(
      s"Error detection: Minimal value is $minValue, you should add $error power at that time."
    )
  }
}
