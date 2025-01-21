package com.denizcan.randomfootball.util

object Constants {
    val NATIONALITIES = listOf(
        "Brazil", "Germany", "Italy", "Argentina", "France",
        "England", "Spain", "Netherlands", "Portugal", "Turkey"
    )

    val FORMATIONS = listOf(
        "3-4-3", "3-5-2", "4-2-4", "4-3-3", "4-4-2",
        "4-5-1", "5-2-3", "5-3-2", "5-4-1"
    )
    val LEAGUE_NAMES = listOf(
        "Elite League",
        "Dream League",
        "Super League",
        "King League",
        "Master League",
        "Champion League",
        "Royal League",
        "Star League"
    )

    val TEAM_NAMES = listOf(
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

    val turkeyMaleNames = listOf(
        "Mehmet", "Ahmet", "Mustafa", "Ali", "Hüseyin",
        "İbrahim", "Yusuf", "Ömer", "Osman", "Emre",
        "Burak", "Arda", "Cengiz", "Volkan", "Oğuzhan",
        "Serdar", "Hakan", "Mert", "Cenk", "Kerem",
        "Çağlar", "Dorukhan", "Kaan", "Okay", "Yılmaz",
        "Uğurcan", "Altay", "Merih", "Zeki", "Umut",
        "Enes", "Halil", "Berat", "Taylan", "Efecan",
        "Barış", "Gökhan", "Furkan", "Batuhan", "Berkay",
        "Deniz", "Ertuğrul", "Ferdi", "İsmail", "Rıdvan",
        "Salih", "Taha", "Yunus", "Abdülkadir", "Berke",
        "Can", "Doğan", "Erdem", "Fatih", "Görkem",
        "Halit", "İlkay", "Kağan", "Mahmut", "Necip",
        "Onur", "Polat", "Recep", "Selçuk", "Tarık",
        "Utku", "Vedat", "Yasin", "Zafer", "Alper",
        "Bilal", "Caner", "Doğukan", "Eren", "Ferhat",
        "Semih", "Tolga", "Yiğit", "Alperen", "Atakan",
        "Berk", "Cemal", "Çınar", "Dağhan", "Emir",
        "Fırat", "Hamza", "İnanç", "Koray", "Levent",
        "Melih", "Nazım", "Orhan", "Özgür", "Ramazan",
        "Sinan", "Tuğrul", "Ufuk", "Yavuz", "Zekeriya"
    )

    val turkeySurnames = listOf(
        "Yılmaz", "Kaya", "Demir", "Çelik", "Şahin",
        "Yıldız", "Yıldırım", "Öztürk", "Aydın", "Özdemir",
        "Arslan", "Doğan", "Kılıç", "Aslan", "Çetin",
        "Erdoğan", "Koç", "Kurt", "Özkan", "Şimşek",
        "Korkmaz", "Çakır", "Alkan", "Bulut", "Keskin",
        "Turan", "Güler", "Yalçın", "Polat", "Aktaş",
        "Altun", "Duran", "Ateş", "Avcı", "Yüksel",
        "Tekin", "Kara", "Ünal", "Aksoy", "Erdem",
        "Güneş", "Kaplan", "Özer", "Güzel", "Şen",
        "Taş", "Özcan", "Kartal", "Acar", "Yavuz",
        "Gül", "Sönmez", "Çetinkaya", "Demirci", "Ercan",
        "Akın", "Türk", "Kocaman", "Özbek", "Aydoğan",
        "Erbaş", "Gündüz", "Işık", "Kahveci", "Mutlu",
        "Ocak", "Parlak", "Sağlam", "Toprak", "Uysal",
        "Varol", "Yaman", "Zengin", "Akbaş", "Başaran",
        "Sarı", "Kılınç", "Yörük", "Akman", "Bilgin",
        "Demirel", "Ekinci", "Fazlı", "Gökçe", "Hacıoğlu",
        "İnan", "Kalaycı", "Mercan", "Nalçacı", "Oğuz",
        "Poyraz", "Rüzgar", "Soylu", "Tanrıverdi", "Uçar",
        "Vural", "Yücel", "Zorlu", "Aksu", "Bayrak"
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

    // Önceden tanımlanmış takım renkleri
    val TEAM_COLORS = listOf(
        "#FF0000", // Kırmızı
        "#0000FF", // Mavi
        "#008000", // Yeşil
        "#800080", // Mor
        "#FFA500", // Turuncu
        "#FFD700", // Altın
        "#4B0082", // Indigo
        "#800000", // Bordo
        "#000080", // Lacivert
        "#008080", // Turkuaz
        "#FF1493", // Pembe
        "#4682B4", // Çelik Mavisi
        "#8B4513", // Kahverengi
        "#556B2F", // Koyu Yeşil
        "#483D8B", // Koyu Slate Mavi
        "#B8860B"  // Koyu Altın
    )
}