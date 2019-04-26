import com.github.tototoshi.csv.CSVWriter
import com.typesafe.scalalogging.LazyLogging

import scala.collection.immutable.HashMap
import scala.concurrent.{ExecutionContext, Future}
import DataFormats._
import Configuration._
object CsvWriter extends LazyLogging {
  implicit val ec: ExecutionContext = ExecutionContext.global

  def writePlantsInEachContinent(answer: HashMap[String, Int], outputFile: String = outputFileGeoStat, header: List[String] = List("Continent", "NumberOfPlants")): Future[Boolean] = {
    logger.info(s"Writing data into file $outputFile")
    val writer = CSVWriter.open(outputFile)
    writer.writeRow(header)
    writer.writeAll(answer.toSeq.map(value => List(value._1, value._2)))
    writer.close()
    Future(true)
  }

  def writeGeneralStat(answer: Seq[Answer], outputFile: String = outputFilePowerPlants, header: List[String] = List("Description", "Answer")): Future[Boolean]  = {
    logger.info(s"Writing data into file $outputFile")
    val writer = CSVWriter.open(outputFile)
    writer.writeRow(header)
    writer.writeAll(answer.map(value=>List(value.description, value.answer)))
    writer.close()
    Future(true)
  }


}
