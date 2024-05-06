import scala.io.StdIn.readLine

@main
def main(): Unit = {
  println("Welcome to the Renewable Energy Plant System!")
  //    apiDataFetching.fetchData()

  systemStartup.startup()

  val windData = systemStartup.csvToMatrix("75_2023-12-01T0000_2024-04-30T2355.csv")
  val solarData = systemStartup.csvToMatrix("248_2023-12-01T0000_2024-04-30T2359.csv")
  val hydroData = systemStartup.csvToMatrix("191_2023-12-01T0000_2024-04-30T2359.csv")

  var option: String = ""
  while (option != "4") {
    println("\nPlease choose an option:")
    println("1: Generate the graph for dataset")
    println("2: Analyze the data")
    println("3: Detect errors in the data")
    println("4: Exit")
    println("Enter your choice (1-4):")

    option = readLine()

    option match {
      case "1" => generateGraph()
      case "2" => analyzeData(windData)
      case "3" => detectErrors(windData)
      case "4" => println("Exiting system...")
      case _ => println("Invalid option. Please enter a number between 1 and 4.")
    }
  }
}

private def generateGraph(): Unit = {
  println("Generating graph for the dataset...")
  // Placeholder for graph generation logic
}

private def analyzeData(data: List[List[String]]): Unit = {
  println("Analyzing data...")
  //    val data = CSVconvertMatrix.csvToMatrix("wind.csv")
  println("Choose the time frame for analysis:\n1. Hourly\n2. Daily\n3. Weekly\n4. Monthly")
  val timeFrame = readLine()

  val selectedData = timeFrame match {
    case "1" => dataFilter.dataHourly(data)
    case "2" => dataFilter.dataDaily(data)
    case "3" => dataFilter.dataWeekly(data)
    case "4" => dataFilter.dataMonthly(data)
    case _ =>
      println("Invalid choice, defaulting to Daily data.")
      dataFilter.dataDaily(data)
  }

  println("Choose the type of statistical analysis:\n1. Average\n2. Median\n3. Mode\n4. Range\n5. Mid Value")
  val analysisType = readLine()
  val result = analysisType match {
    case "1" => s"Average Power: ${new dataAnalysis().average(selectedData)}"
    case "2" => s"Median Power: ${new dataAnalysis().median(selectedData)}"
    case "3" => s"Mode Power: ${new dataAnalysis().mode(selectedData)}"
    case "4" => s"Range of Power: ${new dataAnalysis().range(selectedData)}"
    case "5" => s"Mid Value of Power: ${new dataAnalysis().midValue(selectedData)}"
    case _ =>
      println("Invalid choice, calculating Average by default.")
      s"Average Power: ${new dataAnalysis().average(selectedData)}"
  }

  println(result)
}

private def detectErrors(data: List[List[String]]): Unit = {
  println("Detecting errors in the data...")
  //    val data = CSVconvertMatrix.csvToMatrix("wind.csv")
  println("Choose the time frame for error detection:\n1. Hourly\n2. Daily\n3. Weekly\n4. Monthly")
  val timeFrame = readLine()

  val selectedData = timeFrame match {
    case "1" => dataFilter.dataHourly(data)
    case "2" => dataFilter.dataDaily(data)
    case "3" => dataFilter.dataWeekly(data)
    case "4" => dataFilter.dataMonthly(data)
    case _ =>
      println("Invalid choice, defaulting to Daily data.")
      dataFilter.dataDaily(data)
  }

  val analysis = new dataAnalysis()
  val minValue = analysis.median(selectedData)
  val avgValue = analysis.average(selectedData)
  val error = avgValue - minValue

  println(s"Error detection: Minimal value is $minValue, you should add $error power at that time.")
}