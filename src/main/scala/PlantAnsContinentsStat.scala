
case class PlantsWithContinents(country_code:String,  year_of_capacity_data: String, continentCode: String, continentName: String)
class PlantAnsContinentsStat(val csvPlantReader: CsvPlantReader, val csvContinentsReader: CsvContinentsReader) {
  val Plants=csvPlantReader.readData()
  val countriesAndContinents=csvContinentsReader.readFile()

  def printPlant(ind: Int): Unit ={
    print(Plants(ind))
  }


  def getPlantsInEachContinent()= {
    val temp=Plants.groupBy(_.country_code).mapValues(_.length)
    val out=new Array[PlantsWithContinents](Plants.size)

    for (i<-0 until Plants.size-1){
      val cont=countriesAndContinents.filter(_.country_code==Plants(i).country_code)
      out(i)=PlantsWithContinents(Plants(i).country_code,Plants(i).year_of_capacity_data, cont(0).ContinentCode, cont(0).ContinentName)
    }
    out.groupBy(_.continentName).mapValues(_.length)

  }

  def getCountryWithMinGasPlants()={
    val filtered=Plants.filter(_.fuel1s.contains("Gas"))
    filtered.groupBy(_.country_long).mapValues(_.length).minBy(_._2)
  }


}

