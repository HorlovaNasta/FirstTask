class PlantStat(val csvPlantReader: CsvPlantReader) {
  val Plants=csvPlantReader.readData()

  def printPlant(ind: Int): Unit ={
    print(Plants(ind))
  }


  def getTotalPower():Double={
    Plants.map(_.capacity_MV).sum
  }
  def getCountryWithMAxGasPlants():(String, Int)={
    val filtered=Plants.filter(_.fuel1s.contains("Gas"))
    filtered.groupBy(_.country_long).mapValues(_.length).maxBy(_._2)

  }

  def getCountryWithMinGasPlants():(String, Int)={
    val filtered=Plants.filter(_.fuel1s.contains("Gas"))
    filtered.groupBy(_.country_long).mapValues(_.length).minBy(_._2)
  }
  def getYearWirhMaxPlantsOpened():(Int, Int)={
    case class Commissioning_year(year: Int)
    Plants.filter(_.commissioning_year!="").map(value =>  Commissioning_year(value.commissioning_year.toDouble.toInt)).groupBy(_.year).mapValues(_.length).maxBy(_._2)
  }


}

