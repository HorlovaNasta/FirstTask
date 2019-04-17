class PlantStat(val csvPlantReader: CsvPlantReader) {
  val Plants=csvPlantReader.readData()

  def printPlant(ind: Int): Unit ={
    print(Plants(ind))
  }


  def getTotalPower()={
    Plants.map(_.capacity_MV).sum
  }
  def getCountryWithMAxGasPlants()={
    val filtered=Plants.filter(_.fuel1s.contains("Gas"))
    filtered.groupBy(_.country_long).mapValues(_.length).maxBy(_._2)

  }

  def getCountryWithMinGasPlants()={
    val filtered=Plants.filter(_.fuel1s.contains("Gas"))
    filtered.groupBy(_.country_long).mapValues(_.length).minBy(_._2)
  }
  def getYear()={
    //do normal split, this doesn't work
    def extractYear(plant: PlantsData) = {

      val parts =plant.commissioning_year.split(",")
      parts(0)
    }

    var filtered=Plants.filter(_.commissioning_year!="")
    filtered.groupBy(extractYear(_)).mapValues(_.length).maxBy(_._2)

  }


}

