import com.github.tototoshi.csv.CSVWriter

case class PlantsData(country_code:String, country_long: String, capacity_MV: Double,  commissioning_year:String, fuel1s:Array[String])
case class CountriesAndContinentsData(Continent_Name:String, Continent_Code: String,Three_Letter_Country_Code: String)

class PlantStat() {

  def getTotalPower(Plants: Seq[PlantsData]):Double={
    Plants.map(_.capacity_MV).sum
  }
  def getCountryWithMAxGasPlants(Plants: Seq[PlantsData]): String={
    val filtered=Plants.filter(_.fuel1s.contains("Gas"))
    filtered.groupBy(_.country_long).mapValues(_.length).maxBy(_._2)._1

  }

  def getCountryWithMinGasPlants(Plants: Seq[PlantsData]):String={
    val filtered=Plants.filter(_.fuel1s.contains("Gas"))
    filtered.groupBy(_.country_long).mapValues(_.length).minBy(_._2)._1
  }
  def getYearWirhMaxPlantsOpened(Plants: Seq[PlantsData]): Int ={
    case class Commissioning_year(year: Int)
    Plants.filter(_.commissioning_year!="").map(value =>  Commissioning_year(value.commissioning_year.toDouble.toInt)).groupBy(_.year).mapValues(_.length).maxBy(_._2)._1
  }

  def getPlantsInEachContinent(Plants: Seq[PlantsData], countriesAndContinents:Seq[CountriesAndContinentsData]):Seq[List[Any]]= {
    def matchContinentAndCountry(plantsData: PlantsData):String={
      val tmp1=countriesAndContinents.find(_.Three_Letter_Country_Code==plantsData.country_code)
      if (tmp1.isDefined) {
        tmp1.get.Continent_Name
      }
      else{
        ""
      }
    }
    case class PlantsWithContinents(country_code:String,  capacity_MV: Double, Continent_Name: String)
    val combined=Plants.map(value =>  PlantsWithContinents(value.country_code, value.capacity_MV, Continent_Name = matchContinentAndCountry(value)))
      .filter(_.Continent_Name!="")
      .groupBy(_.Continent_Name).mapValues(_.length).toSeq.map(value=>List(value._1, value._2))
    combined
  }



}

