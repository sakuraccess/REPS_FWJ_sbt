import scala.io.StdIn.readLine

@main
def main(): Unit = {
  println("Welcome to the Renewable Energy Plant System!")
  //    apiDataFetching.fetchData()
  SystemStartup.startup()

  val windData = SystemStartup.csvToMatrix("wind.csv")
  val solarData = SystemStartup.csvToMatrix("solar.csv")
  val hydroData = SystemStartup.csvToMatrix("hydro.csv")

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
      case "3" => Alert.detectErrors()
      case "4" => println("Exiting system...")
      case _ => println("Invalid option. Please enter a number between 1 and 4.")
    }
  }
}