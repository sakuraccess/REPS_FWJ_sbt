import scala.io.StdIn.readLine

object DataAnalysis {

  def average(data: List[(String, Double)]): Double = {
    if (data.isEmpty) 0.0
    else data.map(_._2).sum / data.length
  }

  def median(data: List[(String, Double)]): Double = {
    val sortedData = data.map(_._2).sorted
    if (sortedData.isEmpty) 0.0
    else if (sortedData.length % 2 != 0) sortedData(sortedData.length / 2)
    else
      (sortedData(sortedData.length / 2 - 1) + sortedData(
        sortedData.length / 2
      )) / 2.0
  }

  private def mode(data: List[(String, Double)]): Double = {
    val frequencyMap = data.map(_._2).groupBy(identity).mapValues(_.size)
    val maxFreq = frequencyMap.values.max
    frequencyMap
      .collectFirst { case (value, freq) if freq == maxFreq => value }
      .getOrElse(0.0)
  }

  private def range(data: List[(String, Double)]): Double = {
    if (data.isEmpty) 0.0
    else {
      val numericValues = data.map(_._2)
      numericValues.max - numericValues.min
    }
  }

  private def midValue(data: List[(String, Double)]): Double = median(data)

  def analyzeData(data: List[List[String]]): Unit = {
    println("Analyzing data...")
    //    val data = CSVconvertMatrix.csvToMatrix("wind.csv")
    println(
      "Choose the time frame for analysis:\n1. Hourly\n2. Daily\n3. Weekly\n4. Monthly"
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

    println(
      "Choose the type of statistical analysis:\n1. Average\n2. Median\n3. Mode\n4. Range\n5. Mid Value"
    )
    val analysisType = readLine()
    val result = analysisType match {
      case "1" => s"Average Power: ${average(selectedData)}"
      case "2" => s"Median Power: ${median(selectedData)}"
      case "3" => s"Mode Power: ${mode(selectedData)}"
      case "4" => s"Range of Power: ${range(selectedData)}"
      case "5" => s"Mid Value of Power: ${midValue(selectedData)}"
      case _ =>
        println("Invalid choice, calculating Average by default.")
        s"Average Power: ${average(selectedData)}"
    }

    println(result)
  }
}
