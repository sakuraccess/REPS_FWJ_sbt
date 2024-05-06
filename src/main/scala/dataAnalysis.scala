class dataAnalysis {
  def average(data: List[(String, Double)]): Double = {
    if (data.isEmpty) 0.0
    else data.map(_._2).sum / data.length
  }

  def median(data: List[(String, Double)]): Double = {
    val sortedData = data.map(_._2).sorted
    if (sortedData.isEmpty) 0.0
    else if (sortedData.length % 2 != 0) sortedData(sortedData.length / 2)
    else (sortedData(sortedData.length / 2 - 1) + sortedData(sortedData.length / 2)) / 2.0
  }

  def mode(data: List[(String, Double)]): Double = {
    val frequencyMap = data.map(_._2).groupBy(identity).mapValues(_.size)
    val maxFreq = frequencyMap.values.max
    frequencyMap.collectFirst { case (value, freq) if freq == maxFreq => value }.getOrElse(0.0)
  }

  def range(data: List[(String, Double)]): Double = {
    if (data.isEmpty) 0.0
    else {
      val numericValues = data.map(_._2)
      numericValues.max - numericValues.min
    }
  }

  def midValue(data: List[(String, Double)]): Double = median(data)
}
