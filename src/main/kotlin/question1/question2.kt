package question1

fun main() {

  // TODO :
  RequestUtils.loadHeroIntelligence()

}

// le token d'acces : ff0e8fce6c121fd4f1632406c1163abb

/**
 * Les classes pour représenter les données à récuperer.
 */
data class HeroResponse( val response: String, val results: List<Hero>,  val error: String? = null)
data class Hero(val name : String, val powerstats: PowerStats)
data class PowerStats(val intelligence : String)
