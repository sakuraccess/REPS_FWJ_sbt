import java.text.SimpleDateFormat
import java.util.Date

object DataAnalysis {
  
  def analyzeData(startDate: String, endDate: String, timeFrame: String, dataType: String): (Double, Double, Double, Double, Double) = {
    
    //    println("Analyzing data...")
    //    val data = CSVconvertMatrix.csvToMatrix("./datasets/wind.csv")
    //    println("What type of data do you want to detect:\n1. Wind\n2. Solar\n3. Hydro")
    //    val dataType = readLine().trim
    val data = dataType match {
      case "1" => SystemStartup.csvToMatrix("./datasets/wind.csv")
      case "2" => SystemStartup.csvToMatrix("./datasets/solar.csv")
      case "3" => SystemStartup.csvToMatrix("./datasets/hydro.csv")
      case "4" => SystemStartup.csvToMatrix("./datasets/overview.csv")
      case _ =>
        println("Invalid choice, defaulting to Wind Power data.")
        SystemStartup.csvToMatrix("./datasets/wind.csv")
    }
    
    val desiredDataRange = processRange(data, startDate, endDate)
    //    println(
    //      "Choose the time frame for analysis:\n1. Hourly\n2. Daily\n3. Weekly\n4. Monthly"
    //    )
    
    //    val timeFrame = readLine()
    
    val selectedData = timeFrame match {
      case "Hourly" => DataFilter.dataHourly(desiredDataRange)
      case "Daily" => DataFilter.dataDaily(desiredDataRange)
      case "Weekly" => DataFilter.dataWeekly(desiredDataRange)
      case "Monthly" => DataFilter.dataMonthly(desiredDataRange)
      case _ =>
        println("Invalid choice, defaulting to Daily data.")
        DataFilter.dataDaily(desiredDataRange)
    }
    
    //    println(s"Average Power: ${average(selectedData)}")
    //    println(s"Median Power: ${median(selectedData)}")
    //    println(s"Mode Power: ${mode(selectedData)}")
    //    println(s"Range of Power: ${range(selectedData)}")
    //    println(s"Mid Value of Power: ${midValue(selectedData)}")
    
    (average(selectedData), median(selectedData), mode(selectedData), range(selectedData), midValue(selectedData))
  }
  
  private def average(data: List[(String, Double)]): Double = {
    if (data.isEmpty) 0.0
    else data.map(_._2).sum / data.length
  }
  
  private def median(data: List[(String, Double)]): Double = {
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
  
  
  private def processRange(originalData: List[List[String]], startDate: String, endDate: String): List[List[String]] = {
    val dateFormat = new SimpleDateFormat("dd/MM/yyyy")
    val targetFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    val startDateParsed: Date = dateFormat.parse(startDate)
    val endDateParsed: Date = dateFormat.parse(endDate)
    
    // Convert start and end dates to the timestamp format for comparison
    val startDateTime: String = targetFormat.format(startDateParsed)
    val endDateTime: String = targetFormat.format(endDateParsed)
    
    originalData.filter { record =>
      // Assume the format of each record is List("start time", "end time", "value")
      val startTime = record.head // or record(0) depending on your certainty of record structure
      val endTime = record(1)
      
      // Compare start and end times of each record to the desired range
      startTime >= startDateTime && endTime <= endDateTime
    }
  }
  
}
