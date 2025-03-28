package question1


fun main() {
  println("Bonjour ! Entrez le nom de supers héros svp : ")
  val superHero1: String = readln()
  val superHero2: String = readln()
  if (superHero1.length >= 4 && superHero2.length >= 4) println("Les super héros sont : ${superHero1.uppercase()} et ${superHero2.uppercase()}") else println(
    "saisie incorrecte : il faut un nom avec au moins 4 caracteres pour chaque super héro. Voici les données saisies : \n - ${superHero1.uppercase()} \n - ${superHero2.uppercase()}"
  )
}
