import com.github.tototoshi.csv.CSVWriter

import scala.collection.mutable

import scala.collection.immutable.HashMap


object PlantStat{

  val gasName="Gas"
  def getTotalPower(Plants: Seq[PlantsData]):Double={
    Plants.map(_.capacity_MV).sum
  }
  def getCountryWithMaxGasPlants(Plants: Seq[PlantsData]): HashMap[String, Int]={
    val filtered=Plants.filter(_.fuels.contains(gasName)).groupBy(_.country_long).mapValues(_.length)
    val maxCountry=filtered.maxBy(_._2)._2
    HashMap(filtered.filter(_._2==maxCountry).toList:_*)
  }

  def getCountryWithMinGasPlants(Plants: Seq[PlantsData]): HashMap[String, Int]={
    val filtered=Plants.filter(_.fuels.contains(gasName)).groupBy(_.country_long).mapValues(_.length)
    val minCountry=filtered.minBy(_._2)._2
    HashMap(filtered.filter(_._2==minCountry).toList:_*)
  }
  def getYearWithMaxPlantsOpened(Plants: Seq[PlantsData]): HashMap[Int, Int]={

    val filtered=Plants.filter(!_.commissioning_year.isEmpty).map(value =>   (value.country_code, value.commissioning_year.toDouble.toInt)).groupBy(_._2).mapValues(_.length)
    val maxYearValue=filtered.maxBy(_._2)._2
    HashMap(filtered.filter(_._2 == maxYearValue).toList:_*)

  }

  def getPlantsInEachContinent(Plants: Seq[PlantsData], countriesAndContinents:Seq[CountriesAndContinentsData]):HashMap[String, Int]= {
    def matchContinentAndCountry(plantsData: PlantsData):String={
      countriesAndContinents.find(_.Three_Letter_Country_Code==plantsData.country_code).getOrElse(CountriesAndContinentsData("","")).Continent_Name
    }
    HashMap(Plants.map(value =>  (value.country_code, value.capacity_MV,  matchContinentAndCountry(value)))
      .filter(!_._3.isEmpty)
      .groupBy(_._3).mapValues(_.length).toSeq.toList:_*)
  }


}

