import com.github.tototoshi.csv.CSVWriter
import scala.concurrent.{Await, ExecutionContext, Future}
import com.typesafe.scalalogging.LazyLogging
import scala.collection.JavaConverters._
import scala.collection.immutable.HashMap
import scala.collection.mutable.{ArrayBuffer, ListBuffer}
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.io._
import scala.util._
import CsvWriter.{writeCountriesWithMaxGasPlants, writeCountriesWithMinGasPlants, writeHeaderForPowerPlants, writePlantsInEachContinent, writeTotalPower, writeYearWithMaxNumOfPlants, outputFilePowerPlants, outputFileGeoStat}

import java.nio.file.{Files, FileSystems}

case class PlantsData(country_code: String, country_long: String, capacity_MV: Double, commissioning_year: String, fuels: Array[String])

case class CountriesAndContinentsData(Continent_Name: String, Three_Letter_Country_Code: String)

object ShowResults {

  val countryCodeInCSV = "country"
  val countryNameInCSV = "country_long"
  val capacityInCSV = "capacity_mw"
  val commissioningYearInCSV = "commissioning_year"
  val fuelNamesInCSV = Array("fuel1", "fuel2", "fuel3", "fuel4")

  val continentNameInCSV2 = "Continent_Name"
  val continentCodeInCSV2 = "Continent_Code"
  val countryCodeInCSV2 = "Three_Letter_Country_Code"
  val inputFileGlobalPlants = "./src/main/resources/sources/global_power_plant_database.csv"
  val inputFileGlobalContinents = "./src/main/resources/sources/data.csv"


  val inputFileGlobalPlantsPart1 = "./src/main/resources/sources/xaa.csv"
  val inputFileGlobalPlantsPart2 = "./src/main/resources/sources/xab.csv"
  val inputFileGlobalPlantsPart3 = "./src/main/resources/sources/xac.csv"


  def main(args: Array[String]): Unit = {

    writeHeaderForPowerPlants(outputFilePowerPlants)
    Await.ready(Future.sequence(Seq(
      getTotalPowerFromAll(inputFileGlobalPlantsPart1, inputFileGlobalPlantsPart2, inputFileGlobalPlantsPart3),
      getTotalYearsWithMaxNumOfPlants(inputFileGlobalPlantsPart1, inputFileGlobalPlantsPart2, inputFileGlobalPlantsPart3),
      getTotalcountriesWithMaxGasPlants(inputFileGlobalPlantsPart1, inputFileGlobalPlantsPart2, inputFileGlobalPlantsPart3),
      getTotalcountriesWithMinGasPlants(inputFileGlobalPlantsPart1, inputFileGlobalPlantsPart2, inputFileGlobalPlantsPart3),
      getTotalPlantsInEachContinent(inputFileGlobalPlantsPart1, inputFileGlobalPlantsPart2, inputFileGlobalPlantsPart3, inputFileGlobalContinents)
    )), Duration.Inf)


  }

  //  def parallelTotalPower(file1: String, file2: String, file3: String: Future[List[Double]]=
  //    Future.sequence(List(file1, file2,file3 ))
  //    .map(path=>Future(futureTotalPower(path)))
  //  )

  implicit val ec: ExecutionContext = ExecutionContext.global

  def getTotalPowerFromAll(file1: String, file2: String, file3: String, outputFile: String = outputFilePowerPlants): Future[List[Double]] =
    Future.sequence(List(file1, file2, file3)
      .map(path => Future(PlantStat.getTotalPower(CsvReader.readData(path).map(mapperForPlantsData))))
    ) andThen {
      case Success(values) => {
        writeTotalPower(values.sum, outputFile = outputFile)
      }
      case Failure(e) => println("Failure")
    }

  def getTotalYearsWithMaxNumOfPlants(file1: String, file2: String, file3: String, outputFile: String = outputFilePowerPlants): Future[Seq[HashMap[Int, Int]]] =
    Future.sequence(List(file1, file2, file3)
      .map(path => Future(PlantStat.getYearWithMaxPlantsOpened(CsvReader.readData(path).map(mapperForPlantsData))))
    ) andThen {
      case Success(values) => {
        val allYears = values.fold(HashMap())((l, r) => l.merged(r) {
          case ((k1, v1), (k2, v2)) => (k1, v1 + v2)
        })

        val maxYear = allYears.maxBy(_._2)._2
        writeYearWithMaxNumOfPlants(allYears.filter(_._2 == maxYear), outputFile = outputFile)
      }
      case Failure(e) => println("Failure")
    }

  def getTotalcountriesWithMaxGasPlants(file1: String, file2: String, file3: String, outputFile: String = outputFilePowerPlants): Future[Seq[HashMap[String, Int]]] =
    Future.sequence(List(file1, file2, file3)
      .map(path => Future(PlantStat.getCountryWithMaxGasPlants(CsvReader.readData(path).map(mapperForPlantsData))))
    ) andThen {
      case Success(values) => {
        val allCountries = values.fold(HashMap())((l, r) => l.merged(r) {
          case ((k1, v1), (k2, v2)) => (k1, v1 + v2)
        })

        val maxCountry = allCountries.maxBy(_._2)._2
        writeCountriesWithMaxGasPlants(allCountries.filter(_._2 == maxCountry), outputFile = outputFile)
      }
      case Failure(e) => println("Failure")
    }

  def getTotalcountriesWithMinGasPlants(file1: String, file2: String, file3: String, outputFile: String = outputFilePowerPlants): Future[Seq[HashMap[String, Int]]] =
    Future.sequence(List(file1, file2, file3)
      .map(path => Future(PlantStat.getCountryWithMinGasPlants(CsvReader.readData(path).map(mapperForPlantsData))))
    ) andThen {
      case Success(values) => {
        val allCountries = values.fold(HashMap())((l, r) => l.merged(r) {
          case ((k1, v1), (k2, v2)) => (k1, v1 + v2)
        })
        val minCountry = allCountries.minBy(_._2)._2
        writeCountriesWithMinGasPlants(allCountries.filter(_._2 == minCountry), outputFile = outputFile)
      }
      case Failure(e) => println("Failure")
    }

  def getTotalPlantsInEachContinent(file1: String, file2: String, file3: String, file4: String, outputFile: String = outputFileGeoStat): Future[Seq[HashMap[String, Int]]] =
    Future.sequence(List(file1, file2, file3)
      .map(path => Future(PlantStat.getPlantsInEachContinent(CsvReader.readData(path).map(mapperForPlantsData), CsvReader.readData(file4).map(mapperForContinents))))
    ) andThen {
      case Success(values) => {
        val allCountries = values.fold(HashMap())((l, r) => l.merged(r) {
          case ((k1, v1), (k2, v2)) => (k1, v1 + v2)
        })
        writePlantsInEachContinent(allCountries, outputFile = outputFile)
      }
      case Failure(e) => println("Failure")
    }

  def mapperForPlantsData(value: Map[String, String]): PlantsData = {
    PlantsData(value(countryCodeInCSV).toString, value(countryNameInCSV).toString, value(capacityInCSV).toDouble, value(commissioningYearInCSV).toString, Array(value(fuelNamesInCSV(0)), value(fuelNamesInCSV(1)), value(fuelNamesInCSV(2)), value(fuelNamesInCSV(3))))
  }

  def mapperForContinents(value: Map[String, String]): CountriesAndContinentsData = {
    CountriesAndContinentsData(value(continentNameInCSV2).toString, value(countryCodeInCSV2).toString)
  }
}