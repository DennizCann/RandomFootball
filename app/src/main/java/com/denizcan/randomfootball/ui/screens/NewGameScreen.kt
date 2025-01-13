package com.denizcan.randomfootball.ui.screens

import android.content.Context
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.denizcan.randomfootball.data.AppDatabase
import com.denizcan.randomfootball.data.dao.ManagerDao
import com.denizcan.randomfootball.data.dao.PlayerDao
import com.denizcan.randomfootball.data.model.Game
import com.denizcan.randomfootball.data.model.League
import com.denizcan.randomfootball.data.model.Manager
import com.denizcan.randomfootball.data.model.Player
import com.denizcan.randomfootball.data.model.Team
import com.denizcan.randomfootball.ui.components.TopBar
import kotlinx.coroutines.launch
import java.util.Date
import androidx.compose.foundation.Image
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import com.denizcan.randomfootball.R
import com.denizcan.randomfootball.util.FixtureGenerator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewGameScreen(
    onBackClick: () -> Unit,
    onGameSaved: (Long) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var gameName by remember { mutableStateOf("") }
    var selectedLeagueCount by remember { mutableStateOf(4f) }
    var selectedTeamCount by remember { mutableStateOf(10f) }
    var isLoading by remember { mutableStateOf(false) }

    val footballNames = object {
        val leagues = listOf(
            "Elite League",
            "Dream League",
            "Super League",
            "King League",
            "Master League",
            "Champion League",
            "Royal League",
            "Star League"
        )

        val teams = listOf(
            // Hayvanlar (30)
            "Lions", "Tigers", "Panthers", "Wolves", "Hawks",
            "Bears", "Eagles", "Sharks", "Vipers", "Cobras",
            "Stallions", "Dragons", "Phoenix", "Griffins", "Ravens",
            "Leopards", "Falcons", "Bulls", "Rhinos", "Serpents",
            "Jaguars", "Scorpions", "Raptors", "Cheetahs", "Vultures",
            "Pythons", "Hornets", "Wildcats", "Mustangs", "Thunderbirds",

            // Doğa Olayları (30)
            "Thunder", "Lightning", "Hurricanes", "Tornados", "Cyclones",
            "Storm", "Blizzard", "Avalanche", "Earthquake", "Tsunami",
            "Flames", "Inferno", "Blaze", "Fire", "Frost",
            "Ice", "Arctic", "Meteor", "Comet", "Eclipse",
            "Volcano", "Tempest", "Monsoon", "Whirlwind", "Typhoon",
            "Aurora", "Glacier", "Vortex", "Plasma", "Nebula",

            // Savaşçılar ve Kahramanlar (30)
            "Warriors", "Knights", "Spartans", "Gladiators", "Vikings",
            "Pirates", "Ninjas", "Samurai", "Heroes", "Legends",
            "Titans", "Giants", "Hunters", "Raiders", "Guardians",
            "Templars", "Paladins", "Crusaders", "Berserkers", "Assassins",
            "Centurions", "Commanders", "Defenders", "Conquerors", "Champions",
            "Warlords", "Barbarians", "Mercenaries", "Marauders", "Sentinels",

            // Mistik ve Fantastik (30)
            "Wizards", "Demons", "Angels", "Ghosts", "Phantoms",
            "Spirits", "Soul", "Shadow", "Oracle", "Mystics",
            "Sorcerers", "Warlocks", "Mages", "Druids", "Shamans",
            "Elementals", "Immortals", "Ancients", "Celestials", "Specters",
            "Alchemists", "Necromancers", "Summoners", "Enchanters", "Seers",
            "Prophets", "Ethereals", "Arcane", "Mythicals", "Enigma",

            // Asiller ve Yöneticiler (30)
            "Emperors", "Nobles", "Lords", "Dukes", "Barons",
            "Monarchs", "Regents", "Sovereigns", "Dynasty", "Empire",
            "Kingdom", "Reign", "Crown", "Throne", "Scepter",
            "Dominion", "Realm", "Legacy", "Princes", "Counts",
            "Chancellors", "Magistrates", "Governors", "Viceroys", "Stewards",
            "Overlords", "Paragons", "Imperials", "Consuls", "Tribunes",

            // Diğer Güçlü İsimler (30)
            "United", "City", "Wanderers", "Rovers", "Rangers",
            "Athletic", "Olympic", "Sporting", "Victory", "Glory",
            "Pride", "Honor", "Valor", "Force", "Power",
            "Energy", "Fusion", "Dynamic", "Zenith", "Apex",
            "Alliance", "Legion", "Squadron", "Battalion", "Brigade",
            "Division", "Armada", "Vanguard", "Frontline", "Fortress"
        )

        val colors = listOf(
            // Ana Renkler
            "#FF0000" to "Kırmızı",
            "#0000FF" to "Mavi",
            "#000000" to "Siyah",
            "#FFFFFF" to "Beyaz",
            "#FFFF00" to "Sarı",
            "#008000" to "Yeşil",

            // Koyu Tonlar
            "#8B0000" to "Koyu Kırmızı",
            "#000080" to "Lacivert",
            "#800080" to "Mor",
            "#A0522D" to "Kahverengi",

            // Açık Tonlar
            "#FF69B4" to "Pembe",
            "#87CEEB" to "Açık Mavi",
            "#98FB98" to "Açık Yeşil",

            // Metalik Tonlar
            "#C0C0C0" to "Gümüş",
            "#FFD700" to "Altın",

            // Ara Renkler
            "#FFA500" to "Turuncu",
            "#40E0D0" to "Turkuaz",
            "#708090" to "Arduvaz Grisi"
        )
    }

    val topFootballCountries = listOf(
        "Brazil",        // 5 Dünya Kupası şampiyonluğu
        "Germany",       // 4 Dünya Kupası şampiyonluğu
        "Italy",         // 4 Dünya Kupası şampiyonluğu
        "Argentina",     // 3 Dünya Kupası şampiyonluğu
        "France",        // 2 Dünya Kupası şampiyonluğu
        "England",       // 1 Dünya Kupası şampiyonluğu
        "Spain",         // 1 Dünya Kupası şampiyonluğu
        "Netherlands",   // 3 Dünya Kupası finalisti, şampiyonluk yok
        "Portugal",      // Cristiano Ronaldo gibi oyuncularla son yıllarda başarılı
        "Belgium"        // FIFA dünya sıralamasında uzun süre 1 numarada kaldı
    )

    val positions = listOf(
        "Goalkeeper",  // Kaleci
        "Defender",    // Defans oyuncusu
        "Midfielder",  // Orta saha oyuncusu
        "Forward"      // Forvet
    )

    val englandMaleNames = listOf(
        "Oliver", "George", "Harry", "Jack", "Noah",
        "Charlie", "Thomas", "Oscar", "William", "James",
        "Leo", "Alfie", "Henry", "Jacob", "Freddie",
        "Archie", "Joshua", "Ethan", "Isaac", "Alexander",
        "Joseph", "Edward", "Samuel", "Max", "Daniel",
        "Arthur", "Lucas", "Sebastian", "Theodore", "Logan",
        "Benjamin", "Harrison", "Mason", "Adam", "David",
        "Finn", "Elijah", "Rory", "Elliott", "Matthew",
        "Harvey", "Nathan", "Louis", "Reuben", "Liam",
        "Dylan", "Luke", "Toby", "Jayden", "Caleb",
        "Gabriel", "Michael", "Jude", "Ryan", "Roman",
        "Owen", "Zachary", "Leon", "Theo", "Austin",
        "Elliot", "Hugo", "Kai", "Aiden", "Evan",
        "Ellis", "Aaron", "Blake", "Alex", "Cameron",
        "Connor", "Dominic", "Edward", "Ewan", "Felix",
        "Jasper", "Kieran", "Miles", "Patrick", "Riley",
        "Rowan", "Stanley", "Taylor", "Tristan", "Victor",
        "Wesley", "Zane", "Angus", "Callum", "Declan",
        "Emmett", "Francis", "Gregory", "Harold", "Jonah",
        "Keegan", "Malcolm", "Niall", "Oliver", "Percy"
    )

    val englandSurnames = listOf(
        "Smith", "Jones", "Taylor", "Brown", "Williams",
        "Wilson", "Johnson", "Davies", "Robinson", "Wright",
        "Thompson", "Evans", "Walker", "White", "Roberts",
        "Green", "Hall", "Wood", "Harris", "Martin",
        "Jackson", "Clarke", "Clark", "Turner", "Hill",
        "Scott", "Moore", "Cooper", "Ward", "Morris",
        "King", "Watson", "Baker", "Allen", "Bell",
        "Young", "Adams", "Mitchell", "Anderson", "Phillips",
        "James", "Campbell", "Lee", "Hughes", "Parker",
        "Cook", "Carter", "Murphy", "Reid", "Morgan",
        "Stewart", "Bailey", "Gray", "Murray", "Miller",
        "Dixon", "Harper", "Fox", "Pearson", "Shaw",
        "Holmes", "Kennedy", "Ellis", "Reynolds", "Simpson",
        "Chambers", "Foster", "Butler", "Knight", "Lawrence",
        "Palmer", "Booth", "Mills", "Webb", "Chapman",
        "Howard", "Graham", "Wallace", "Russell", "Fletcher",
        "Lawson", "Armstrong", "Dawson", "Burton", "Kerr",
        "Bradley", "Henderson", "Todd", "Stevens", "Paterson",
        "Barber", "Fisher", "Rice", "Mackenzie", "Munro"
    )

    val franceMaleNames = listOf(
        "Jean", "Louis", "Pierre", "Michel", "Jacques",
        "Claude", "Paul", "Henri", "Philippe", "Alain",
        "Éric", "Bernard", "René", "Gérard", "François",
        "Daniel", "André", "Christian", "Luc", "Marc",
        "Patrick", "Julien", "Vincent", "Nicolas", "Antoine",
        "Sébastien", "Alexandre", "Maxime", "Benoît", "Yannick",
        "Théo", "Hugo", "Jules", "Arthur", "Léo",
        "Mathis", "Ethan", "Enzo", "Raphaël", "Thomas",
        "Clément", "Gabriel", "Adrien", "Lucas", "Noah",
        "Nathan", "Romain", "Simon", "Baptiste", "Matthieu",
        "Quentin", "Damien", "Florian", "Gauthier", "Victor",
        "Paul-Émile", "Jean-Baptiste", "Jean-Luc", "Jean-Paul", "Jean-Pierre",
        "Émile", "Armand", "Christophe", "Olivier", "Édouard",
        "Fabrice", "Guillaume", "Laurent", "Pascal", "Rémi",
        "Thierry", "Dominique", "Yves", "Georges", "Joseph",
        "Hervé", "Élisée", "Didier", "Augustin", "Bruno",
        "Cédric", "Damian", "Alban", "Elliot", "Xavier",
        "Kevin", "Robin", "Cyril", "Tristan", "Loïc",
        "Sylvain", "Gilbert", "Manuel", "Gaël", "Bastien"
    )

    val franceSurnames = listOf(
        "Martin", "Bernard", "Thomas", "Petit", "Robert",
        "Richard", "Durand", "Dubois", "Moreau", "Laurent",
        "Simon", "Michel", "Lefebvre", "Leroy", "Roux",
        "David", "Bertrand", "Morel", "Fournier", "Girard",
        "Bonnet", "Dupont", "Lambert", "Fontaine", "Rousseau",
        "Vincent", "Muller", "Lefevre", "Faure", "Andre",
        "Mercier", "Blanc", "Guerin", "Boyer", "Garnier",
        "Chevalier", "Francois", "Legrand", "Gauthier", "Garcia",
        "Perrin", "Robin", "Clement", "Morin", "Nicolas",
        "Henry", "Roussel", "Mathieu", "Gautier", "Masson",
        "Marchand", "Duval", "Denis", "Dumont", "Marie",
        "Lemoine", "Noel", "Meyer", "Dufour", "Meunier",
        "Brun", "Blanchard", "Giraud", "Joly", "Riviere",
        "Lucas", "Brunet", "Gaillard", "Barbier", "Arnaud",
        "Martinez", "Gerard", "Roche", "Renard", "Schmitt",
        "Roy", "Huet", "Baron", "Leclerc", "Pires",
        "Barthez", "Bellamy", "Caron", "Delacroix", "Durand",
        "Ferry", "Hamon", "Joubert", "Lambert", "Paquet",
        "Perret", "Renaud", "Savary", "Tessier", "Vidal"
    )


    val spainMaleNames = listOf(
        "Antonio", "José", "Manuel", "Francisco", "David",
        "Juan", "Javier", "José Antonio", "Daniel", "Francisco Javier",
        "Jesús", "Carlos", "Miguel", "José Luis", "Alejandro",
        "José Manuel", "Rafael", "Pedro", "Ángel", "Pablo",
        "Sergio", "Miguel Ángel", "José María", "Fernando", "Luis",
        "Jorge", "Alberto", "Alfonso", "Juan Carlos", "Adrián",
        "Raúl", "Joaquín", "Ricardo", "Víctor", "Eduardo",
        "Rubén", "Óscar", "Iván", "Diego", "Salvador",
        "Andrés", "Mario", "Marcos", "Jaime", "Guillermo",
        "Ramón", "Hugo", "Gabriel", "Ismael", "Samuel",
        "Rodrigo", "Marc", "Iker", "Ignacio", "Aitor",
        "Emilio", "Álvaro", "Santiago", "Sebastián", "Gonzalo",
        "Martín", "Cristian", "Álex", "Vicente", "César",
        "Felipe", "Tomás", "Julián", "Josep", "Félix",
        "Agustín", "Héctor", "Arturo", "Albert", "Rubén",
        "Jonathan", "Raúl", "Enrique", "Anselmo", "Bernardo",
        "Eloy", "Pascual", "Raimundo", "Esteban", "Rodolfo",
        "Aleix", "Marcelo", "Gerard", "Ezequiel", "Isidoro",
        "Leandro", "Cristóbal", "Simón", "Patricio", "Humberto"
    )

    val spainSurnames = listOf(
        "García", "Martínez", "López", "Sánchez", "Rodríguez",
        "Pérez", "González", "Fernández", "Gómez", "Ruiz",
        "Hernández", "Jiménez", "Álvarez", "Díaz", "Moreno",
        "Muñoz", "Romero", "Alonso", "Gutiérrez", "Navarro",
        "Torres", "Domínguez", "Vázquez", "Ramos", "Gil",
        "Castro", "Flores", "Suárez", "Molina", "Ortega",
        "Delgado", "Ortiz", "Rubio", "Marín", "Santos",
        "Iglesias", "Medina", "Crespo", "Vega", "Reyes",
        "Herrera", "Soto", "Cabrera", "Campos", "Carrasco",
        "Fuentes", "Aguilar", "Núñez", "Cortés", "Serrano",
        "Blanco", "Calvo", "Rivas", "Pardo", "Nieto",
        "Lorenzo", "Hidalgo", "Peña", "Vicente", "Arroyo",
        "Méndez", "Prieto", "Miranda", "Castillo", "Parra",
        "Bravo", "Vidal", "Bernal", "Mora", "Velasco",
        "Rey", "Rivera", "Pastor", "Soler", "Gallardo",
        "Montes", "Saavedra", "Espinosa", "Del Río", "Esteban",
        "Lara", "Caballero", "Acosta", "Segura", "Andrade",
        "Benítez", "Pacheco", "Carrillo", "Varela", "Montalvo",
        "Soria", "Castañeda", "Montero", "Linares", "Aranda"
    )

    val germanyMaleNames = listOf(
        "Maximilian", "Alexander", "Paul", "Leon", "Lukas",
        "Finn", "Felix", "Elias", "Noah", "Julian",
        "Ben", "Luis", "Jonas", "Nico", "David",
        "Emil", "Jan", "Philipp", "Johannes", "Liam",
        "Matteo", "Anton", "Simon", "Jakob", "Vincent",
        "Henry", "Oskar", "Samuel", "Tom", "Rafael",
        "Matthias", "Florian", "Sebastian", "Tobias", "Marc",
        "Michael", "Daniel", "Patrick", "Fabian", "Christian",
        "Niklas", "Oliver", "Moritz", "Adrian", "Jannik",
        "Benjamin", "Dominik", "Kai", "Eric", "Robin",
        "Tim", "Till", "Benedikt", "Frederik", "Julius",
        "Lennart", "Aron", "Hannes", "Mats", "Konstantin",
        "Karl", "Andreas", "Stefan", "Rene", "Martin",
        "Malte", "Christopher", "Georg", "Aaron", "Leo",
        "Emanuel", "Gregor", "Silas", "Oscar", "Theo",
        "Albert", "Friedrich", "Heinrich", "Hans", "Otto",
        "Richard", "Walter", "Franz", "Gustav", "Erik",
        "Lorenz", "Valentin", "Wolfgang", "Uwe", "Lutz",
        "Armin", "Klaus", "Detlef", "Jürgen", "Bernd",
        "Holger", "Joachim", "Rolf", "Rainer", "Manfred"
    )

    val germanySurnames = listOf(
        "Müller", "Schmidt", "Schneider", "Fischer", "Weber",
        "Meyer", "Wagner", "Becker", "Hoffmann", "Schäfer",
        "Koch", "Bauer", "Richter", "Klein", "Wolf",
        "Schröder", "Neumann", "Schwarz", "Zimmermann", "Braun",
        "Krüger", "Hofmann", "Hartmann", "Lange", "Schmitt",
        "Werner", "Schmitz", "Krause", "Meier", "Lehmann",
        "Schmid", "Schulz", "Maier", "Köhler", "Herrmann",
        "König", "Walter", "Mayer", "Huber", "Kaiser",
        "Fuchs", "Peters", "Lang", "Scholz", "Möller",
        "Weiß", "Jung", "Hahn", "Schubert", "Vogel",
        "Friedrich", "Keller", "Günther", "Frank", "Berger",
        "Winkler", "Roth", "Beck", "Lorenz", "Baumann",
        "Franke", "Albrecht", "Schuster", "Simon", "Ludwig",
        "Böhm", "Winter", "Kraus", "Martin", "Schumacher",
        "Krämer", "Vogt", "Stein", "Jäger", "Otto",
        "Groß", "Sommer", "Seidel", "Heinrich", "Brandt",
        "Haas", "Dietrich", "Ziegler", "Reinhardt", "Kühn",
        "Pohl", "Engel", "Horn", "Busch", "Bergmann",
        "Thomas", "Sauer", "Arnold", "Stahl", "Krieger"
    )

    val italyMaleNames = listOf(
        "Alessandro", "Lorenzo", "Matteo", "Francesco", "Andrea",
        "Leonardo", "Riccardo", "Tommaso", "Giuseppe", "Antonio",
        "Giovanni", "Davide", "Federico", "Gabriele", "Marco",
        "Alberto", "Simone", "Niccolò", "Edoardo", "Michele",
        "Luca", "Vincenzo", "Pietro", "Stefano", "Filippo",
        "Daniele", "Angelo", "Salvatore", "Carlo", "Cristiano",
        "Enrico", "Giacomo", "Paolo", "Alfonso", "Domenico",
        "Massimo", "Raffaele", "Roberto", "Valerio", "Nicola",
        "Emanuele", "Gianluca", "Luigi", "Claudio", "Matia",
        "Giulio", "Amedeo", "Sebastiano", "Fabio", "Sergio",
        "Martino", "Diego", "Franco", "Ruggero", "Corrado",
        "Giampiero", "Nino", "Vito", "Gianmarco", "Gianfranco",
        "Alvise", "Carmine", "Giorgio", "Luciano", "Osvaldo",
        "Tiziano", "Guido", "Elio", "Bruno", "Ottavio",
        "Mauro", "Ettore", "Rodolfo", "Aldo", "Fabrizio",
        "Maurizio", "Gennaro", "Umberto", "Marcello", "Adriano",
        "Renato", "Girolamo", "Benedetto", "Pasquale", "Giuliano",
        "Achille", "Rinaldo", "Ciro", "Dario", "Ernesto",
        "Lamberto", "Rosario", "Teodoro", "Tullio", "Massimiliano",
        "Ivano", "Alarico", "Romano", "Arturo", "Santino"
    )

    val italySurnames = listOf(
        "Rossi", "Russo", "Ferrari", "Esposito", "Bianchi",
        "Romano", "Colombo", "Ricci", "Marino", "Greco",
        "Bruno", "Gallo", "Conti", "De Luca", "Mancini",
        "Costa", "Giordano", "Rizzo", "Lombardi", "Moretti",
        "Barbieri", "Fontana", "Santoro", "Mariani", "Rinaldi",
        "Caruso", "Ferrara", "Gatti", "Pellegrini", "Palumbo",
        "Sanna", "Farina", "Amato", "Monti", "Martini",
        "Leone", "Longo", "Gentile", "Vitali", "Ferri",
        "Mazza", "Piras", "Coppola", "Grassi", "Bianco",
        "Brunetti", "Marchetti", "Silvestri", "Parisi", "Sartori",
        "Cattaneo", "Caputo", "Donati", "Pastore", "D'Angelo",
        "Valli", "Sorrentino", "Pagano", "Bellini", "De Santis",
        "Testa", "Piazza", "Corsi", "Benedetti", "Villa",
        "Morelli", "Barone", "Carbone", "Giuliani", "Serra",
        "De Angelis", "Bertolini", "Neri", "Landi", "Valente",
        "De Simone", "Fiore", "Battaglia", "Pellegrino", "Fabbri",
        "Carli", "Orlando", "Barbato", "Luciano", "Bellucci",
        "Guerini", "Ruggiero", "Raffaelli", "Catalano", "Del Vecchio",
        "Perini", "D'Amico", "Scalise", "Marotta", "Basile"
    )

    val portugalMaleNames = listOf(
        "João", "José", "Francisco", "António", "Pedro",
        "Manuel", "Carlos", "Luís", "Jorge", "Miguel",
        "Rui", "Fernando", "Ricardo", "Tiago", "Paulo",
        "André", "Hugo", "Alexandre", "Vítor", "Nuno",
        "Filipe", "Bruno", "Marco", "Gonçalo", "Diogo",
        "Duarte", "Eduardo", "Henrique", "Rafael", "Daniel",
        "Rodrigo", "Gabriel", "Martim", "Afonso", "Tomás",
        "Gustavo", "Lucas", "Dinis", "Vasco", "David",
        "Sérgio", "Simão", "Cristiano", "Leandro", "Álvaro",
        "Salvador", "Rui Pedro", "Leonardo", "Joaquim", "Ivo",
        "Artur", "Tiago Manuel", "Domingos", "Aníbal", "César",
        "Mateus", "Mário", "Valentim", "Américo", "Celso",
        "Osvaldo", "Arnaldo", "Adriano", "Teodoro", "Fausto",
        "Caio", "Elias", "Cristóvão", "Samuel", "Baltasar",
        "Jacinto", "Emanuel", "Nicolau", "Luís Miguel", "Isaías",
        "João Paulo", "António Luís", "Fábio", "Maurício", "Renato",
        "Edgar", "Sebastião", "Adelino", "Hélio", "Nelson",
        "Tobias", "Humberto", "Vicente", "Romeu", "Rodolfo",
        "Amândio", "Aurélio", "Rogério", "Gaspar", "Hermínio",
        "Xavier", "Lourenço", "António Manuel", "Fernando José", "Álvaro Luís"
    )

    val portugalSurnames = listOf(
        "Silva", "Santos", "Ferreira", "Pereira", "Oliveira",
        "Costa", "Rodrigues", "Martins", "Jesus", "Sousa",
        "Fernandes", "Gonçalves", "Gomes", "Lopes", "Marques",
        "Almeida", "Ribeiro", "Monteiro", "Carvalho", "Correia",
        "Mendes", "Nunes", "Soares", "Vieira", "Cardoso",
        "Antunes", "Leite", "Azevedo", "Cunha", "Machado",
        "Teixeira", "Figueiredo", "Rocha", "Barros", "Coelho",
        "Moreira", "Pires", "Simões", "Campos", "Serra",
        "Peixoto", "Neves", "Henriques", "Mota", "Batista",
        "Fonseca", "Borges", "Magalhães", "Queirós", "Aguiar",
        "Vaz", "Pinheiro", "Valente", "Barreto", "Lima",
        "Amaral", "Tavares", "Castro", "Faria", "Sales",
        "Camacho", "Resende", "Esteves", "Carmo", "Freitas",
        "Maia", "Bessa", "Paiva", "Gaspar", "Cordeiro",
        "Braga", "Afonso", "Álvares", "Palmeira", "Matias",
        "Taveira", "Fraga", "Romão", "Sequeira", "Barbosa",
        "Viana", "Saraiva", "Salvador", "Cabral", "Assunção",
        "Pinto", "Saramago", "Coutinho", "Reis", "Alvarenga",
        "Canas", "Carrilho", "Negrão", "Viegas", "Duarte",
        "Amado", "Bettencourt", "Caldeira", "Estevão", "Ávila"
    )

    val netherlandsMaleNames = listOf(
        "Daan", "Sem", "Finn", "Noah", "Liam",
        "Jesse", "Lucas", "Levi", "Milan", "Luuk",
        "Mees", "Julian", "Ties", "Sam", "Thijs",
        "Sven", "Thomas", "Tim", "Ruben", "Gijs",
        "Stijn", "Floris", "Benjamin", "Tijn", "Bram",
        "Cas", "Noud", "Lars", "Jens", "Max",
        "Oscar", "Niek", "Daniël", "Koen", "Teun",
        "Joep", "Pim", "Vince", "Roan", "Pepijn",
        "Mats", "Siem", "Job", "Jim", "Jurre",
        "Fedde", "Dylan", "Tobias", "Julius", "Willem",
        "Maarten", "Bas", "Guus", "Olaf", "Joris",
        "Sander", "Roel", "Ivo", "Bart", "Luca",
        "Boaz", "Dean", "Thomas", "Wout", "Arjen",
        "Dirk", "Boris", "Casper", "Diederik", "Jochem",
        "Stefan", "Marcel", "Thimo", "Hugo", "Rein",
        "Koos", "Hendrik", "Berend", "Gerard", "Kees",
        "Freek", "Rik", "Douwe", "Niels", "Matthijs",
        "Stef", "Christiaan", "Gert", "Johannes", "Vincent",
        "Jaap", "Aart", "Twan", "Bart-Jan", "Corné",
        "Lodewijk", "Jelle", "Pieter", "Huib", "Rutger"
    )

    val netherlandsSurnames = listOf(
        "De Jong", "Jansen", "De Vries", "Van den Berg", "Van Dijk",
        "Bakker", "Jansen", "Visser", "Smit", "Meijer",
        "De Boer", "Mulder", "Van der Meer", "Bos", "Vos",
        "Peters", "Hendriks", "Van der Heijden", "Dekker", "Kok",
        "Van der Veen", "Verhoeven", "Van der Wal", "Hoekstra", "De Groot",
        "Kuipers", "Schouten", "Van Leeuwen", "Van der Laan", "Veenstra",
        "Kramer", "Van Dam", "Timmermans", "Van den Heuvel", "Koster",
        "Evers", "Postma", "Dijkstra", "Hermans", "Van de Ven",
        "Hofman", "Willems", "Vonk", "De Bruin", "Van Beek",
        "Gerritsen", "Van den Broek", "Schuurman", "Van der Werf", "Van Schaik",
        "Smits", "Van Zanten", "De Wit", "Van den Bosch", "Van Houten",
        "Van Eijk", "Veldman", "Groen", "Van der Linden", "Van der Steen",
        "Scholten", "Van der Velden", "Koot", "Van den Brink", "Bosman",
        "Van Es", "De Haan", "Van der Heijden", "Boersma", "Van der Linde",
        "Vink", "De Ridder", "Van den Hoek", "Huisman", "Molenaar",
        "Verbeek", "Veldhuis", "Van der Meulen", "Van Gelder", "De Vos",
        "Van der Horst", "Van den Berghe", "Van Aalst", "Haring", "Bruins",
        "De Koning", "Van Gils", "Otten", "Van Baalen", "Van Dongen",
        "Heemskerk", "Van Hees", "Schoemaker", "Van Maanen", "Van Rij",
        "Van der Zee", "Van Amersfoort", "Van Vliet", "Dekkers", "Van Bemmel"
    )

    val belgiumMaleNames = listOf(
        "Lucas", "Liam", "Noah", "Ethan", "Arthur",
        "Louis", "Nathan", "Gabriel", "Jules", "Victor",
        "Adam", "Oscar", "Matteo", "Alexander", "Thomas",
        "Hugo", "Aaron", "Emile", "Leon", "Maxime",
        "Simon", "Benjamin", "Florent", "David", "Theo",
        "Ruben", "Mathis", "Samuel", "Quentin", "Dylan",
        "Martin", "Julien", "Emmanuel", "Sébastien", "Antoine",
        "Axel", "Jonas", "Nicolas", "Kevin", "Robin",
        "Jeremy", "Elias", "Max", "Arnaud", "Timothy",
        "Cedric", "Felix", "Adrien", "Pierre", "Milo",
        "Guillaume", "Yannick", "Paul", "Christian", "Bastien",
        "Alexis", "Vince", "Frederik", "Jean", "Michel",
        "Romain", "Charles", "Gauthier", "Marcel", "Loic",
        "Jérôme", "Etienne", "Thibault", "Hendrik", "Karel",
        "Stijn", "Bart", "Willem", "Jens", "Sven",
        "Elliot", "Tim", "Lars", "Koen", "Jan",
        "Gilles", "Jelle", "Daan", "Maarten", "Niels",
        "Wout", "Victor", "Jordi", "Tom", "Benoit",
        "Cédric", "Andreas", "Tanguy", "Luc", "Xander",
        "Gianni", "René", "Bram", "Théo", "Francis"
    )

    val belgiumSurnames = listOf(
        "Peeters", "Janssens", "Maes", "Jacobs", "Willems",
        "Claes", "Goossens", "Wouters", "De Smet", "De Jong",
        "Lemmens", "Vermeulen", "Van den Broeck", "Mertens", "Aerts",
        "Van den Berghe", "Martens", "Smets", "Boons", "De Vries",
        "Van Damme", "Hendrickx", "Bauwens", "Lambrechts", "Michiels",
        "Stevens", "Van Dyck", "Vandamme", "Thijs", "Dumont",
        "Van de Velde", "Segers", "Verstraeten", "Pieters", "De Clercq",
        "De Vos", "De Cock", "De Winter", "De Backer", "Vandenberghe",
        "Van Leeuwen", "De Ridder", "Van den Heuvel", "Van Acker", "Dierckx",
        "Vandenbosch", "Wouters", "De Coster", "Huygens", "Dujardin",
        "Coenen", "Vancoppenolle", "De Meyer", "De Wilde", "Van Rooy",
        "Declercq", "Van Pelt", "Verbeke", "Verschueren", "Vandenbroucke",
        "De Bruyn", "Van Loon", "De Graeve", "Van der Linden", "De Coninck",
        "De Man", "Vervaet", "Vandenberg", "Van Hoof", "Van Gorp",
        "Vervaeke", "Luyten", "Baeten", "Van Hove", "Vandenplas",
        "Vandewalle", "Vandenbulcke", "Debruyne", "Vanderhaegen", "Desmet",
        "Van Geel", "Vanderlinden", "Hendrikx", "Verbist", "Vandebroeck",
        "Van Hoorde", "Schreurs", "Geerts", "Van Rompaey", "Van den Eynde",
        "De Pauw", "Vandenbussche", "Van de Walle", "Van Laer", "Vandervoort"
    )

    val argentinaMaleNames = listOf(
        "Santiago", "Mateo", "Matías", "Joaquín", "Lucas",
        "Benjamín", "Gabriel", "Tomás", "Emiliano", "Franco",
        "Ignacio", "Facundo", "Julian", "Martín", "Lautaro",
        "Nicolás", "Agustín", "Thiago", "Federico", "Juan",
        "Gonzalo", "Alejandro", "Diego", "Sebastián", "Luciano",
        "Maximiliano", "Pablo", "Cristian", "Damián", "Rodrigo",
        "Leonardo", "Ricardo", "Iván", "Felipe", "Marcos",
        "Ezequiel", "Ramiro", "Esteban", "Adrián", "Álvaro",
        "Bruno", "Simón", "Kevin", "Hernán", "Raúl",
        "Mario", "Daniel", "Andrés", "Alan", "Mauricio",
        "José", "Carlos", "Julián", "César", "Antonio",
        "Rafael", "Víctor", "Hugo", "Enzo", "Ariel",
        "Guillermo", "Leandro", "Milton", "Nahuel", "Ismael",
        "Valentín", "Tobías", "Gustavo", "Salvador", "Francisco",
        "Ángel", "Patricio", "Javier", "Juan Cruz", "Bautista",
        "Ignacio", "Luca", "Francesco", "Manuel", "Darío",
        "Lisandro", "Claudio", "Emanuel", "Elías", "Matheo",
        "Sergio", "Ivo", "Maximo", "Cristóbal", "Tomás Ignacio",
        "Thiago Joaquín", "Ramón", "Matteo", "Alonso", "Camilo",
        "Santino", "Damian", "Fabián", "Orlando", "Aldo"
    )

    val argentinaSurnames = listOf(
        "González", "Rodríguez", "Gómez", "Fernández", "López",
        "Martínez", "Díaz", "Pérez", "Sánchez", "Romero",
        "Álvarez", "Torres", "Ruiz", "Ramírez", "Flores",
        "Acosta", "Benítez", "Medina", "Herrera", "Aguirre",
        "Molina", "Ortiz", "Silva", "Luna", "Cabrera",
        "Moreno", "Castro", "Ríos", "Ávila", "Chávez",
        "Figueroa", "Ponce", "Vera", "Vargas", "Ferreyra",
        "Suárez", "Rojas", "Ávalos", "Villalba", "Córdoba",
        "Carrizo", "Bustamante", "Mansilla", "Orellana", "Barrios",
        "Godoy", "Escobar", "Correa", "Peralta", "Franco",
        "Leiva", "Paz", "Valenzuela", "Cardozo", "Lucero",
        "Montes", "Quiroga", "Villarreal", "Peña", "Cruz",
        "Arias", "Mendoza", "Santana", "Espinoza", "Zárate",
        "Morales", "Navarro", "Meza", "Velázquez", "Campos",
        "Blanco", "Sosa", "Bustos", "Alvarado", "Domínguez",
        "Roldán", "Palacios", "Delgado", "Galván", "Vázquez",
        "Barreto", "Olmedo", "Cáceres", "Fuentes", "Ocampo",
        "Salinas", "Alonso", "Báez", "Rivera", "Paredes",
        "Vega", "Maldonado", "Toledo", "Gaitán", "Ledesma"
    )

    val brazilMaleNames = listOf(
        "Gabriel", "Lucas", "Matheus", "Gustavo", "Felipe",
        "Rafael", "Leonardo", "João", "Pedro", "Henrique",
        "Bruno", "Thiago", "Ricardo", "Fernando", "Carlos",
        "Daniel", "Luiz", "Victor", "André", "Eduardo",
        "José", "Vinícius", "Diego", "Rodrigo", "Alexandre",
        "Marcelo", "Ruan", "Caio", "Antonio", "Murilo",
        "Miguel", "Luan", "Paulo", "Arthur", "Bernardo",
        "Emanuel", "Samuel", "Thiago", "Sérgio", "Fábio",
        "Renato", "Roberto", "Jorge", "Wesley", "Vitor",
        "Alessandro", "Igor", "Erick", "Márcio", "Davi",
        "Enzo", "Maurício", "Raul", "Cristiano", "Brayan",
        "Otávio", "Rogério", "Cláudio", "Wallace", "Nathan",
        "Matheus Henrique", "João Paulo", "João Victor", "Danilo", "Júnior",
        "Alberto", "César", "Adriano", "Leandro", "Rômulo",
        "Renan", "Alan", "Evandro", "Luciano", "Elias",
        "David", "Cristiano", "Alex", "Jonas", "Everaldo",
        "Rivaldo", "Neymar", "Kléber", "Amarildo", "Nilton",
        "Talles", "Humberto", "Celso", "João Gabriel", "Augusto",
        "Ronaldo", "Gilberto", "Sandro", "Carlos Alberto", "Jair",
        "Emerson", "Maicon", "Juan", "Eder", "Zico"
    )

    val brazilSurnames = listOf(
        "Silva", "Santos", "Oliveira", "Souza", "Pereira",
        "Costa", "Almeida", "Ferreira", "Rodrigues", "Lima",
        "Gomes", "Ribeiro", "Martins", "Barbosa", "Carvalho",
        "Araujo", "Melo", "Rocha", "Nascimento", "Cavalcante",
        "Monteiro", "Teixeira", "Moreira", "Faria", "Campos",
        "Cardoso", "Correia", "Andrade", "Vieira", "Cruz",
        "Araújo", "Dias", "Pinto", "Barros", "Antunes",
        "Lopes", "Leite", "Freitas", "Batista", "Nascimento",
        "Tavares", "Moraes", "Brandão", "Coelho", "Siqueira",
        "Coutinho", "Franco", "Macedo", "Nogueira", "Ramos",
        "Reis", "Assis", "Miranda", "Neves", "Fonseca",
        "Furtado", "Bezerra", "Brito", "Alves", "Duarte",
        "Queiroz", "Xavier", "Maia", "Paiva", "César",
        "Serra", "Guedes", "Guimarães", "Aguiar", "Viana",
        "Lacerda", "Pontes", "Vargas", "Valente", "Dantas",
        "Caldeira", "Meireles", "Soares", "Farias", "Lacerda",
        "Pacheco", "Bueno", "Saldanha", "Borges", "Sales",
        "Pedrosa", "Amaral", "Silveira", "Prado", "Negrão",
        "Magalhães", "Rezende", "Telles", "Monteiro", "Barreto",
        "Rangel", "Peixoto", "Canedo", "Cavalcanti", "Fonseca"
    )

    // namesByNationality değişkeni burada tanımlanıyor
    val namesByNationality = mapOf(
        "England" to Pair(englandMaleNames, englandSurnames),
        "France" to Pair(franceMaleNames, franceSurnames),
        "Spain" to Pair(spainMaleNames, spainSurnames),
        "Germany" to Pair(germanyMaleNames, germanySurnames),
        "Italy" to Pair(italyMaleNames, italySurnames),
        "Portugal" to Pair(portugalMaleNames, portugalSurnames),
        "Netherlands" to Pair(netherlandsMaleNames, netherlandsSurnames),
        "Belgium" to Pair(belgiumMaleNames, belgiumSurnames),
        "Argentina" to Pair(argentinaMaleNames, argentinaSurnames),
        "Brazil" to Pair(brazilMaleNames, brazilSurnames)
    )

    val formations = listOf(
        "3-4-3",
        "3-5-2",
        "4-2-4",
        "4-3-3",
        "4-4-2",
        "4-5-1",
        "5-2-3",
        "5-3-2",
        "5-4-1"
    )

    // Renk üretme fonksiyonu
    fun generateRandomColor(): String {
        val colors = listOf(
            "#FF0000", // Kırmızı
            "#00FF00", // Yeşil
            "#0000FF", // Mavi
            "#FFFF00", // Sarı
            "#FF00FF", // Magenta
            "#00FFFF", // Cyan
            "#800000", // Bordo
            "#008000", // Koyu Yeşil
            "#000080", // Lacivert
            "#808000", // Zeytin Yeşili
            "#800080", // Mor
            "#008080", // Turkuaz
            "#FFA500", // Turuncu
            "#A52A2A", // Kahverengi
            "#DEB887", // Açık Kahve
            "#5F9EA0", // Deniz Mavisi
            "#D2691E", // Çikolata
            "#556B2F", // Koyu Zeytin Yeşili
            "#8B0000", // Koyu Kırmızı
            "#E9967A"  // Somon
        )
        return colors.random()
    }

    fun generateRandomName(): Pair<String, String> {  // İsim ve milliyet döndürür
        // Rastgele Milliyet Belirleme
        val selectedNationality = topFootballCountries.random()

        // Milliyete Göre İsim ve Soyisim Listelerini Al
        val (nameList, surnameList) = namesByNationality[selectedNationality]!!

        // Rastgele İsim ve Soyisim Seç
        val firstName = nameList.random()
        val lastName = surnameList.random()

        return Pair("$firstName $lastName", selectedNationality)
    }

    fun generatePlayers(teamId: Long, formation: String): List<Player> {
        val players = mutableListOf<Player>()
        val usedShirtNumbers = mutableSetOf<Int>()

        // Formasyonu parse et (örn: "4-3-3")
        val formationParts = formation.split("-").map { it.toInt() }
        val defenders = formationParts[0]
        val midfielders = formationParts[1]
        val forwards = formationParts[2]

        // Kaleci oluştur (her formasyonda 1 kaleci)
        val (goalkeeperName, goalkeeperNationality) = generateRandomName()
        var shirtNumber = (1..99).random()
        usedShirtNumbers.add(shirtNumber)

        players.add(
            Player(
                teamId = teamId,
                name = goalkeeperName,
                nationality = goalkeeperNationality,
                position = "Goalkeeper",
                shirtNumber = shirtNumber,
                skill = (60..90).random()
            )
        )

        // Defans oyuncuları
        repeat(defenders) {
            val (defenderName, defenderNationality) = generateRandomName()
            do {
                shirtNumber = (1..99).random()
            } while (shirtNumber in usedShirtNumbers)
            usedShirtNumbers.add(shirtNumber)

            players.add(
                Player(
                    teamId = teamId,
                    name = defenderName,
                    nationality = defenderNationality,
                    position = "Defender",
                    shirtNumber = shirtNumber,
                    skill = (60..90).random()
                )
            )
        }

        // Orta saha oyuncuları
        repeat(midfielders) {
            val (midfielderName, midfielderNationality) = generateRandomName()
            do {
                shirtNumber = (1..99).random()
            } while (shirtNumber in usedShirtNumbers)
            usedShirtNumbers.add(shirtNumber)

            players.add(
                Player(
                    teamId = teamId,
                    name = midfielderName,
                    nationality = midfielderNationality,
                    position = "Midfielder",
                    shirtNumber = shirtNumber,
                    skill = (60..90).random()
                )
            )
        }

        // Forvet oyuncuları
        repeat(forwards) {
            val (forwardName, forwardNationality) = generateRandomName()
            do {
                shirtNumber = (1..99).random()
            } while (shirtNumber in usedShirtNumbers)
            usedShirtNumbers.add(shirtNumber)

            players.add(
                Player(
                    teamId = teamId,
                    name = forwardName,
                    nationality = forwardNationality,
                    position = "Forward",
                    shirtNumber = shirtNumber,
                    skill = (60..90).random()
                )
            )
        }

        return players
    }

    suspend fun saveGame(
        context: Context,
        gameName: String,
        teamCount: Int,
        onGameSaved: (Long) -> Unit
    ) {
        val database = AppDatabase.getDatabase(context)
        val gameDao = database.gameDao()
        val leagueDao = database.leagueDao()
        val teamDao = database.teamDao()
        val managerDao = database.managerDao()
        val playerDao = database.playerDao()
        val fixtureDao = database.fixtureDao()

        // Oyunu kaydet
        val gameId = gameDao.insertGame(
            Game(
                name = gameName,
                creationDate = Date()
            )
        )

        // Seçilen ligleri rastgele oluştur
        val selectedLeagueNames = footballNames.leagues.shuffled()
            .take(selectedLeagueCount.toInt())

        // Her lig için
        selectedLeagueNames.forEach { leagueName ->
            // Ligi kaydet
            val leagueId = leagueDao.insertLeague(
                League(
                    name = leagueName,
                    gameId = gameId
                )
            )

            // Bu ligdeki takımlar için kullanılan isimleri takip et
            val usedTeamNames = mutableSetOf<String>()

            // Takımları oluştur ve kaydet
            val teams = (1..teamCount).map {
                // Benzersiz bir takım ismi seç
                var teamName: String
                do {
                    teamName = footballNames.teams.random()
                } while (teamName in usedTeamNames)
                usedTeamNames.add(teamName)

                Team(
                    name = teamName,
                    leagueId = leagueId,
                    primaryColor = generateRandomColor(),
                    secondaryColor = generateRandomColor()
                )
            }

            // Takımları veritabanına kaydet ve ID'lerini al
            val teamIds = teams.map { team ->
                teamDao.insertTeam(team)
            }

            // Lig fikstürünü oluştur
            val fixtureTeams = teams.mapIndexed { index, team ->
                team.copy(teamId = teamIds[index])
            }

            val fixtures = FixtureGenerator.generateFixtures(
                teams = fixtureTeams,
                leagueId = leagueId,
                gameId = gameId
            )

            // Fikstürü veritabanına kaydet
            fixtureDao.insertFixtures(fixtures)

            // Her takım için menajer ve oyuncuları oluştur
            teamIds.forEach { teamId ->
                // Önce menajer oluştur
                val (managerName, managerNationality) = generateRandomName()
                val formation = formations.random()  // Formasyonu burada seç
                val manager = Manager(
                    name = managerName,
                    teamId = teamId,
                    formation = formation,
                    nationality = managerNationality
                )
                managerDao.insertManager(manager)

                // Oyuncuları menajerin formasyonuna göre oluştur
                val players = generatePlayers(teamId, formation)  // Seçilen formasyonu kullan
                players.forEach { player ->
                    playerDao.insertPlayer(player)
                }
            }
        }

        onGameSaved(gameId)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                TopBar(
                    title = "New Game",
                    onBackClick = onBackClick
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF4CAF50))
                    .padding(paddingValues)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = gameName,
                    onValueChange = { gameName = it },
                    label = { Text("Game Name", color = Color.White) },
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color.White,
                        focusedBorderColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedTextColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White.copy(alpha = 0.1f)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Number of Leagues: ${selectedLeagueCount.toInt()}",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Slider(
                            value = selectedLeagueCount,
                            onValueChange = { selectedLeagueCount = it },
                            valueRange = 4f..8f,
                            steps = 1,
                            colors = SliderDefaults.colors(
                                thumbColor = Color.White,
                                activeTrackColor = Color.White,
                                inactiveTrackColor = Color.White.copy(alpha = 0.3f)
                            )
                        )
                    }
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White.copy(alpha = 0.1f)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Teams per League: ${selectedTeamCount.toInt()}",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Slider(
                            value = selectedTeamCount,
                            onValueChange = { selectedTeamCount = it },
                            valueRange = 10f..20f,
                            steps = 10,
                            colors = SliderDefaults.colors(
                                thumbColor = Color.White,
                                activeTrackColor = Color.White,
                                inactiveTrackColor = Color.White.copy(alpha = 0.3f)
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = {
                        if (gameName.isNotBlank()) {
                            isLoading = true
                            scope.launch {
                                try {
                                    saveGame(
                                        context = context,
                                        gameName = gameName,
                                        teamCount = selectedTeamCount.toInt(),
                                        onGameSaved = onGameSaved
                                    )
                                } finally {
                                    isLoading = false
                                }
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color(0xFF4CAF50)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    enabled = gameName.isNotBlank() && !isLoading
                ) {
                    Text(
                        text = "Generate",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

            }
        }

        // Loading overlay'i buraya taşıdık
        if (isLoading) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(Color(0xFF4CAF50).copy(alpha = 0.9f)),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    val infiniteTransition = rememberInfiniteTransition(label = "")
                    val rotation by infiniteTransition.animateFloat(
                        initialValue = 0f,
                        targetValue = 360f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(2000, easing = LinearEasing),
                            repeatMode = RepeatMode.Restart
                        ), label = ""
                    )

                    Image(
                        painter = painterResource(id = R.drawable.baseline_sports_soccer_24),
                        contentDescription = "Loading",
                        modifier = Modifier
                            .size(80.dp)
                            .rotate(rotation)
                    )

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Creating Game...",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "Please wait while we generate your football universe",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}
