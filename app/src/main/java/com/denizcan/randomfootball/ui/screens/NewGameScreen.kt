package com.denizcan.randomfootball.ui.screens

import android.content.Context
import android.util.Log
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
import com.denizcan.randomfootball.data.model.LeagueTable
import com.denizcan.randomfootball.util.Constants
import com.denizcan.randomfootball.util.FixtureGenerator
import kotlinx.coroutines.flow.first

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




    // İki farklı renk seçen fonksiyon
    fun generateTeamColors(): Pair<String, String> {
        val firstColor = Constants.TEAM_COLORS.random()
        val secondColor = Constants.TEAM_COLORS.filter { it != firstColor }.random()
        return Pair(firstColor, secondColor)
    }

    // namesByNationality değişkeni burada tanımlanıyor
    val namesByNationality = mapOf(
        "England" to Pair(Constants.englandMaleNames, Constants.englandSurnames),
        "France" to Pair(Constants.franceMaleNames, Constants.franceSurnames),
        "Spain" to Pair(Constants.spainMaleNames, Constants.spainSurnames),
        "Germany" to Pair(Constants.germanyMaleNames, Constants.germanySurnames),
        "Italy" to Pair(Constants.italyMaleNames, Constants.italySurnames),
        "Portugal" to Pair(Constants.portugalMaleNames, Constants.portugalSurnames),
        "Netherlands" to Pair(Constants.netherlandsMaleNames, Constants.netherlandsSurnames),
        "Turkey" to Pair(Constants.turkeyMaleNames, Constants.turkeySurnames),
        "Argentina" to Pair(Constants.argentinaMaleNames, Constants.argentinaSurnames),
        "Brazil" to Pair(Constants.brazilMaleNames, Constants.brazilSurnames)
    )



    fun generateRandomName(gameId: Long): Pair<String, String> {
        val selectedNationality = Constants.NATIONALITIES.random()
        val (nameList, surnameList) = namesByNationality[selectedNationality]!!

        val firstName = nameList.random()
        val lastName = surnameList.random()

        return Pair("$firstName $lastName", selectedNationality)
    }

    fun generatePlayers(gameId: Long, teamId: Long, formation: String): List<Player> {
        val players = mutableListOf<Player>()
        val usedShirtNumbers = mutableSetOf<Int>()
        val usedNames = mutableSetOf<String>()

        // Formasyonu parse et (örn: "4-3-3")
        val formationParts = formation.split("-").map { it.toInt() }
        val defenders = formationParts[0]
        val midfielders = formationParts[1]
        val forwards = formationParts[2]

        // Pozisyonlara göre kullanılabilecek forma numaraları
        val goalkeeperNumbers = setOf(1)
        val defenderNumbers = setOf(2, 3, 4, 5, 6)
        val midfielderNumbers = setOf(7, 8)
        val forwardNumbers = setOf(9)
        val attackingNumbers = setOf(10, 11) // Forvet ve orta saha için
        val commonNumbers = (12..99).toSet() // Tüm pozisyonlar için

        fun getAvailableNumber(position: String): Int {
            val availableNumbers = when (position) {
                "Goalkeeper" -> {
                    if (!usedShirtNumbers.containsAll(goalkeeperNumbers)) {
                        goalkeeperNumbers - usedShirtNumbers
                    } else commonNumbers - usedShirtNumbers
                }
                "Defender" -> {
                    if (!usedShirtNumbers.containsAll(defenderNumbers)) {
                        defenderNumbers - usedShirtNumbers
                    } else commonNumbers - usedShirtNumbers
                }
                "Midfielder" -> {
                    val midfielderPool = if (!usedShirtNumbers.containsAll(midfielderNumbers)) {
                        midfielderNumbers - usedShirtNumbers
                    } else if (!usedShirtNumbers.containsAll(attackingNumbers)) {
                        attackingNumbers - usedShirtNumbers
                    } else commonNumbers - usedShirtNumbers
                    midfielderPool
                }
                "Forward" -> {
                    val forwardPool = if (!usedShirtNumbers.containsAll(forwardNumbers)) {
                        forwardNumbers - usedShirtNumbers
                    } else if (!usedShirtNumbers.containsAll(attackingNumbers)) {
                        attackingNumbers - usedShirtNumbers
                    } else commonNumbers - usedShirtNumbers
                    forwardPool
                }
                else -> commonNumbers - usedShirtNumbers
            }
            return availableNumbers.random()
        }

        fun createTeam(isFirstTeam: Boolean) {
            // Kaleci
            var (goalkeeperName, goalkeeperNationality) = generateRandomName(gameId)
            while (goalkeeperName in usedNames) {
                val newName = generateRandomName(gameId)
                goalkeeperName = newName.first
                goalkeeperNationality = newName.second
            }
            usedNames.add(goalkeeperName)

            val shirtNumber = getAvailableNumber("Goalkeeper")
            usedShirtNumbers.add(shirtNumber)

            players.add(
                Player(
                    teamId = teamId,
                    name = goalkeeperName,
                    nationality = goalkeeperNationality,
                    position = "Goalkeeper",
                    shirtNumber = shirtNumber,
                    skill = if (isFirstTeam) (65..90).random() else (55..75).random()
                )
            )

            // Defans
            repeat(defenders) {
                var (defenderName, defenderNationality) = generateRandomName(gameId)
                while (defenderName in usedNames) {
                    val newName = generateRandomName(gameId)
                    defenderName = newName.first
                    defenderNationality = newName.second
                }
                usedNames.add(defenderName)

                val defenderNumber = getAvailableNumber("Defender")
                usedShirtNumbers.add(defenderNumber)

                players.add(
                    Player(
                        teamId = teamId,
                        name = defenderName,
                        nationality = defenderNationality,
                        position = "Defender",
                        shirtNumber = defenderNumber,
                        skill = if (isFirstTeam) (65..90).random() else (55..75).random()
                    )
                )
            }

            // Orta saha
            repeat(midfielders) {
                var (midfielderName, midfielderNationality) = generateRandomName(gameId)
                while (midfielderName in usedNames) {
                    val newName = generateRandomName(gameId)
                    midfielderName = newName.first
                    midfielderNationality = newName.second
                }
                usedNames.add(midfielderName)

                val midfielderNumber = getAvailableNumber("Midfielder")
                usedShirtNumbers.add(midfielderNumber)

                players.add(
                    Player(
                        teamId = teamId,
                        name = midfielderName,
                        nationality = midfielderNationality,
                        position = "Midfielder",
                        shirtNumber = midfielderNumber,
                        skill = if (isFirstTeam) (65..90).random() else (55..75).random()
                    )
                )
            }

            // Forvet
            repeat(forwards) {
                var (forwardName, forwardNationality) = generateRandomName(gameId)
                while (forwardName in usedNames) {
                    val newName = generateRandomName(gameId)
                    forwardName = newName.first
                    forwardNationality = newName.second
                }
                usedNames.add(forwardName)

                val forwardNumber = getAvailableNumber("Forward")
                usedShirtNumbers.add(forwardNumber)

                players.add(
                    Player(
                        teamId = teamId,
                        name = forwardName,
                        nationality = forwardNationality,
                        position = "Forward",
                        shirtNumber = forwardNumber,
                        skill = if (isFirstTeam) (65..90).random() else (55..75).random()
                    )
                )
            }
        }

        // İlk 11'i oluştur
        createTeam(isFirstTeam = true)

        // Yedek 11'i oluştur
        createTeam(isFirstTeam = false)

        // 3 ekstra oyuncu ekle
        // 1 kaleci
        var (extraGkName, extraGkNationality) = generateRandomName(gameId)
        while (extraGkName in usedNames) {
            val newName = generateRandomName(gameId)
            extraGkName = newName.first
            extraGkNationality = newName.second
        }
        usedNames.add(extraGkName)

        var shirtNumber = (1..99).random()
        while (shirtNumber in usedShirtNumbers) {
            shirtNumber = (1..99).random()
        }
        usedShirtNumbers.add(shirtNumber)

        players.add(
            Player(
                teamId = teamId,
                name = extraGkName,
                nationality = extraGkNationality,
                position = "Goalkeeper",
                shirtNumber = shirtNumber,
                skill = (55..75).random()
            )
        )

        // 2 random pozisyon oyuncusu
        repeat(2) {
            val position = listOf("Defender", "Midfielder", "Forward").random()
            var (extraPlayerName, extraPlayerNationality) = generateRandomName(gameId)
            while (extraPlayerName in usedNames) {
                val newName = generateRandomName(gameId)
                extraPlayerName = newName.first
                extraPlayerNationality = newName.second
            }
            usedNames.add(extraPlayerName)

            do {
                shirtNumber = (1..99).random()
            } while (shirtNumber in usedShirtNumbers)
            usedShirtNumbers.add(shirtNumber)

            players.add(
                Player(
                    teamId = teamId,
                    name = extraPlayerName,
                    nationality = extraPlayerNationality,
                    position = position,
                    shirtNumber = shirtNumber,
                    skill = (55..75).random()
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
        val leagueTableDao = database.leagueTableDao()

        // 1. Oyunu oluştur
        val gameId = gameDao.insertGame(
            Game(
                name = gameName,
                creationDate = Date()
            )
        )

        // 2. Ligleri seç ve oluştur
        val selectedLeagueNames = Constants.LEAGUE_NAMES.shuffled()
            .take(selectedLeagueCount.toInt())

        // Her lig için işlemler
        selectedLeagueNames.forEach { leagueName ->
            // 3. Ligi oluştur
            val leagueId = leagueDao.insertLeague(
                League(
                    name = leagueName,
                    gameId = gameId
                )
            )
            Log.d("NewGameScreen", "Created league with ID: $leagueId")

            // Takım isimlerini takip et
            val usedTeamNames = mutableSetOf<String>()
            val usedManagerNames = mutableSetOf<String>()

            // 4. Her takım için işlemler
            val teamIds = (1..teamCount).map {
                // 4.1 Takım ismini seç
                var teamName: String
                do {
                    teamName = Constants.TEAM_NAMES.random()
                } while (teamName in usedTeamNames)
                usedTeamNames.add(teamName)

                // 4.2 Takım renklerini seç
                val (primaryColor, secondaryColor) = generateTeamColors()

                // 4.3 Önce takımı oluştur (managerId olmadan)
                val team = Team(
                    name = teamName,
                    leagueId = leagueId,
                    managerId = 0, // Geçici olarak 0
                    primaryColor = primaryColor,
                    secondaryColor = secondaryColor
                )
                val teamId = teamDao.insertTeam(team)

                // 4.4 Sonra menajeri oluştur
                var (managerName, managerNationality) = generateRandomName(gameId)
                while (managerName in usedManagerNames) {
                    val newName = generateRandomName(gameId)
                    managerName = newName.first
                    managerNationality = newName.second
                }
                usedManagerNames.add(managerName)

                val formation = Constants.FORMATIONS.random()

                val manager = Manager(
                    name = managerName,
                    teamId = teamId, // Artık gerçek teamId'yi kullanabiliriz
                    gameId = gameId,
                    nationality = managerNationality,
                    formation = formation
                )
                val managerId = managerDao.insertManager(manager)

                // 4.5 Takımın managerId'sini güncelle
                teamDao.updateTeam(team.copy(managerId = managerId))

                // 4.6 Menajerin formasyonuna göre oyuncuları oluştur
                val players = generatePlayers(gameId, teamId, formation)
                players.forEach { player ->
                    playerDao.insertPlayer(player)
                }

                // 4.7 Lig tablosu kaydı oluştur
                val leagueTable = LeagueTable(
                    leagueId = leagueId,
                    teamId = teamId,
                    position = 0,
                    points = 0,
                    played = 0,
                    won = 0,
                    drawn = 0,
                    lost = 0,
                    goalsFor = 0,
                    goalsAgainst = 0,
                    goalDifference = 0
                )
                leagueTableDao.insertLeagueTable(leagueTable)

                teamId
            }.sorted()

            // 5. Fikstür oluştur
            val teams = teamIds.map { teamId ->
                teamDao.getTeamById(teamId).first()
            }.filterNotNull()  // null takımları filtrele

            val fixtures = FixtureGenerator.generateFixtures(
                teams = teams,
                leagueId = leagueId,
                gameId = gameId
            )

            Log.d("NewGameScreen", """
                Generated Fixtures:
                League ID: $leagueId
                Game ID: $gameId
                Fixture Count: ${fixtures.size}
                Fixtures: $fixtures
            """.trimIndent())

            // 6. Fikstürü kaydet
            fixtureDao.insertFixtures(fixtures)

            // 7. Lig sıralamalarını güncelle
            leagueTableDao.updatePositions(leagueId)
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