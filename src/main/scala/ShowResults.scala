
object ShowResults extends App {

  val csvPlantsData=new CsvPlantReader("./src/main/resources/sources/global_power_plant_database.csv")
  val statPlants=new PlantStat(csvPlantsData)

  val csvContData=new CsvContinentsReader("./src/main/resources/sources/data.csv")
  val statPlantsAndCont=new PlantAnsContinentsStat(csvPlantsData, csvContData)


  println("Total  capacity of all existing power plants: "+statPlants.getTotalPower())
  println("Countries with the most gas power plants: "+statPlants.getCountryWithMAxGasPlants()._1)
  println("Countries with the least gas power plants: : "+statPlants.getCountryWithMinGasPlants()._1)
  println("The year in which the largest number of power plants was put into operation: "+statPlants.getYearWirhMaxPlantsOpened()._1)
  statPlantsAndCont.getPlantsInEachContinent("./src/main/resources/sources/out.csv")

}