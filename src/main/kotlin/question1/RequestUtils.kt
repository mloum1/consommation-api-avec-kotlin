package question1

import okhttp3.OkHttpClient
import com.google.gson.Gson
import okhttp3.Request

const val API_BASE_URL  = ""
const val API_POUR_BATMAN = "https://www.superheroapi.com/api.php/1022497964237252/search/batman"
const val API_LISTE_PERSONNAGE = "https://www.superheroapi.com/ids.html"

/**
 * Utilitaire pour effectuer des requêtes HTTP vers l'API SuperHero.
 */
object RequestUtils{
  val client = OkHttpClient()
  val gson = Gson()

  /**
   * Méthode pour charger les héros.
   */
  fun loadHeroIntelligence() {
    println("Appel de l'API pour récupérer les intelligences des héros : $API_POUR_BATMAN")
    val response = fetchApiResponse() ?: return
    val heroes = extractHeroes(response) ?: return
    calculateAndPrintAverageIntelligence(heroes)
  }

  /**
   *
   */
  private fun fetchApiResponse(): String? {
    val request = Request.Builder().url(API_POUR_BATMAN).build()
    return try {
      client.newCall(request).execute().use { response ->
        if (!response.isSuccessful) {
          println("❌ Erreur HTTP : ${response.code} - ${response.message}")
          return null
        }
        response.body.string()
      }
    } catch (e: Exception) {
      println("❌ Exception lors de l’appel API : ${e.localizedMessage}")
      null
    }
  }

  /**
   * Extrait la liste des héros.
   * @param json le JSON contenant la réponse de l’API.
   * @return Une liste de héros ou null.
   */
  private fun extractHeroes(json: String): List<Hero>? {
    val heroResponse = gson.fromJson(json, HeroResponse::class.java)
    if (heroResponse.response == "error") {
      println("❌ Erreur de lors de l'extraction des données : ${heroResponse.error ?: "Erreur inconnue"}")
      return null
    }
    val heroes = heroResponse.results
    if (heroes.isEmpty()) {
      println("⚠️ Aucun héros trouvé dans la réponse.")
      return null
    }
    println("✅ ${heroes.size} héros trouvés.")
    return heroes
  }

  /**
   * Calcule et affiche la moyenne d’intelligence des héros fournis.
   *
   * @param heroes la liste de héros.
   */
  private fun calculateAndPrintAverageIntelligence(heroes: List<Hero>) {
    val intelligences = heroes.mapNotNull {
      it.powerstats.intelligence.toIntOrNull()
    }
    if (intelligences.isEmpty()) {
      println("⚠️ Aucune intelligence exploitable parmi les héros.")
      return
    }
    val average = intelligences.average()
    println("🧠 Moyenne d’intelligence des héros : $average")
  }

}
