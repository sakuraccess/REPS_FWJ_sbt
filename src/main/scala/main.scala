import scala.io.StdIn.readLine

@main
def main(): Unit = {
  println("Welcome to the Renewable Energy Plant System!")
  //    apiDataFetching.fetchData()
  
//  var option: String = ""
//  while (option != "4") {
//    println("\nPlease choose an option:")
//    println("1: Generate the graph for dataset")
//    println("2: Analyze the data")
//    println("3: Detect errors in the data")
//    println("4: Exit")
//    println("Enter your choice (1-4):")
//
//    option = readLine()
//
//    option match {
//      case "1" => ViewsGenerate.viewStatus()
//      case "2" => DataAnalysis.analyzeData()
//      case "3" => Alert.detectErrors()
//      case "4" => println("Exiting system...")
//      case _ => println("Invalid option. Please enter a number between 1 and 4.")
//    }
//  }

  SystemStartup.startup()
//  ViewsGenerate.viewStatus()
//  GUI.main()
}