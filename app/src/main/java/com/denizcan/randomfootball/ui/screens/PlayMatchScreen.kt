package com.denizcan.randomfootball.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.denizcan.randomfootball.data.AppDatabase
import com.denizcan.randomfootball.ui.components.TopBar
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextOverflow
import com.denizcan.randomfootball.R
import com.denizcan.randomfootball.data.model.Player
import com.denizcan.randomfootball.data.model.Team
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import kotlin.random.Random
import androidx.compose.foundation.lazy.items
import android.util.Log
import com.denizcan.randomfootball.data.dao.FixtureDao
import com.denizcan.randomfootball.data.dao.PlayerStatsDao
import com.denizcan.randomfootball.data.model.Fixture
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.denizcan.randomfootball.util.TeamUtils

// Maç fazlarını temsil eden enum
private enum class MatchPhase {
    NOT_STARTED,      // 0. dakika - Maç başlamadı
    FIRST_PHASE,      // 15. dakika
    SECOND_PHASE,     // 30. dakika
    THIRD_PHASE,      // 45. dakika
    FOURTH_PHASE,     // 60. dakika
    FIFTH_PHASE,      // 75. dakika
    SIXTH_PHASE,      // 90. dakika
    MATCH_ENDED       // 95. dakika - Maç bitti
}

// Takım istatistiklerini tutan data class
private data class TeamStats(
    val attackPoints: Int,
    val defensePoints: Int
)

// Takımın ilk 11'ini formasyona göre seçen fonksiyon
private fun selectFirstEleven(players: List<Player>, formation: String): List<Player> {
    if (players.isEmpty()) {
        return emptyList()
    }

    val firstEleven = mutableListOf<Player>()

    // Kaleci seç (her zaman 1 tane)
    val goalkeepers = players.filter { it.position == "Goalkeeper" }
        .sortedByDescending { it.skill }

    if (goalkeepers.isNotEmpty()) {
        firstEleven.add(goalkeepers.first())
    }

    // Formasyonu parçala (örn: "4-3-3" -> [4,3,3])
    val formationParts = formation.split("-").map { it.toInt() }

    // Defans oyuncularını seç
    val defenders = players.filter { it.position == "Defender" }
        .sortedByDescending { it.skill }
    firstEleven.addAll(defenders.take(formationParts[0]))

    // Orta saha oyuncularını seç
    val midfielders = players.filter { it.position == "Midfielder" }
        .sortedByDescending { it.skill }
    firstEleven.addAll(midfielders.take(formationParts[1]))

    // Forvet oyuncularını seç
    val forwards = players.filter { it.position == "Forward" }
        .sortedByDescending { it.skill }
    firstEleven.addAll(forwards.take(formationParts[2]))

    return firstEleven
}

// Takım puanlarını formasyona göre hesaplayan fonksiyon
private fun calculateTeamStats(players: List<Player>, formation: String): TeamStats {
    // İlk 11'i seç
    val firstEleven = selectFirstEleven(players, formation)

    // Savunma puanı (Kaleci + Defans oyuncuları)
    val defensePoints = firstEleven
        .filter { it.position == "Goalkeeper" || it.position == "Defender" }
        .sumOf { it.skill }

    // Hücum puanı (Orta saha + Forvet oyuncuları)
    val attackPoints = firstEleven
        .filter { it.position == "Midfielder" || it.position == "Forward" }
        .sumOf { it.skill }

    Log.d("PlayMatchScreen", """
        Team Stats:
        First Eleven Size: ${firstEleven.size}
        Defense Players: ${firstEleven.count { it.position == "Goalkeeper" || it.position == "Defender" }}
        Attack Players: ${firstEleven.count { it.position == "Midfielder" || it.position == "Forward" }}
        Defense Points: $defensePoints
        Attack Points: $attackPoints
    """.trimIndent())

    return TeamStats(attackPoints, defensePoints)
}

// Event data class'ı
data class MatchEvent(
    val minute: Int,
    val eventType: EventType,
    val player: Player,
    val assist: Player? = null,
    val team: Team
)

enum class EventType {
    GOAL
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayMatchScreen(
    fixtureId: Long,
    onBackClick: () -> Unit,
    onMatchEnd: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val database = remember { AppDatabase.getDatabase(context) }
    val fixtureDao = remember { database.fixtureDao() }
    val teamDao = remember { database.teamDao() }
    val playerDao = remember { database.playerDao() }
    val playerStatsDao = remember { database.playerStatsDao() }

    // Database state'leri
    val fixture = fixtureDao.getFixtureById(fixtureId).collectAsState(initial = null)
    val gameId = remember(fixture.value?.gameId) { fixture.value?.gameId ?: 0L }

    val homeTeam = remember(fixture.value?.homeTeamId) {
        fixture.value?.homeTeamId?.let { teamDao.getTeamById(it) }
    }?.collectAsState(initial = null)

    val awayTeam = remember(fixture.value?.awayTeamId) {
        fixture.value?.awayTeamId?.let { teamDao.getTeamById(it) }
    }?.collectAsState(initial = null)

    // Menajerleri al
    val homeManager = remember(homeTeam?.value?.managerId) {
        homeTeam?.value?.managerId?.let { managerId ->
            database.managerDao().getManagerById(managerId)
        }
    }?.collectAsState(initial = null)

    val awayManager = remember(awayTeam?.value?.managerId) {
        awayTeam?.value?.managerId?.let { managerId ->
            database.managerDao().getManagerById(managerId)
        }
    }?.collectAsState(initial = null)

    // Takımların oyuncularını al
    val homePlayers = remember(fixture.value?.homeTeamId) {
        fixture.value?.homeTeamId?.let { playerDao.getPlayersByTeamId(it) }
    }?.collectAsState(initial = emptyList())
    val awayPlayers = remember(fixture.value?.awayTeamId) {
        fixture.value?.awayTeamId?.let { playerDao.getPlayersByTeamId(it) }
    }?.collectAsState(initial = emptyList())

    // Takımların formasyonlarını al
    val homeTeamFormation = homeManager?.value?.formation ?: "4-4-2"
    val awayTeamFormation = awayManager?.value?.formation ?: "4-4-2"

    // İlk 11'leri seç
    val homeFirstEleven = remember(homePlayers?.value, homeTeamFormation) {
        homePlayers?.value?.let { players ->
            TeamUtils.selectFirstEleven(players, homeTeamFormation)
        } ?: emptyList()
    }

    val awayFirstEleven = remember(awayPlayers?.value, awayTeamFormation) {
        awayPlayers?.value?.let { players ->
            TeamUtils.selectFirstEleven(players, awayTeamFormation)
        } ?: emptyList()
    }

    // Takım puanlarını hesapla
    val homeTeamStats = remember(homeFirstEleven, homeTeamFormation) {
        homePlayers?.value?.let { players ->
            calculateTeamStats(players, homeTeamFormation)
        } ?: TeamStats(0, 0)
    }

    val awayTeamStats = remember(awayFirstEleven, awayTeamFormation) {
        awayPlayers?.value?.let { players ->
            calculateTeamStats(players, awayTeamFormation)
        } ?: TeamStats(0, 0)
    }

    // Maç state'leri
    var currentPhase by remember { mutableStateOf(MatchPhase.NOT_STARTED) }
    var currentMinute by remember { mutableStateOf(0) }
    var currentTeamWithBall by remember { mutableStateOf<String?>(null) }
    var homeScore by remember { mutableStateOf(0) }
    var awayScore by remember { mutableStateOf(0) }
    var isMatchInProgress by remember { mutableStateOf(false) }
    var isMatchEnded by remember { mutableStateOf(false) }

    // Animasyon state'leri
    var showBallAnimation by remember { mutableStateOf(false) }
    var showTeamAnimation by remember { mutableStateOf(false) }

    // Faz durumlarını takip etmek için yeni state'ler
    var homeTeamPlayed by remember { mutableStateOf(false) }
    var awayTeamPlayed by remember { mutableStateOf(false) }

    // Maç simülasyonu fonksiyonları
    val matchFunctions = remember {
        object {
            var simulateFirstPhase: (() -> Unit)? = null
            var simulateRandomPhase: ((String) -> Unit)? = null
            var proceedToNextPhase: (() -> Unit)? = null
            var endMatch: (() -> Unit)? = null
            var simulateMatch: (() -> Unit)? = null
        }
    }

    // Maç olaylarını tutacak liste
    var matchEvents by remember { mutableStateOf(listOf<MatchEvent>()) }

    // Faz geçişlerini yöneten fonksiyon
    fun checkPhaseCompletion() {
        if (homeTeamPlayed && awayTeamPlayed) {
            homeTeamPlayed = false
            awayTeamPlayed = false
            matchFunctions.proceedToNextPhase?.invoke()
        }
    }

    // Animasyon değerleri
    val ballPosition by animateFloatAsState(
        targetValue = if (showBallAnimation) 1f else 0f,
        animationSpec = tween(1000),
        finishedListener = {
            if (showBallAnimation) {
                scope.launch {
                    delay(200) // Kısa bir gecikme
                    showBallAnimation = false
                    showTeamAnimation = true
                }
            }
        }
    )

    val teamTextAlpha by animateFloatAsState(
        targetValue = if (showTeamAnimation) 1f else 0f,
        animationSpec = tween(500),
        finishedListener = {
            if (showTeamAnimation) {
                scope.launch {
                    delay(1000)
                    showTeamAnimation = false

                    when (currentPhase) {
                        MatchPhase.FIRST_PHASE -> {
                            delay(500)
                            when (currentTeamWithBall) {
                                "home" -> {
                                    homeTeamPlayed = true
                                    if (!awayTeamPlayed) {
                                        currentTeamWithBall = "away"
                                        matchFunctions.simulateFirstPhase?.invoke()
                                    }
                                }

                                "away" -> {
                                    awayTeamPlayed = true
                                    checkPhaseCompletion()
                                }
                            }
                        }

                        MatchPhase.SECOND_PHASE,
                        MatchPhase.THIRD_PHASE,
                        MatchPhase.FOURTH_PHASE,
                        MatchPhase.FIFTH_PHASE,
                        MatchPhase.SIXTH_PHASE -> {
                            delay(500)
                            when (currentTeamWithBall) {
                                "home" -> {
                                    homeTeamPlayed = true
                                    if (!awayTeamPlayed) {
                                        currentTeamWithBall = "away"
                                        matchFunctions.simulateRandomPhase?.invoke("away")
                                    }
                                }

                                "away" -> {
                                    awayTeamPlayed = true
                                    checkPhaseCompletion()
                                }
                            }
                        }

                        else -> {}
                    }
                }
            }
        }
    )

    val ballRotation by animateFloatAsState(
        targetValue = if (showBallAnimation) 360f else 0f,
        animationSpec = tween(1000)
    )

    // Maç başlangıcında çağrılacak fonksiyon
    suspend fun incrementAppearances() {
        // İlk 11'deki oyuncular için önce kayıt oluştur ve appearances'ı artır
        (homeFirstEleven + awayFirstEleven).forEach { player ->
            playerStatsDao.createStatsIfNotExists(player.playerId, gameId)
            playerStatsDao.incrementAppearance(player.playerId, gameId)
        }
    }

    // Gol atan oyuncuyu seçmek için yardımcı fonksiyon
    fun selectScorer(players: List<Player>): Player {
        return when (Random.nextInt(100)) {
            in 0..55 -> players.filter { it.position == "Forward" }  // %55 Forvet
            in 56..85 -> players.filter { it.position == "Midfielder" }  // %30 Orta Saha
            else -> players.filter { it.position == "Defender" }  // %15 Defans
        }.randomOrNull() ?: players.filter {
            it.position != "Goalkeeper"
        }.random()
    }

    // Asist yapan oyuncuyu seçmek için yardımcı fonksiyon
    fun selectAssist(players: List<Player>, scorer: Player): Player? {
        val eligiblePlayers = players.filter { it.playerId != scorer.playerId && it.position != "Goalkeeper" }
        if (eligiblePlayers.isEmpty()) return null

        return when (Random.nextInt(100)) {
            in 0..50 -> eligiblePlayers.filter { it.position == "Midfielder" }  // %50 Orta Saha
            in 51..80 -> eligiblePlayers.filter { it.position == "Forward" }    // %30 Forvet
            else -> eligiblePlayers.filter { it.position == "Defender" }        // %20 Defans
        }.randomOrNull()
    }

    // Diğer maçların simülasyonu için yardımcı fonksiyon - Önce tanımlanmalı
    suspend fun simulateOtherMatch(
        database: AppDatabase,
        weekFixture: Fixture,
        currentFixture: Fixture,
        playerStatsDao: PlayerStatsDao,
        fixtureDao: FixtureDao
    ) = withContext(Dispatchers.IO) {
        try {
            // Takımları al
            val homeTeam = database.teamDao().getTeamById(weekFixture.homeTeamId).first()
                ?: throw Exception("Home team not found")
            val awayTeam = database.teamDao().getTeamById(weekFixture.awayTeamId).first()
                ?: throw Exception("Away team not found")

            // Menajerleri al
            val homeManager = database.managerDao().getManagerById(homeTeam.managerId).first()
                ?: throw Exception("Home manager not found")
            val awayManager = database.managerDao().getManagerById(awayTeam.managerId).first()
                ?: throw Exception("Away manager not found")

            // Oyuncuları al
            val homeTeamPlayers = database.playerDao().getPlayersByTeamId(weekFixture.homeTeamId).first()
            val awayTeamPlayers = database.playerDao().getPlayersByTeamId(weekFixture.awayTeamId).first()

            // Formasyonları al ve ilk 11'leri seç
            val homeFirstEleven = TeamUtils.selectFirstEleven(homeTeamPlayers, homeManager.formation)
            val awayFirstEleven = TeamUtils.selectFirstEleven(awayTeamPlayers, awayManager.formation)

            // İlk 11'deki oyuncuların appearances sayısını artır
            homeFirstEleven.forEach { player ->
                Log.d("PlayMatchScreen", "Creating stats for home player: ${player.name}")
                playerStatsDao.createStatsIfNotExists(player.playerId, weekFixture.gameId)
                Log.d("PlayMatchScreen", "Incrementing appearance for home player: ${player.name}")
                playerStatsDao.incrementAppearance(player.playerId, weekFixture.gameId)
            }

            awayFirstEleven.forEach { player ->
                Log.d("PlayMatchScreen", "Creating stats for away player: ${player.name}")
                playerStatsDao.createStatsIfNotExists(player.playerId, weekFixture.gameId)
                Log.d("PlayMatchScreen", "Incrementing appearance for away player: ${player.name}")
                playerStatsDao.incrementAppearance(player.playerId, weekFixture.gameId)
            }

            // Takım puanlarını hesapla
            val homeStats = calculateTeamStats(homeTeamPlayers, homeManager.formation)
            val awayStats = calculateTeamStats(awayTeamPlayers, awayManager.formation)

            var homeGoals = 0
            var awayGoals = 0
            val scorers = mutableListOf<Pair<Player, Player?>>() // Gol atan ve asist yapan oyuncuları tut

            // İlk faz - Hücum-Savunma karşılaştırması
            if (homeStats.attackPoints > awayStats.defensePoints) {
                homeGoals++
                val scorer = selectScorer(homeFirstEleven)
                val assist = selectAssist(homeFirstEleven, scorer)
                scorers.add(scorer to assist)
            }

            if (awayStats.attackPoints > homeStats.defensePoints) {
                awayGoals++
                val scorer = selectScorer(awayFirstEleven)
                val assist = selectAssist(awayFirstEleven, scorer)
                scorers.add(scorer to assist)
            }

            // Diğer fazlar için de aynı mantık
            repeat(5) {
                if ((1..5).random() == 5) {
                    homeGoals++
                    val scorer = selectScorer(homeFirstEleven)
                    val assist = selectAssist(homeFirstEleven, scorer)
                    scorers.add(scorer to assist)
                }
                if ((1..5).random() == 5) {
                    awayGoals++
                    val scorer = selectScorer(awayFirstEleven)
                    val assist = selectAssist(awayFirstEleven, scorer)
                    scorers.add(scorer to assist)
                }
            }

            // Maç sonunda clean sheet kontrolü
            if (homeGoals == 0) {
                awayFirstEleven.forEach { player ->
                    playerStatsDao.createStatsIfNotExists(player.playerId, weekFixture.gameId)
                    playerStatsDao.updateCleanSheet(player.playerId, weekFixture.gameId)
                }
            }

            if (awayGoals == 0) {
                homeFirstEleven.forEach { player ->
                    playerStatsDao.createStatsIfNotExists(player.playerId, weekFixture.gameId)
                    playerStatsDao.updateCleanSheet(player.playerId, weekFixture.gameId)
                }
            }

            // Maç sonucunu kaydet
            fixtureDao.updateFixture(
                weekFixture.copy(
                    homeScore = homeGoals,
                    awayScore = awayGoals,
                    isPlayed = true
                )
            )

            // Lig tablosunu güncelle
            database.leagueTableDao().updateAfterMatch(
                leagueId = weekFixture.leagueId,
                homeTeamId = weekFixture.homeTeamId,
                awayTeamId = weekFixture.awayTeamId,
                homeScore = homeGoals,
                awayScore = awayGoals
            )

            // Gol ve asist istatistiklerini bir kerede güncelle
            scorers.forEach { (scorer, assist) ->
                playerStatsDao.createStatsIfNotExists(scorer.playerId, weekFixture.gameId)
                playerStatsDao.updateMatchStats(
                    playerId = scorer.playerId,
                    gameId = weekFixture.gameId,
                    goals = 1,
                    assists = 0
                )

                assist?.let {
                    playerStatsDao.createStatsIfNotExists(it.playerId, weekFixture.gameId)
                    playerStatsDao.updateMatchStats(
                        playerId = it.playerId,
                        gameId = weekFixture.gameId,
                        goals = 0,
                        assists = 1
                    )
                }
            }

            true
        } catch (e: Exception) {
            Log.e("PlayMatchScreen", "Error in match simulation", e)
            false
        }
    }

    // Gol olayını işleyecek fonksiyon
    suspend fun handleGoal(scoringTeam: Team, opposingTeam: Team) {
        val manager = database.managerDao().getManagerById(scoringTeam.managerId).firstOrNull()
            ?: throw Exception("Manager not found")

        val scoringTeamPlayers = playerDao.getPlayersByTeamId(scoringTeam.teamId).first()
        val firstEleven = TeamUtils.selectFirstEleven(scoringTeamPlayers, manager.formation)

        // Gol atan oyuncuyu seç
        val scorer = selectScorer(firstEleven)

        // Gol atan oyuncu için kayıt oluştur ve güncelle
        playerStatsDao.createStatsIfNotExists(scorer.playerId, gameId)

        // İstatistikleri güncellemeden önce kontrol et
        val beforeStats = playerStatsDao.getPlayerStatsDirectly(scorer.playerId, gameId)
        Log.d("PlayMatchScreen", """
            Before update - Scorer ${scorer.name}:
            Goals: ${beforeStats?.goals}
            Assists: ${beforeStats?.assists}
            Appearances: ${beforeStats?.appearances}
        """.trimIndent())

        playerStatsDao.updateMatchStats(
            playerId = scorer.playerId,
            gameId = gameId,
            goals = 1,
            assists = 0
        )

        // İstatistikleri güncellemeden sonra kontrol et
        val afterStats = playerStatsDao.getPlayerStatsDirectly(scorer.playerId, gameId)
        Log.d("PlayMatchScreen", """
            After update - Scorer ${scorer.name}:
            Goals: ${afterStats?.goals}
            Assists: ${afterStats?.assists}
            Appearances: ${afterStats?.appearances}
        """.trimIndent())

        // Aktif istatistikleri kontrol et
        val activeStats = playerStatsDao.getActiveStatsCount(gameId)
        Log.d("PlayMatchScreen", "Active stats count in game $gameId: $activeStats")

        // Asist yapan oyuncuyu seç
        val assist = selectAssist(firstEleven, scorer)

        // Asist yapan oyuncu için kayıt oluştur ve güncelle
        assist?.let {
            playerStatsDao.createStatsIfNotExists(it.playerId, gameId)
            playerStatsDao.updateMatchStats(
                playerId = it.playerId,
                gameId = gameId,
                goals = 0,
                assists = 1
            )
        }

        // Olayı listeye ekle
        matchEvents = matchEvents + MatchEvent(
            minute = currentMinute,
            eventType = EventType.GOAL,
            player = scorer,
            assist = assist,
            team = scoringTeam
        )
    }

    // Fonksiyonları tanımla
    matchFunctions.simulateFirstPhase = {
        homePlayers?.value?.let { hPlayers ->
            awayPlayers?.value?.let { aPlayers ->
                val homeStats = calculateTeamStats(hPlayers, homeTeamFormation)
                val awayStats = calculateTeamStats(aPlayers, awayTeamFormation)

                when (currentTeamWithBall) {
                    "home" -> {
                        if (homeStats.attackPoints > awayStats.defensePoints) {
                            homeScore++
                            showBallAnimation = true
                            // Gol olayını işle - scope.launch kaldırıldı
                            scope.launch {
                                withContext(Dispatchers.IO) {
                                    homeTeam?.value?.let { team ->
                                        awayTeam?.value?.let { opposingTeam ->
                                            handleGoal(team, opposingTeam)
                                        }
                                    }
                                }
                            }
                        } else {
                            homeTeamPlayed = true
                            if (!awayTeamPlayed) {
                                currentTeamWithBall = "away"
                                matchFunctions.simulateFirstPhase?.invoke()
                            }
                        }
                    }
                    "away" -> {
                        if (awayStats.attackPoints > homeStats.defensePoints) {
                            awayScore++
                            showBallAnimation = true
                            // Gol olayını işle - scope.launch kaldırıldı
                            scope.launch {
                                withContext(Dispatchers.IO) {
                                    awayTeam?.value?.let { team ->
                                        homeTeam?.value?.let { opposingTeam ->
                                            handleGoal(team, opposingTeam)
                                        }
                                    }
                                }
                            }
                        } else {
                            awayTeamPlayed = true
                            checkPhaseCompletion()
                        }
                    }
                }
            }
        }
    }

    matchFunctions.simulateRandomPhase = { team ->
        if ((1..5).random() == 5) {
            if (team == "home") {
                homeScore++
                // Gol olayını işle
                scope.launch {
                    withContext(Dispatchers.IO) {
                        homeTeam?.value?.let { scoringTeam ->
                            awayTeam?.value?.let { opposingTeam ->
                                handleGoal(scoringTeam, opposingTeam)
                            }
                        }
                    }
                }
            } else {
                awayScore++
                // Gol olayını işle
                scope.launch {
                    withContext(Dispatchers.IO) {
                        awayTeam?.value?.let { scoringTeam ->
                            homeTeam?.value?.let { opposingTeam ->
                                handleGoal(scoringTeam, opposingTeam)
                            }
                        }
                    }
                }
            }
            currentTeamWithBall = team
            showBallAnimation = true
        } else {
            when (team) {
                "home" -> {
                    homeTeamPlayed = true
                    if (!awayTeamPlayed) {
                        currentTeamWithBall = "away"
                        matchFunctions.simulateRandomPhase?.invoke("away")
                    } else {
                        checkPhaseCompletion()
                    }
                }
                "away" -> {
                    awayTeamPlayed = true
                    checkPhaseCompletion()
                }
            }
        }
    }

    matchFunctions.proceedToNextPhase = {
        when (currentPhase) {
            MatchPhase.NOT_STARTED -> {
                currentPhase = MatchPhase.FIRST_PHASE
                currentMinute = 15
            }

            MatchPhase.FIRST_PHASE -> {
                currentPhase = MatchPhase.SECOND_PHASE
                currentMinute = 30
                currentTeamWithBall = "home"
                matchFunctions.simulateRandomPhase?.invoke("home")
            }

            MatchPhase.SECOND_PHASE -> {
                currentPhase = MatchPhase.THIRD_PHASE
                currentMinute = 45
                currentTeamWithBall = "home"
                matchFunctions.simulateRandomPhase?.invoke("home")
            }

            MatchPhase.THIRD_PHASE -> {
                currentPhase = MatchPhase.FOURTH_PHASE
                currentMinute = 60
                currentTeamWithBall = "home"
                matchFunctions.simulateRandomPhase?.invoke("home")
            }

            MatchPhase.FOURTH_PHASE -> {
                currentPhase = MatchPhase.FIFTH_PHASE
                currentMinute = 75
                currentTeamWithBall = "home"
                matchFunctions.simulateRandomPhase?.invoke("home")
            }

            MatchPhase.FIFTH_PHASE -> {
                currentPhase = MatchPhase.SIXTH_PHASE
                currentMinute = 90
                currentTeamWithBall = "home"
                matchFunctions.simulateRandomPhase?.invoke("home")
            }

            MatchPhase.SIXTH_PHASE -> {
                currentPhase = MatchPhase.MATCH_ENDED
                currentMinute = 95
                matchFunctions.endMatch?.invoke()
            }

            else -> {}
        }
    }

    matchFunctions.endMatch = {
        isMatchEnded = true
        isMatchInProgress = false

        fixture.value?.let { currentFixture ->
            scope.launch {
                // Maç sonucunu kaydet
                fixtureDao.updateFixture(
                    currentFixture.copy(
                        homeScore = homeScore,
                        awayScore = awayScore,
                        isPlayed = true
                    )
                )

                // Clean sheet kontrolü
                if (homeScore == 0) {
                    // Deplasman takımı gol yemedi - tüm ilk 11 için clean sheet
                    awayFirstEleven.forEach { player ->
                        playerStatsDao.createStatsIfNotExists(player.playerId, gameId)
                        playerStatsDao.updateCleanSheet(player.playerId, gameId)
                    }
                }

                if (awayScore == 0) {
                    // Ev sahibi takım gol yemedi - tüm ilk 11 için clean sheet
                    homeFirstEleven.forEach { player ->
                        playerStatsDao.createStatsIfNotExists(player.playerId, gameId)
                        playerStatsDao.updateCleanSheet(player.playerId, gameId)
                    }
                }

                // Lig tablosunu güncelle
                val leagueTableDao = database.leagueTableDao()
                leagueTableDao.updateAfterMatch(
                    leagueId = currentFixture.leagueId,
                    homeTeamId = currentFixture.homeTeamId,
                    awayTeamId = currentFixture.awayTeamId,
                    homeScore = homeScore,
                    awayScore = awayScore
                )

                delay(2000)
                onMatchEnd()
            }
        }
    }

    matchFunctions.simulateMatch = {
        if (!isMatchInProgress && !isMatchEnded) {
            isMatchInProgress = true
            scope.launch {
                // Maç başlangıcında tüm oyuncuların appearances sayısını artır
                incrementAppearances()

                // Maç başlangıç değerlerini ayarla
                homeScore = 0
                awayScore = 0
                currentPhase = MatchPhase.NOT_STARTED
                currentMinute = 0
                homeTeamPlayed = false
                awayTeamPlayed = false
                currentTeamWithBall = "home"

                // Diğer maçların simülasyonu
                fixture.value?.let { currentFixture ->
                    // Önce oyundaki tüm ligleri al
                    val allLeagues = database.leagueDao().getLeaguesByGameId(gameId).first()

                    // Her lig için aynı haftadaki maçları al ve oyna
                    allLeagues.forEach { league ->
                        // Fikstürleri al
                        val weekFixtures = fixtureDao.getFixturesByWeek(
                            leagueId = league.leagueId,
                            week = currentFixture.week
                        ).first()

                        // Bizim maçımız hariç diğerlerini filtrele ve işle
                        for (weekFixture in weekFixtures) {
                            if (weekFixture.fixtureId != currentFixture.fixtureId) {
                                scope.launch {
                                    val success = simulateOtherMatch(
                                        database = database,
                                        weekFixture = weekFixture,
                                        currentFixture = currentFixture,
                                        playerStatsDao = playerStatsDao,
                                        fixtureDao = fixtureDao
                                    )
                                    if (!success) {
                                        Log.e("PlayMatchScreen", "Failed to simulate match")
                                    }
                                }
                            }
                        }
                    }
                }

                // İlk faza geç
                matchFunctions.proceedToNextPhase?.invoke()
                matchFunctions.simulateFirstPhase?.invoke()
            }
        }
    }

    // UI
    Scaffold(
        topBar = {
            TopBar(
                title = "Match Day",
                onBackClick = onBackClick
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF4CAF50))
                .padding(paddingValues)
        ) {
            // Üst kısım (skor tablosu ve animasyonlar) - yüksekliği azaltıldı
            Box(
                modifier = Modifier
                    .weight(0.65f)
                    .fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp) // spacing azaltıldı
                ) {
                    // Hafta ve dakika bilgisi - font boyutu küçültüldü
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        fixture.value?.let { currentFixture ->
                            Text(
                                text = "Week ${currentFixture.week}",
                                color = Color.White,
                                fontSize = 20.sp, // küçültüldü
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Text(
                            text = "$currentMinute'",
                            color = Color.White,
                            fontSize = 20.sp, // küçültüldü
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Takımların en iyi 11'lerinin puanlarını hesapla
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1B5E20))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Ev sahibi takım puanları
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = homeTeam?.value?.name ?: "Home Team",
                                    color = Color.White,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = "Attack",
                                            color = Color.White,
                                            fontSize = 14.sp
                                        )
                                        Text(
                                            text = homeTeamStats.attackPoints.toString(),
                                            color = Color.White,
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = "Defense",
                                            color = Color.White,
                                            fontSize = 14.sp
                                        )
                                        Text(
                                            text = homeTeamStats.defensePoints.toString(),
                                            color = Color.White,
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }

                            // VS yazısı
                            Text(
                                text = "VS",
                                color = Color.White,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )

                            // Deplasman takım puanları
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = awayTeam?.value?.name ?: "Away Team",
                                    color = Color.White,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = "Attack",
                                            color = Color.White,
                                            fontSize = 14.sp
                                        )
                                        Text(
                                            text = awayTeamStats.attackPoints.toString(),
                                            color = Color.White,
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = "Defense",
                                            color = Color.White,
                                            fontSize = 14.sp
                                        )
                                        Text(
                                            text = awayTeamStats.defensePoints.toString(),
                                            color = Color.White,
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Maç kartı
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.SpaceEvenly
                        ) {
                            // Ev sahibi
                            Text(
                                text = homeTeam?.value?.name ?: "Home Team",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF4CAF50)
                            )

                            // Skor
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = homeScore.toString(),
                                    fontSize = 32.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF4CAF50)
                                )
                                Text(
                                    text = " - ",
                                    fontSize = 32.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF4CAF50),
                                    modifier = Modifier.padding(horizontal = 16.dp)
                                )
                                Text(
                                    text = awayScore.toString(),
                                    fontSize = 32.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF4CAF50)
                                )
                            }

                            // Deplasman
                            Text(
                                text = awayTeam?.value?.name ?: "Away Team",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF4CAF50)
                            )
                        }
                    }

                    // Oyna butonu
                    Button(
                        onClick = { matchFunctions.simulateMatch?.invoke() },
                        enabled = !isMatchEnded && !isMatchInProgress,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (!isMatchEnded && !isMatchInProgress)
                                Color(0xFF388E3C) else Color.Gray
                        )
                    ) {
                        Text(
                            text = when {
                                isMatchEnded -> "Match Ended"
                                isMatchInProgress -> "Match in Progress..."
                                else -> "Play Match"
                            },
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Animasyonlar burada kalacak
                if (showTeamAnimation) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        // Takım gol yazısı
                        Text(
                            text = when (currentTeamWithBall) {
                                "home" -> "${homeTeam?.value?.name} SCORES!!!"
                                "away" -> "${awayTeam?.value?.name} SCORES!!!"
                                else -> "GOAL!!!"
                            },
                            color = Color(0xFF388E3C), // Yeşil renk
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                                .offset(y = (LocalConfiguration.current.screenHeightDp * 0.75f).dp)
                                .alpha(teamTextAlpha)
                        )
                    }

                    if (showBallAnimation) {
                        // Top animasyonu
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_sports_soccer_24),
                            contentDescription = "Goal",
                            modifier = Modifier
                                .size(48.dp)
                                .offset(
                                    x = (LocalConfiguration.current.screenWidthDp * ballPosition).dp,
                                    y = (LocalConfiguration.current.screenHeightDp * 0.66f).dp
                                )
                                .rotate(ballRotation),
                            tint = Color(0xFF388E3C) // Yeşil renk
                        )
                    }
                }
            }

            // Alt kısım (olaylar listesi)
            Card(
                modifier = Modifier
                    .weight(0.35f)
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f))
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    reverseLayout = true
                ) {
                    items(items = matchEvents) { event ->
                        when (event.eventType) {
                            EventType.GOAL -> {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    horizontalArrangement = if (event.team == homeTeam?.value)
                                        Arrangement.Start else Arrangement.End
                                ) {
                                    if (event.team == awayTeam?.value) {
                                        Spacer(modifier = Modifier.weight(1f))
                                    }

                                    Column {
                                        Text(
                                            text = "${event.minute}' GOAL!",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp, // küçültüldü
                                            color = Color(0xFF4CAF50)
                                        )
                                        Text(
                                            text = event.player.name,
                                            fontSize = 11.sp // küçültüldü
                                        )
                                        event.assist?.let {
                                            Text(
                                                text = "Assist: ${it.name}",
                                                fontSize = 10.sp, // küçültüldü
                                                color = Color.Gray
                                            )
                                        }
                                    }

                                    if (event.team == homeTeam?.value) {
                                        Spacer(modifier = Modifier.weight(1f))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
} 