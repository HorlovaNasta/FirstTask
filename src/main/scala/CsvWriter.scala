import com.github.tototoshi.csv.CSVWriter

import scala.collection.immutable.HashMap

object CsvWriter {
  val outputFilePowerPlants = "./src/main/resources/sources/powerplants.csv"
  val outputFileGeoStat = "./src/main/resources/sources/geo-stats.csv"
  def writeHeaderForPowerPlants(outputFile: String, header: List[String] = List("Description", "Answer")): Unit = {
    val writer = CSVWriter.open(outputFile)
    writer.writeRow(header)
  }

  def writePlantsInEachContinent(answer: HashMap[String, Int], outputFile: String = outputFileGeoStat, header: List[String] = List("Continent", "NumberOfPlants")): Unit = {
    val writer = CSVWriter.open(outputFile)
    writer.writeRow(header)
    writer.writeAll(answer.toSeq.map(value => List(value._1, value._2)))
    writer.close()
  }

  def writeTotalPower(answer: Double, description: String = "Total capacity of all existing power plants", outputFile: String = outputFilePowerPlants): Unit = {

    val writer = CSVWriter.open(outputFile, append = true)
    writer.writeRow(Seq(description, answer))
    writer.close()
  }

  def writeYearWithMaxNumOfPlants(answer: Map[Int, Int], description: String = "The year in which the largest number of power plants was put into operation", outputFile: String = outputFilePowerPlants): Unit = {
    val writer = CSVWriter.open(outputFile, append = true)
    writer.writeRow(Seq(description, answer.keys.toList))
    writer.close()
  }

  def writeCountriesWithMaxGasPlants(answer: Map[String, Int], description: String = "Countries with the most gas power plants", outputFile: String = outputFilePowerPlants): Unit = {
    val writer = CSVWriter.open(outputFile, append = true)
    writer.writeRow(Seq(description, answer.keys.toList))
    writer.close()
  }

  def writeCountriesWithMinGasPlants(answer: Map[String, Int], description: String = "Countries with the least gas power plants", outputFile: String = outputFilePowerPlants): Unit = {
    val writer = CSVWriter.open(outputFile, append = true)
    writer.writeRow(Seq(description, answer.keys.toList))
    writer.close()
  }


}
