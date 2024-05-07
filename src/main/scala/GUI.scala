import javax.swing.ImageIcon
import scala.swing.*
import scala.swing.GridBagPanel.Fill
import scala.swing.event.*

object GUI extends SimpleSwingApplication {
  println("Welcome to the Renewable Energy Plant System!")
  SystemStartup.startup()
  
  def top: MainFrame = new MainFrame {
    title = "Data Analysis Tool"
    size = new Dimension(1250, 768)
    location = new Point(300, 100)
    
    private val tabs = new TabbedPane {
      pages += new TabbedPane.Page("Overview", createTabPanel("Combined Overview of Power Generation", "./charts/overviewChart.png", "4"))
      pages += new TabbedPane.Page("Solar", createTabPanel("Solar Power Generation in last 24h", "./charts/solarChart.png", "2"))
      pages += new TabbedPane.Page("Wind", createTabPanel("Wind Power Generation in last 24h", "./charts/windChart.png", "1"))
      pages += new TabbedPane.Page("Hydro", createTabPanel("Hydro Power Generation in last 24h", "./charts/hydroChart.png", "3"))
    }
    
    contents = tabs
    
    def createTabPanel(description: String, imagePath: String, dataType: String): Component = {
      val panel = new BoxPanel(Orientation.Horizontal) {
        border = Swing.EmptyBorder(10)
        
        contents += new BoxPanel(Orientation.Vertical) {
          border = Swing.EmptyBorder(10)
          contents += new Label(description)
          contents += new Label {
            icon = new ImageIcon(imagePath)
          }
        }
        contents += new ScrollPane(makeControlsPanel(dataType))
      }
      panel
    }
    
    private def makeControlsPanel(dataType: String): GridBagPanel = {
      val controls: GridBagPanel = new GridBagPanel {
        val c = new Constraints
        val insets = new Insets(5, 5, 5, 5)
        
        // Adding date inputs and time span selector
        c.gridx = 0
        c.gridy = 0
        c.fill = Fill.Horizontal
        layout(new Label("Start Date (DD/MM/YYYY):")) = c
        
        c.gridy += 1
        val input1: TextField = new TextField {
          columns = 5
        }
        layout(input1) = c
        
        c.gridy += 1
        layout(new Label("End Date (DD/MM/YYYY):")) = c
        
        c.gridy += 1
        val input2: TextField = new TextField {
          columns = 5
        }
        layout(input2) = c
        
        c.gridy += 1
        layout(new Label("Time Span:")) = c
        
        c.gridy += 1
        val timeSpanSelector = new ComboBox(Seq("Hourly", "Daily", "Weekly", "Monthly"))
        layout(timeSpanSelector) = c
        
        c.gridy += 1
        val searchButton = new Button("Search")
        layout(searchButton) = c
        
        // Results area
        c.gridy += 1
        val resultsArea: TextArea = new TextArea(8, 20) {
          editable = false
        }
        layout(resultsArea) = c
        
        listenTo(searchButton)
        reactions += {
          case ButtonClicked(`searchButton`) =>
            val startDate = input1.text
            val endDate = input2.text
            val timeSpan = timeSpanSelector.selection.item
            
            if (validateDate(startDate) && validateDate(endDate)) {
              val results = analyzeData(startDate, endDate, timeSpan, dataType)
              resultsArea.text = s"Mean: ${results._1}\nMedian: ${results._2}\nMode: ${results._3}\nRange: ${results._4}\nMidrange: ${results._5}"
            } else {
//              resultsArea.text = "Invalid date format. Please enter the date in the format 'DD/MM/YYYY'.\nFor example, enter '12/04/2024' for April 12, 2024."
              Dialog.showMessage(
                contents.head,
                "Invalid date format. Please enter the date in the format 'DD/MM/YYYY'.\nFor example, enter '12/04/2024' for April 12, 2024.",
                title = "Date Format Error"
              )
            }
        }
        
      }
      controls
    }
    
    private def validateDate(dateStr: String): Boolean = {
      val dateFormat = new java.text.SimpleDateFormat("dd/MM/yyyy")
      dateFormat.setLenient(false)
      try {
        dateFormat.parse(dateStr)
        true
      } catch {
        case _: java.text.ParseException => false
      }
    }
    
    // Dummy implementation of analyzeData function
    private def analyzeData(startDate: String, endDate: String, timeFrame: String, dataType: String): (Double, Double, Double, Double, Double) = {
      // Perform actual analysis here and replace the following dummy values
      DataAnalysis.analyzeData(startDate, endDate, timeFrame, dataType)
//      (1.0, 1.0, 1.0, 1.0, 1.0)
    }
  }
}
