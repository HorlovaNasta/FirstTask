object Hello extends App {


  val csvPlantsData=new CsvPlantReader("./src/main/resources/sources/global_power_plant_database.csv")
  val statPlants=new PlantStat(csvPlantsData)

  val csvContData=new CsvContinentsReader("./src/main/resources/sources/data.csv")
  val statPlantsAndCont=new PlantAnsContinentsStat(csvPlantsData, csvContData)


  println(statPlants.getTotalPower())
  println(statPlantsAndCont.getPlantsInEachContinent())
  println(statPlants.getCountryWithMAxGasPlants())

  
  println(statPlants.getCountryWithMinGasPlants())
  println(statPlants.getYear())


}