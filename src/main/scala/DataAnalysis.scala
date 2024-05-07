import scala.io.StdIn.readLine

object DataAnalysis {
  
  def analyzeData(): Unit = {
    
    println("Analyzing data...")
    //    val data = CSVconvertMatrix.csvToMatrix("./datasets/wind.csv")
    println("What type of data do you want to detect:\n1. Wind\n2. Solar\n3. Hydro")
    val datatype = readLine().trim
    val data = datatype match {
      case "1" => SystemStartup.csvToMatrix("./datasets/wind.csv")
      case "2" => SystemStartup.csvToMatrix("./datasets/solar.csv")
      case "3" => SystemStartup.csvToMatrix("./datasets/hydro.csv")
      case _ =>
        println("Invalid choice, defaulting to Wind Power data.")
        SystemStartup.csvToMatrix("./datasets/wind.csv")
    }
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
    
    
    println(s"Average Power: ${average(selectedData)}")
    println(s"Median Power: ${median(selectedData)}")
    println(s"Mode Power: ${mode(selectedData)}")
    println(s"Range of Power: ${range(selectedData)}")
    println(s"Mid Value of Power: ${midValue(selectedData)}")
    
  }
  
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
}
