import com.github.tototoshi.csv.CSVReader

trait CsvReader {
  /**
    * @return A [[Seq]] containing all the sales.
    */
  def readData(): Seq[PlantsData]

}


case class PlantsData(country_code:String, country_long: String, capacity_MV: Double,  commissioning_year:String, fuel1s:Array[String])

class CsvPlantReader (val fileName: String) extends CsvReader {
  override def readData(): Seq[PlantsData] =  {
    val reader=CSVReader.open(fileName)
    val cc=reader.allWithHeaders()
    reader.close()
    cc.map(value=> PlantsData(value("country").toString, value("country_long").toString, value("capacity_mw").toDouble, value("commissioning_year").toString, Array(value("fuel1"),value("fuel2"),value("fuel3"),value("fuel4"))))
  }
}

