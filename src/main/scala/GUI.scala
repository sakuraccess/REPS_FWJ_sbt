// FWJ_Project_Part A
import java.awt.{Color, Font}
import java.text.SimpleDateFormat
import java.util.Date
import javax.swing.ImageIcon
import scala.swing.*
import scala.swing.GridBagPanel.Fill
import scala.swing.event.*

class GUI extends SimpleSwingApplication {
  //  println("Welcome to the Renewable Energy Plant System!")
  //  SystemStartup.startup()
  
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
    
    def alertMessage(header: String, message: String): Unit = {
      Dialog.showMessage(
        contents.head,
        message,
        title = header
      )
    }
    
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
        val analysisButton = new Button("Analysis selected data")
        layout(analysisButton) = c
        
        // Results area
        c.gridy += 1
        val resultsArea: TextArea = new TextArea(8, 20) {
          editable = false
        }
        layout(resultsArea) = c
        
        c.gridy += 1
        val simulateIssuesButton = new Button("Simulate Issues")
        if dataType != "4" then
          layout(simulateIssuesButton) = c
        
        listenTo(analysisButton)
        reactions += {
          case ButtonClicked(`analysisButton`) =>
            val startDate = input1.text
            val endDate = input2.text
            val timeSpan = timeSpanSelector.selection.item
            
            try {
              if (startDate == endDate) {
                Dialog.showMessage(
                  contents.head,
                  "Start date cannot be the same as end date.",
                  title = "Error"
                )
              } else if (compareDate(dateFormat.parse(endDate), dateFormat.parse(startDate))) {
                Dialog.showMessage(
                  contents.head,
                  "Start date cannot be later than the end date.",
                  title = "Error"
                )
              } else if (validateDate(startDate) == "true" && validateDate(endDate) == "true") {
                val results = DataAnalysis.analyzeData(startDate, endDate, timeSpan, dataType)
                if results._6 != "Nothing" then Dialog.showMessage(
                  contents.head,
                  results._6,
                  title = "Error"
                ) else resultsArea.text = s"Mean: ${results._1}\nMedian: ${results._2}\nMode: ${results._3}\nRange: ${results._4}\nMidrange: ${results._5}"
              } else if (validateDate(startDate) != "true") {
                //              resultsArea.text = "Invalid date format. Please enter the date in the format 'DD/MM/YYYY'.\nFor example, enter '12/04/2024' for April 12, 2024."
                Dialog.showMessage(
                  contents.head,
                  validateDate(startDate),
                  title = "Date Format Error"
                )
              } else if (validateDate(endDate) != "true") {
                //              resultsArea.text = "Invalid date format. Please enter the date in the format 'DD/MM/YYYY'.\nFor example, enter '12/04/2024' for April 12, 2024."
                Dialog.showMessage(
                  contents.head,
                  validateDate(endDate),
                  title = "Date Format Error"
                )
              }
            } catch {
              case _: java.text.ParseException => Dialog.showMessage(
                contents.head,
                "Invalid date format. Please enter the date in the format 'DD/MM/YYYY'.\nFor example, enter '12/04/2024' for April 12, 2024." +
                  "\nOr the date you selected is not within the range, please limit your query to between December 1, 2023 and April 30, 2024",
                title = "Date Format Error"
              )
            }
          
        }
        
        listenTo(simulateIssuesButton)
        reactions += {
          case ButtonClicked(`simulateIssuesButton`) =>
            val message = SimulateIssues.detectErrors(dataType)
            val errorImage = new Label {
              icon = new ImageIcon(dataType match {
                case "1" => "./charts/errorWind.png"
                case "2" => "./charts/errorSolar.png"
                case "3" => "./charts/errorHydro.png"
              })
              verticalTextPosition = Alignment.Bottom
              horizontalTextPosition = Alignment.Center
            }
            
            val messageLabel = new Label(message) {
              font = new Font("Serif", Font.BOLD, 16) // Set the font family, style, and size
              foreground = Color.red // Set the text color to red
              horizontalAlignment = Alignment.Center // Center the text horizontally within the label
            }
            
            val panel = new BoxPanel(Orientation.Vertical) {
              contents += messageLabel
              contents += Swing.VStrut(10) // Add 10 pixels of vertical space
              contents += errorImage
              border = Swing.EmptyBorder(10)
            }
            
            val dialog = new Dialog {
              contents = panel
              resizable = false
              title = "Results"
              modal = true
            }
            
            dialog.centerOnScreen()
            dialog.open()
        }
        
        
      }
      controls
    }
    
    val dateFormat = new java.text.SimpleDateFormat("dd/MM/yyyy")
    val startDateParsed: Date = dateFormat.parse("30/11/2023")
    val endDateParsed: Date = dateFormat.parse("01/05/2024")
    
    def compareDate(date1: Date, date2: Date): Boolean = {
      val comparisonResult = date1.compareTo(date2)
      if comparisonResult < 0 then true
      else if comparisonResult > 0 then false
      else true
    }
    
    private def validateDate(dateStr: String): String = {
      dateFormat.setLenient(false)
      try {
        if compareDate(dateFormat.parse(dateStr), startDateParsed) || compareDate(endDateParsed, dateFormat.parse(dateStr)) then
          "The date you selected is not within the range, please limit your query to between December 1, 2023 and April 30, 2024"
        else
          try {
            dateFormat.parse(dateStr)
            "true"
          } catch {
            case _: java.text.ParseException => "Invalid date format. Please enter the date in the format 'DD/MM/YYYY'.\nFor example, enter '12/04/2024' for April 12, 2024."
          }
      } catch {
        case _: java.text.ParseException => "Invalid date format. Please enter the date in the format 'DD/MM/YYYY'.\nFor example, enter '12/04/2024' for April 12, 2024."
      }
    }
    
    // Dummy implementation of analyzeData function
    //    private def analyzeData(startDate: String, endDate: String, timeFrame: String, dataType: String): (Double, Double, Double, Double, Double) = {
    //      // Perform actual analysis here and replace the following dummy values
    //      DataAnalysis.analyzeData(startDate, endDate, timeFrame, dataType)
    ////      (1.0, 1.0, 1.0, 1.0, 1.0)
    //    }
  }
}
