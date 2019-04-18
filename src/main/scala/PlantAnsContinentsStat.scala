import com.github.tototoshi.csv.CSVWriter

case class PlantsWithContinents(country_code:String,  capacity_MV: Double, Continent_Name: String)

class PlantAnsContinentsStat(val csvPlantReader: CsvPlantReader, val csvContinentsReader: CsvContinentsReader) {
  private val Plants=csvPlantReader.readData()
  private val countriesAndContinents=csvContinentsReader.readFile()


  def getPlantsInEachContinent(fileName:String):Unit= {
    def matchContinentAndCountry(plantsData: PlantsData)={
      val tmp1=countriesAndContinents.find(_.Three_Letter_Country_Code==plantsData.country_code)
      if (tmp1.isDefined) {
        tmp1.get.Continent_Name
      }
      else{
        ""
      }
    }

    val combined=Plants.map(value =>  PlantsWithContinents(value.country_code, value.capacity_MV, Continent_Name = matchContinentAndCountry(value))).filter(_.Continent_Name!="").groupBy(_.Continent_Name).mapValues(_.length).toSeq.map(value=>List(value._1, value._2))
    val writer = CSVWriter.open(fileName)
    writer.writeRow(List("Continent", "NumberOfPlants"))
    writer.writeAll(combined)
    writer.close()

  }



}

