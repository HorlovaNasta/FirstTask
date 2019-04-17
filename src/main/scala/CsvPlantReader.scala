
trait CsvReader {
  /**
    * @return A [[Seq]] containing all the sales.
    */
  def readData(): Seq[PlantsData]

}

case class PlantsData(country_code:String, country_long: String, capacity_MV: Double, year_of_capacity_data: String, commissioning_year:String, fuel1s:Array[String])

class CsvPlantReader (val fileName: String) extends CsvReader {
  override def readData(): Seq[PlantsData] =  {
    for {
      line <- io.Source.fromFile(fileName).getLines().drop(1).toVector
      values = line.split(",", -1).map(_.trim)
    } yield PlantsData(values(0), values(1), values(4).toDouble, values(16),values(11), Array(values(7),values(8), values(9), values(10)))
  }
}

