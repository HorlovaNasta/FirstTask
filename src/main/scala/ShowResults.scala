import com.github.tototoshi.csv.CSVWriter
import scala.concurrent.{Await, ExecutionContext, Future}

import scala.collection.JavaConverters._
import scala.collection.immutable.HashMap
import scala.collection.mutable.{ArrayBuffer, ListBuffer}
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.io._
import scala.util._

import java.nio.file.{Files, FileSystems}

object ShowResults{

  def main(args:Array[String]): Unit={
    implicit val ec: ExecutionContext = ExecutionContext.global
    val inputFileGlobalPlants="./src/main/resources/sources/global_power_plant_database.csv"
    val inputFileGlobalContinents="./src/main/resources/sources/data.csv"
    val outputFilePowePlants="./src/main/resources/sources/powerplants.csv"
    val outputFileGeoStat="./src/main/resources/sources/geo-stats.csv"
    val inputFileGlobalPlantsPart1="./src/main/resources/sources/xaa.csv"
    val inputFileGlobalPlantsPart2="./src/main/resources/sources/xab.csv"
    val inputFileGlobalPlantsPart3="./src/main/resources/sources/xac.csv"

    //parallelTotalPower(inputFileGlobalPlantsPart1, inputFileGlobalPlantsPart2, inputFileGlobalPlantsPart3)
    Await.ready(Future.sequence(Seq(
      getTotalPowerFromAll(inputFileGlobalPlantsPart1, inputFileGlobalPlantsPart2, inputFileGlobalPlantsPart3,outputFilePowePlants)
    )), Duration.Inf)
//    val csvPlantsData=CsvReader.readData(inputFileGlobalPlants).map(mapperForPlantsData)
//    val csvCountriesAndContinentName=CsvReader.readData(inputFileGlobalContinents).map(mapperForContinents)
//
//    val statPlants=new PlantStat()


//    val totalCapacity=statPlants.getTotalPower(csvPlantsData)
//    writePlantsStat("Total  capacity of all existing power plants",totalCapacity,outputFilePowePlants , false)
//
//    val maxGasPlants=statPlants.getCountryWithMAxGasPlants(csvPlantsData)
//    writePlantsStat("Countries with the most gas power plants",maxGasPlants, outputFilePowePlants)
//
//    val minGasPlants=statPlants.getCountryWithMinGasPlants(csvPlantsData)
//    writePlantsStat("Countries with the least gas power plants",minGasPlants, outputFilePowePlants)
//
//    val yearWithMaxNumOfPlants=statPlants.getYearWirhMaxPlantsOpened(csvPlantsData)
//    writePlantsStat("The year in which the largest number of power plants was put into operation",minGasPlants,outputFilePowePlants)
//
//    val PlantsInEachContinent= statPlants.getPlantsInEachContinent(csvPlantsData, csvCountriesAndContinentName)
//    writePlantsInEachContinent(PlantsInEachContinent,outputFileGeoStat)
  }
//  def parallelTotalPower(file1: String, file2: String, file3: String: Future[List[Double]]=
//    Future.sequence(List(file1, file2,file3 ))
//    .map(path=>Future(futureTotalPower(path)))
//  )

  implicit val ec: ExecutionContext = ExecutionContext.global
  
  def getTotalPowerFromAll (file1: String, file2: String, file3: String, outputFilePowePlants:String): Future[List[Double]]=
    Future.sequence(List(file1, file2, file3)
      .map(path => Future(PlantStat.getTotalPower(CsvReader.readData(path).map(mapperForPlantsData))))
    ) andThen {
      case Success(values) => {
        writePlantsStat("Total  capacity of all existing power plants",values.sum, outputFilePowePlants)
      }
      case Failure(e) => println("Failure")
  }



  def writePlantsInEachContinent(answer:Seq[List[Any]],  outputFile: String, header: List[String]=List("Continent", "NumberOfPlants")): Unit ={
    val writer = CSVWriter.open(outputFile)
    writer.writeRow(header)
    writer.writeAll(answer)
    writer.close()
  }

  def writePlantsStat(description:String, answer : Any, outputFile: String): Unit={

    val writer = CSVWriter.open(outputFile, append=true)
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