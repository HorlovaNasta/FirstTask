

trait CsvContReader {
  /**
    * @return A [[Seq]] containing all the sales.
    */
  def readFile(): Seq[CountriesAndContinentsData]
}

case class CountriesAndContinentsData(ContinentName:String, ContinentCode: String, country_code: String)

class CsvContinentsReader (val fileName: String) extends CsvContReader {
  override def readFile(): Seq[CountriesAndContinentsData] =  {
    for {
      line <- io.Source.fromFile(fileName).getLines().drop(1).toVector
      values = line.split(",", -1).map(_.trim)
    } yield CountriesAndContinentsData(values(0), values(1), values(5))
  }
}