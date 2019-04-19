import com.github.tototoshi.csv.CSVReader

object CsvReader {
  def readData(fileName: String):List[Map[String,String]]={
    val reader=CSVReader.open(fileName)
    val dataFromFile=reader.allWithHeaders()
    reader.close()
    dataFromFile
  }
}

