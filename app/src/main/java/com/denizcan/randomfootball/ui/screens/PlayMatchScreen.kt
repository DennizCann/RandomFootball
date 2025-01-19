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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextOverflow
import com.denizcan.randomfootball.R
import com.denizcan.randomfootball.data.model.Player
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

// Maç fazlarını temsil eden enum
private enum class MatchPhase {
    NOT_STARTED,      // Maç başlamadı
    FIRST_PHASE,      // İlk faz (Hücum-Savunma karşılaştırması)
    SECOND_PHASE,     // 15. dakika
    THIRD_PHASE,      // 30. dakika
    FOURTH_PHASE,     // 45. dakika
    FIFTH_PHASE,      // 60. dakika
    SIXTH_PHASE,      // 75. dakika
    MATCH_ENDED       // 90. dakika - Maç bitti
}

// Takım istatistiklerini tutan data class
private data class TeamStats(
    val attackPoints: Int,
    val defensePoints: Int
)

// Takım puanlarını hesaplayan fonksiyonlar
private fun calculateTeamStats(players: List<Player>): TeamStats {
    // Hücum puanı (Orta saha + Forvet)
    val attackPoints = (players
        .filter { it.position == "Midfielder" || it.position == "Forward" }
        .sortedByDescending { it.skill }
        .take(6) // En iyi 6 hücum oyuncusu
        .sumOf { it.skill } * 0.8).toInt() // Hücum puanını %80'e düşür

    // Savunma puanı (Kaleci + Defans)
    val defensePoints = players
        .filter { it.position == "Goalkeeper" || it.position == "Defender" }
        .sortedByDescending { it.skill }
        .take(5) // En iyi 5 savunma oyuncusu
        .sumOf { it.skill }

    return TeamStats(attackPoints, defensePoints)
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

    // Database state'leri
    val fixture = fixtureDao.getFixtureById(fixtureId).collectAsState(initial = null)
    val homeTeam = remember(fixture.value?.homeTeamId) {
        fixture.value?.homeTeamId?.let { teamDao.getTeamById(it) }
    }?.collectAsState(initial = null)
    val awayTeam = remember(fixture.value?.awayTeamId) {
        fixture.value?.awayTeamId?.let { teamDao.getTeamById(it) }
    }?.collectAsState(initial = null)

    // Takımların oyuncularını al
    val homePlayers = remember(fixture.value?.homeTeamId) {
        fixture.value?.homeTeamId?.let { playerDao.getPlayersByTeamId(it) }
    }?.collectAsState(initial = emptyList())
    val awayPlayers = remember(fixture.value?.awayTeamId) {
        fixture.value?.awayTeamId?.let { playerDao.getPlayersByTeamId(it) }
    }?.collectAsState(initial = emptyList())

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

    // Fonksiyonları tanımla
    matchFunctions.simulateFirstPhase = {
        homePlayers?.value?.let { hPlayers ->
            awayPlayers?.value?.let { aPlayers ->
                val homeStats = calculateTeamStats(hPlayers)
                val awayStats = calculateTeamStats(aPlayers)

                when (currentTeamWithBall) {
                    "home" -> {
                        // Sadece hücum > savunma kontrolü
                        if (homeStats.attackPoints > awayStats.defensePoints) {
                            homeScore++
                            showBallAnimation = true
                        } else {
                            homeTeamPlayed = true
                            if (!awayTeamPlayed) {
                                currentTeamWithBall = "away"
                                scope.launch {
                                    delay(500)
                                    matchFunctions.simulateFirstPhase?.invoke()
                                }
                            }
                        }
                    }
                    "away" -> {
                        // Sadece hücum > savunma kontrolü
                        if (awayStats.attackPoints > homeStats.defensePoints) {
                            awayScore++
                            showBallAnimation = true
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
        if ((1..5).random() == 5) { // %20 şans (1/5)
            if (team == "home") homeScore++ else awayScore++
            currentTeamWithBall = team
            showBallAnimation = true
        } else {
            when (team) {
                "home" -> {
                    homeTeamPlayed = true
                    if (!awayTeamPlayed) {
                        currentTeamWithBall = "away"
                        scope.launch {
                            delay(500)
                            matchFunctions.simulateRandomPhase?.invoke("away")
                        }
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
                currentMinute = 0
            }
            MatchPhase.FIRST_PHASE -> {
                currentPhase = MatchPhase.SECOND_PHASE
                currentMinute = 15
                currentTeamWithBall = "home"
                matchFunctions.simulateRandomPhase?.invoke("home")
            }
            MatchPhase.SECOND_PHASE -> {
                currentPhase = MatchPhase.THIRD_PHASE
                currentMinute = 30
                currentTeamWithBall = "home"
                matchFunctions.simulateRandomPhase?.invoke("home")
            }
            MatchPhase.THIRD_PHASE -> {
                currentPhase = MatchPhase.FOURTH_PHASE
                currentMinute = 45
                currentTeamWithBall = "home"
                matchFunctions.simulateRandomPhase?.invoke("home")
            }
            MatchPhase.FOURTH_PHASE -> {
                currentPhase = MatchPhase.FIFTH_PHASE
                currentMinute = 60
                currentTeamWithBall = "home"
                matchFunctions.simulateRandomPhase?.invoke("home")
            }
            MatchPhase.FIFTH_PHASE -> {
                currentPhase = MatchPhase.SIXTH_PHASE
                currentMinute = 75
                currentTeamWithBall = "home"
                matchFunctions.simulateRandomPhase?.invoke("home")
            }
            MatchPhase.SIXTH_PHASE -> {
                currentPhase = MatchPhase.MATCH_ENDED
                currentMinute = 90
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
                fixtureDao.updateFixture(currentFixture.copy(
                    homeScore = homeScore,
                    awayScore = awayScore,
                    isPlayed = true
                ))

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
        isMatchInProgress = true
        homeScore = 0
        awayScore = 0
        currentPhase = MatchPhase.FIRST_PHASE
        currentMinute = 0
        homeTeamPlayed = false
        awayTeamPlayed = false
        currentTeamWithBall = "home"

        // Diğer maçların simülasyonu
        fixture.value?.let { currentFixture ->
            scope.launch {
                // Önce oyundaki tüm ligleri al
                val gameId = currentFixture.gameId
                val allLeagues = database.leagueDao().getLeaguesByGameId(gameId).first()

                // Her lig için aynı haftadaki maçları al ve oyna
                allLeagues.forEach { league ->
                    val allFixtures = fixtureDao.getFixturesByWeek(
                        leagueId = league.leagueId,
                        week = currentFixture.week
                    )

                    // Her maç için simülasyon yap (bizim maçımız hariç)
                    allFixtures
                        .filter { it.fixtureId != currentFixture.fixtureId }
                        .forEach { weekFixture ->
                            val homeTeamPlayers = playerDao.getPlayersByTeamId(weekFixture.homeTeamId).first()
                            val awayTeamPlayers = playerDao.getPlayersByTeamId(weekFixture.awayTeamId).first()

                            val homeStats = calculateTeamStats(homeTeamPlayers)
                            val awayStats = calculateTeamStats(awayTeamPlayers)

                            var homeGoals = 0
                            var awayGoals = 0

                            // İlk faz - Sadece puanlar
                            if (homeStats.attackPoints > awayStats.defensePoints) {
                                homeGoals++
                            }
                            if (awayStats.attackPoints > homeStats.defensePoints) {
                                awayGoals++
                            }

                            // Diğer 5 faz - Sadece %20 şans
                            repeat(5) {
                                if ((1..5).random() == 5) homeGoals++
                                if ((1..5).random() == 5) awayGoals++
                            }

                            // Maç sonucunu kaydet
                            fixtureDao.updateFixture(weekFixture.copy(
                                homeScore = homeGoals,
                                awayScore = awayGoals,
                                isPlayed = true
                            ))

                            // Lig tablosunu güncelle
                            val leagueTableDao = database.leagueTableDao()
                            leagueTableDao.updateAfterMatch(
                                leagueId = weekFixture.leagueId,
                                homeTeamId = weekFixture.homeTeamId,
                                awayTeamId = weekFixture.awayTeamId,
                                homeScore = homeGoals,
                                awayScore = awayGoals
                            )
                        }
                }
            }
        }

        // Görünen maç için animasyonlu simülasyonu başlat
        matchFunctions.simulateFirstPhase?.invoke()
    }

    // Takımların en iyi 11'lerinin puanlarını hesapla
    val homeTeamStats = remember(homePlayers?.value) {
        homePlayers?.value?.let { players ->
            calculateTeamStats(players)
        } ?: TeamStats(0, 0)
    }

    val awayTeamStats = remember(awayPlayers?.value) {
        awayPlayers?.value?.let { players ->
            calculateTeamStats(players)
        } ?: TeamStats(0, 0)
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
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF4CAF50))
                    .padding(paddingValues)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Hafta ve dakika bilgisi
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    fixture.value?.let { currentFixture ->
                        Text(
                            text = "Week ${currentFixture.week}",
                            color = Color.White,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    
                    Text(
                        text = "$currentMinute'",
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Takımların en iyi 11'lerinin puanlarını hesapla
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
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

            // Animasyonlar
            if (showBallAnimation || showTeamAnimation) {
                Box(modifier = Modifier.fillMaxSize()) {
                    if (showTeamAnimation) {
                        // Takım gol yazısı
                        Text(
                            text = when (currentTeamWithBall) {
                                "home" -> "${homeTeam?.value?.name} SCORES!!!"
                                "away" -> "${awayTeam?.value?.name} SCORES!!!"
                                else -> "GOAL!!!"
                            },
                            color = Color.White,
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
                            tint = Color.White
                        )
                    }
                }
            }
        }
    }
} 