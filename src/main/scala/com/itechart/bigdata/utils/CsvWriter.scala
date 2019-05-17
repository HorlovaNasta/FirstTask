package com.itechart.bigdata.utils

import com.github.tototoshi.csv.CSVWriter
import com.typesafe.scalalogging.LazyLogging

import scala.collection.immutable.HashMap
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}
import org.slf4j.LoggerFactory

object CsvWriter {
  private lazy val logger = LoggerFactory.getLogger(this.getClass)
  case class Answer(description: String, answer: Any)

  implicit val ec: ExecutionContext = ExecutionContext.global

  def writePlantsInEachContinent(answer: HashMap[String, Int], outputFile: String, header: List[String] = List("Continent", "NumberOfPlants")): Future[Boolean] = {
    logger.info(s"Writing data into file $outputFile")
    Try {
      val writer = CSVWriter.open(outputFile)
      writer.writeRow(header)
      writer.writeAll(answer.toSeq.map(value => List(value._1, value._2)))
      writer.close()

    } match {
      case Success(_) => Future(true)
      case Failure (_) => {
        logger.info("Failed to write into file")
        Future(false)
      }

    }
  }

  def writeGeneralStat(answer: Seq[Answer], outputFile: String, header: List[String] = List("Description", "Answer")): Future[Boolean] = {
    logger.info(s"Writing data into file $outputFile")
    val writer = CSVWriter.open(outputFile)
    writer.writeRow(header)

    writer.writeAll(answer.map(value => List(value.description, value.answer)))
    writer.close()
    Future(true)
  }


}
