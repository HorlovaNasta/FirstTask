import ContinentInfoFields.{continentName, countryThreeLetterCode}
import GeneralStatsFields._

object DataFormats {

  case class PlantsData(country_code: String, country_long: String, capacity_MV: Double, commissioning_year: String, fuels: Array[String])
  case class CountriesAndContinentsData(Continent_Name: String, Three_Letter_Country_Code: String)
  case class Answer(description: String, answer: Any)

  def mapperForPlantsData(value: Map[String, String]): PlantsData = {
    PlantsData(value(countryCode).toString, value(countryName).toString, value(capacityOfStation).toDouble, value(commissioningYear).toString, Array(value(fuelNames(0)), value(fuelNames(1)), value(fuelNames(2)), value(fuelNames(3))))
  }

  def mapperForContinents(value: Map[String, String]): CountriesAndContinentsData = {
    CountriesAndContinentsData(value(continentName).toString, value(countryThreeLetterCode).toString)
  }
}
