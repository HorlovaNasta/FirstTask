import com.typesafe.scalalogging.LazyLogging
import com.typesafe.config.ConfigFactory
import scala.collection.immutable.HashMap
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}



import DataFormats._
import com.itechart.bigdata.utils.CsvReader
import com.itechart.bigdata.utils.CsvWriter._


object ShowResults extends LazyLogging {

  def main(args: Array[String]): Unit = {

    logger.info("Start main application...")
    val filesConfig = ConfigFactory.load("app.conf")
    val pathToDataFolder = filesConfig.getString("file.inputPath")
    val pathToResultFolder = filesConfig.getString("file.outputPath")
    val listInputFilesGlobalPlants=filesConfig.getStringList("file.input.InputFilesGlobalPlants").toArray.toList.map(pathToDataFolder+_)
    val inputFileGlobalContinents=pathToDataFolder+filesConfig.getString("file.input.inputFileGlobalContinents")
    val outputFilePowerPlants = pathToResultFolder+filesConfig.getString("file.output.outputFilePowerPlants")
    val outputFileGeoStat = pathToResultFolder+filesConfig.getString("file.output.outputFileGeoStat")

    val generalStatResults = Future.sequence(Seq(
      getTotalPowerFromAll(listInputFilesGlobalPlants),
      getTotalYearsWithMaxNumOfPlants(listInputFilesGlobalPlants),
      getTotalcountriesWithMaxGasPlants(listInputFilesGlobalPlants),
      getTotalcountriesWithMinGasPlants(listInputFilesGlobalPlants)
    )).map(values=>writeGeneralStat(values, outputFilePowerPlants))

    val writeSucessfulGeoStat = getTotalPlantsInEachContinent(listInputFilesGlobalPlants, inputFileGlobalContinents).map(value=>writePlantsInEachContinent(value, outputFileGeoStat))
    Await.ready(Future.sequence(Seq(generalStatResults, writeSucessfulGeoStat)), Duration.Inf)
    logger.info("Sucessfully ended")
  }
  implicit val ec: ExecutionContext = ExecutionContext.global

  def getTotalPowerFromAll(files: List[String], description: String="Total capacity of all existing power plants"): Future[Answer] = {
    logger.info("Calculating total installed capacity of all operating power plants")
    Future.sequence(files.map(path => Future(PlantStat.getTotalPower(CsvReader.readData(path).map(mapperForPlantsData))))
    ).map(values => {
      Answer(description,values.sum)
    })
  }

  def getTotalYearsWithMaxNumOfPlants(files: List[String], description: String="The year in which the largest number of power plants was put into operation"): Future[Answer] = {
    logger.info("Searching for years in which the largest number of power plants was put into operation")
    Future.sequence(files
      .map(path => Future(PlantStat.getYearWithMaxPlantsOpened(CsvReader.readData(path).map(mapperForPlantsData))))
    ).map(values => {
      val allYears = values.fold(HashMap())((l, r) => l.merged(r) {
        case ((k1, v1), (k2, v2)) => (k1, v1 + v2)
      })
      val maxYear = allYears.maxBy(_._2)._2
      Answer(description, allYears.filter(_._2 == maxYear).keys.toList)
    })
  }

  def getTotalcountriesWithMaxGasPlants(files:  List[String], description: String="Countries with the most gas power plants"): Future[Answer] = {
    logger.info("Searching for countries with the largest amount of gas power plants")

    Future.sequence(files
      .map(path => Future(PlantStat.getCountryWithMaxGasPlants(CsvReader.readData(path).map(mapperForPlantsData))))
    ).map(values => {
      val allCountries = values.fold(HashMap())((l, r) => l.merged(r) {
        case ((k1, v1), (k2, v2)) => (k1, v1 + v2)
      })

      val maxCountry = allCountries.maxBy(_._2)._2
      Answer(description, allCountries.filter(_._2 == maxCountry).keys.toList)
    })
  }

  def getTotalcountriesWithMinGasPlants(files:  List[String], description: String="Countries with the least gas power plants"): Future[Answer] = {
    logger.info("Searching for countries with the smallest amount of gas power plants")
    Future.sequence(files
      .map(path => Future(PlantStat.getCountryWithMinGasPlants(CsvReader.readData(path).map(mapperForPlantsData))))
    ).map(values => {
      val allCountries = values.fold(HashMap())((l, r) => l.merged(r) {
        case ((k1, v1), (k2, v2)) => (k1, v1 + v2)
      })
      val minCountry = allCountries.minBy(_._2)._2
      Answer(description, allCountries.filter(_._2 == minCountry).keys.toList)
    })
  }

  def getTotalPlantsInEachContinent(files:  List[String],file2: String): Future[HashMap[String, Int]]= {
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