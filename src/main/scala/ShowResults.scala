import com.typesafe.scalalogging.LazyLogging
import com.typesafe.config.ConfigFactory
import scala.collection.immutable.HashMap
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}
import java.io.File

import DataFormats._
import com.itechart.bigdata.utils.CsvReader
import com.itechart.bigdata.utils.CsvWriter._
import org.slf4j.LoggerFactory

object ShowResults extends App {

  implicit val ec: ExecutionContext = ExecutionContext.global
  System.setProperty("logback.configurationFile", getClass.getResource("/logback.xml").toString)
  lazy val logger = LoggerFactory.getLogger(this.getClass)
  logger.info("Start main application...")
  val filesConfig = ConfigFactory.load("app.conf")
  val pathToDataFolder = filesConfig.getString("file.inputPath")
  val pathToResultFolder = filesConfig.getString("file.outputPath")

    val theDir = new File(pathToDataFolder)
    if (!theDir.exists()) {
      try {
        theDir.mkdir
      } catch {
        case se: SecurityException =>
          logger.info("Failed to create directory")
        //handle it
      }
    }

  val listInputFilesGlobalPlants = filesConfig.getStringList("file.input.InputFilesGlobalPlants").toArray.toList.map(pathToDataFolder + _)
  val inputFileGlobalContinents = pathToDataFolder + filesConfig.getString("file.input.inputFileGlobalContinents")
  val outputFilePowerPlants = pathToResultFolder + filesConfig.getString("file.output.outputFilePowerPlants")
  val outputFileGeoStat = pathToResultFolder + filesConfig.getString("file.output.outputFileGeoStat")
  val generalStatResults = Future.sequence(Seq(
    getTotalPowerFromAll(listInputFilesGlobalPlants),
    getAnswer(listInputFilesGlobalPlants, "The year in which the largest number of power plants was put into operation", PlantStat.getYearWithMaxPlantsOpened, maxBy),
    getAnswer(listInputFilesGlobalPlants, "Countries with the most gas power plants", PlantStat.getCountryWithMaxGasPlants, maxBy),
    getAnswer(listInputFilesGlobalPlants, "Countries with the least gas power plants", PlantStat.getCountryWithMinGasPlants, minBy),
  )).map(values => writeGeneralStat(values, outputFilePowerPlants))

  val writeSucessfulGeoStat = getTotalPlantsInEachContinent(listInputFilesGlobalPlants, inputFileGlobalContinents).map(value => writePlantsInEachContinent(value, outputFileGeoStat))
  Await.ready(Future.sequence(Seq(generalStatResults, writeSucessfulGeoStat)), Duration.Inf)
  logger.info("Sucessfully ended")




  def getTotalPowerFromAll(files: List[String], description: String = "Total capacity of all existing power plants"): Future[Answer] = {
    logger.info("Calculating total installed capacity of all operating power plants")
    Future.sequence(files.map(path => Future(PlantStat.getTotalPower(CsvReader.readData(path).map(mapperForPlantsData))))
    ).map(values => {
      Answer(description, values.sum)
    })
  }

  def minBy(map: HashMap[Any, Int]): Int = {
    map.minBy(_._2)._2
  }

  def maxBy(map: HashMap[Any, Int]): Int = {
    map.maxBy(_._2)._2
  }

  def getAnswer(files: List[String], description: String, funcForRaw: Seq[PlantsData] => HashMap[Any, Int],
                funcAfterMerge: HashMap[Any, Int] => Int): Future[Answer] = {
    logger.info("Searching for " + description)
    Future.sequence(files
      .map(path => Future(funcForRaw(CsvReader.readData(path).map(mapperForPlantsData))))
    ).map(values => {
      val allCountries = values.fold(HashMap())((l, r) => l.merged(r) {
        case ((k1, v1), (k2, v2)) => (k1, v1 + v2)
      })
      val searchedValue = funcAfterMerge(allCountries)
      Answer(description, allCountries.filter(_._2 == searchedValue).keys.toList.mkString(", "))
    })
  }

  def getTotalPlantsInEachContinent(files: List[String], file2: String): Future[HashMap[String, Int]] = {
    logger.info("Calcilating the number of power plants on each continent")
    Future.sequence(files
      .map(path => Future(PlantStat.getPlantsInEachContinent(CsvReader.readData(path).map(mapperForPlantsData), CsvReader.readData(file2).map(mapperForContinents))))
    ).map(values => {
      val allCountries = values.fold(HashMap())((l, r) => l.merged(r) {
        case ((k1, v1), (k2, v2)) => (k1, v1 + v2)
      })
      allCountries
    })
  }


}