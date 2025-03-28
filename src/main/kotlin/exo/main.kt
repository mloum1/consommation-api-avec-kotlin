package exo

import utils.RequestUtils


fun main() {
   RequestUtils.execute()
}


/**
 * Les classes pour représenter les données des héros à récuperer.
 */
data class HeroResponse( val response: String, val results: List<Hero>,  val error: String? = null)
data class Hero(val name : String, val powerstats: PowerStats)
data class HeroData(val name : String, val avgIntelligence: Double, val url : String)
data class PowerStats(val intelligence : String)
