import scala.util.Try

def tryToDouble( s: String ) = Try(s.toDouble).toInt
var x=tryToDouble("123.45")
