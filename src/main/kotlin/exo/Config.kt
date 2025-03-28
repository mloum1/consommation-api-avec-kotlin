package exo

import io.github.cdimascio.dotenv.dotenv
import kotlin.system.exitProcess

object Config {
  private val ACCESS_TOKEN = dotenv()["SUPERHERO_API_TOKEN"]
    ?: error("❌ Token non défini dans le fichier .env")

  val BASE_API_URL = "https://www.superheroapi.com/"
  val SEARCH_ENDPOINT = "${BASE_API_URL}api/$ACCESS_TOKEN/search/"
}
