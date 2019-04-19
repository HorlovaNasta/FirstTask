import com.github.tototoshi.csv.CSVWriter
import scala.concurrent.{Await, ExecutionContext, Future}

import java.nio.file.{Files, FileSystems}

object ShowResults{

  def main(args:Array[String]): Unit={
    val inputFileGlobalPlants="./src/main/resources/sources/global_power_plant_database.csv"
    val inputFileGlobalContinents="./src/main/resources/sources/data.csv"
    val outputFilePowePlants="./src/main/resources/sources/powerplants.csv"
    val outputFileGeoStat="./src/main/resources/sources/geo-stats.csv"
    val csvPlantsData=CsvReader.readData(inputFileGlobalPlants).map(mapperForPlantsData)
    val statPlants=new PlantStat()
    val csvCountriesAndContinentName=CsvReader.readData(inputFileGlobalContinents).map(mapperForContinents)

    var totalCapacity=statPlants.getTotalPower(csvPlantsData)
    writePlantsStat("Total  capacity of all existing power plants",totalCapacity,outputFilePowePlants , false)

    val maxGasPlants=statPlants.getCountryWithMAxGasPlants(csvPlantsData)
    writePlantsStat("Countries with the most gas power plants",maxGasPlants, outputFilePowePlants)

    val minGasPlants=statPlants.getCountryWithMinGasPlants(csvPlantsData)
    writePlantsStat("Countries with the least gas power plants",minGasPlants, outputFilePowePlants)

    val yearWithMaxNumOfPlants=statPlants.getYearWirhMaxPlantsOpened(csvPlantsData)
    writePlantsStat("The year in which the largest number of power plants was put into operation",minGasPlants,outputFilePowePlants)

    val PlantsInEachContinent= statPlants.getPlantsInEachContinent(csvPlantsData, csvCountriesAndContinentName)
    writePlantsInEachContinent(PlantsInEachContinent,outputFileGeoStat)
  }

  def writePlantsInEachContinent(answer:Seq[List[Any]],  outputFile: String, header: List[String]=List("Continent", "NumberOfPlants")): Unit ={
    val writer = CSVWriter.open(outputFile)
    writer.writeRow(header)
    writer.writeAll(answer)
    writer.close()
  }

  def writePlantsStat(description:String, answer : Any, outputFile: String,  appendNewRow: Boolean=true, header: List[String]=List("Description", "Answer")): Unit={
    val writer = CSVWriter.open(outputFile, append=appendNewRow)
    if (!appendNewRow){
      writer.writeRow(header)
    }
    writer.writeRow(Seq(description,answer))
    writer.close()
  }

  def mapperForPlantsData(value:Map[String,String]):PlantsData={
    PlantsData(value("country").toString, value("country_long").toString, value("capacity_mw").toDouble, value("commissioning_year").toString, Array(value("fuel1"),value("fuel2"),value("fuel3"),value("fuel4")))
  }

  def mapperForContinents(value:Map[String,String]):CountriesAndContinentsData={
    CountriesAndContinentsData(value("Continent_Name").toString, value("Continent_Code").toString, value("Three_Letter_Country_Code").toString)
  }

}