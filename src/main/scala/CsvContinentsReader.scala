import com.github.tototoshi.csv.CSVReader

trait CsvReader2 {
  /**
    * @return A [[Seq]] containing all the sales.
    */
  def readFile(): Seq[CountriesAndContinentsData]

}


case class CountriesAndContinentsData(Continent_Name:String, Continent_Code: String,Three_Letter_Country_Code: String)

class CsvContinentsReader (val fileName: String)  extends CsvReader2 {
  override def readFile(): Seq[CountriesAndContinentsData] =  {
    val reader=CSVReader.open(fileName)
    val cc=reader.allWithHeaders()
    reader.close()
    cc.map(value=> CountriesAndContinentsData(value("Continent_Name").toString, value("Continent_Code").toString, value("Three_Letter_Country_Code").toString))
  }
}