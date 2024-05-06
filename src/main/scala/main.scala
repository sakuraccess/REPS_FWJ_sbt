import scala.io.StdIn.readLine

@main
def main(): Unit = {
  println("Welcome to the Renewable Energy Plant System!")
  //    apiDataFetching.fetchData()

  SystemStartup.startup()

  val windData = SystemStartup.csvToMatrix("75_2023-12-01T0000_2024-04-30T2355.csv")
  val solarData = SystemStartup.csvToMatrix("248_2023-12-01T0000_2024-04-30T2359.csv")
  val hydroData = SystemStartup.csvToMatrix("191_2023-12-01T0000_2024-04-30T2359.csv")

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
      case "1" => ViewStatus.generateGraph()
      case "2" => DataAnalysis.analyzeData(windData)
      case "3" => Alert.detectErrors(windData)
      case "4" => println("Exiting system...")
      case _ => println("Invalid option. Please enter a number between 1 and 4.")
    }
  }
}