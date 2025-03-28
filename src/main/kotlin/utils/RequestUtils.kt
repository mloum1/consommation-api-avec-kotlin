package utils

import com.google.gson.Gson
import exo.Config.BASE_API_URL
import exo.Config.SEARCH_ENDPOINT
import exo.Hero
import exo.HeroData
import exo.HeroResponse
import okhttp3.OkHttpClient
import okhttp3.Request
import java.net.URLEncoder
import kotlin.system.exitProcess


object RequestUtils {
  private val client = OkHttpClient()
  private val gson = Gson()

  /**
   * Exécute le programme sous forme de template
   */
  fun execute() {
    printHeroListLink()
    val (hero1, hero2) = askUserToFillSuperHeroes()
    searchHero(hero1, hero2)
  }

  private fun printHeroListLink() = println("Liste des super héros compatibles : ${BASE_API_URL}ids.html")


  private fun askUserToFillSuperHeroes(): Pair<String, String> {
    print("Donnez le nom d’un super héro : ")
    val hero1 = readln().trim()
    print("Donnez le nom d’un 2eme super héro : ")
    val hero2 = readln().trim()
    return Pair(hero1, hero2)
  }

  private fun searchHero(hero1 : String , hero2 : String) {
    verifyAC(hero1, hero2)
    validateHeroAvailability( hero1, hero2)
    displayComparison(fetchHeroData(hero1), fetchHeroData(hero2))

  }

  private fun verifyAC(hero1: String, hero2: String) =
    (hero1.isNotBlank() && hero1.length >= 4 && hero2.isNotBlank() && hero2.length >= 4)
      .also { isValid ->
        if (!isValid) {
          println("❌ Chaque nom doit être non vide et contenir au moins 4 caractères.")
          exitProcess(1)
        }
      }

  private fun fetchAvailableHeroNames(): List<String> {
    val url = "${BASE_API_URL}ids.html"
    val request = Request.Builder().url(url).build()

    return try {
      client.newCall(request).execute().use { response ->
        if (!response.isSuccessful) {
          println("❌ Impossible de récupérer la liste des héros compatibles.")
          // Pas d'erreur une page html
          exitProcess(1)
        }
        val body = response.body.string()
        val regex = Regex("""<td>([^<]+)</td>""")
        regex.findAll(body).map { it.groupValues[1].trim().lowercase() }.toList()
      }
    } catch (e: Exception) {
      println("❌ Erreur lors du chargement de la liste des héros : ${e.localizedMessage}")
      exitProcess(1)
    }
  }

  private fun fetchApiResponse(url: String): String? {
    val request = Request.Builder().url(url).build()
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

  private fun fetchHeroData(hero: String): HeroData {
    val encoded = encodeForUrl(hero)
    val url = "$SEARCH_ENDPOINT$encoded"
    println("url : $url")

    val json = fetchApiResponse(url)
    val response = parseHeroResponse(json, hero) ?: exitProcess(1)

    val avg = getAverageIntelligence(response.results)
    val name = response.results.first().name

    return HeroData(name, avg, url)
  }

  private fun getAverageIntelligence(heroes: List<Hero>): Double {
    val intelligences = heroes.mapNotNull { it.powerstats.intelligence.toIntOrNull() }
    return if (intelligences.isNotEmpty()) intelligences.average() else 0.0
  }


  private fun parseHeroResponse(json: String?, heroName: String): HeroResponse? {
    val heroResponse = gson.fromJson(json, HeroResponse::class.java)
    if (heroResponse.response == "error") {
      println("❌ Erreur API pour $heroName : ${heroResponse.error}")
      return null
    }
    return heroResponse
  }

  private fun displayComparison(h1: HeroData, h2: HeroData) {
    val resultLine = arrayListOf(
      "${h1.name} (${String.format("%.2f", h1.avgIntelligence)})",
      "${h2.name} (${String.format("%.2f", h2.avgIntelligence)})"
    ).joinToString(" - ")

    println(resultLine)

    when {
      h1.avgIntelligence > h2.avgIntelligence -> println("${h1.name} est plus intelligent")
      h2.avgIntelligence > h1.avgIntelligence -> println("${h2.name} est plus intelligent")
      else -> println("Les deux héros ont la même intelligence")
    }
  }

  private fun validateHeroAvailability( hero1: String, hero2: String){
    val herosDisponibles =  fetchAvailableHeroNames()
    if (hero1.lowercase() !in herosDisponibles || hero2.lowercase() !in herosDisponibles) {
      println("❌ Il faut que les 2 héros soient disponibles dans la liste des héros compatibles.")
      exitProcess(1)
    }

  }

  // surcharge de la méthode encode pour appliquer le %20 au lieu du +
  private fun encodeForUrl(input: String): String = URLEncoder.encode(input, "UTF-8").replace("+", "%20")
}


