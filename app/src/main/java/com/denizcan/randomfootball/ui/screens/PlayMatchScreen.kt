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

// Maç simülasyonu yardımcı fonksiyonları
private fun calculateAttackPoints(players: List<Player>): Int {
    return players
        .filter { it.position == "Midfielder" || it.position == "Forward" }
        .sumOf { it.skill }
}

private fun calculateDefensePoints(players: List<Player>): Int {
    return players
        .filter { it.position == "Goalkeeper" || it.position == "Defender" }
        .sumOf { it.skill }
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

    // Skor ve animasyon state'leri
    var homeScore by remember { mutableStateOf(0) }
    var awayScore by remember { mutableStateOf(0) }
    var scoringTeam by remember { mutableStateOf<String?>(null) }
    var showGoalAnimation by remember { mutableStateOf(false) }

    // Maç durumu için state'ler
    var matchPhase by remember { mutableStateOf(0) } // 0: Başlamadı, 1: İlk tur, 2-4: Random turlar, 5: Bitti
    var isMatchEnded by remember { mutableStateOf(false) }
    var currentTeamScoring by remember { mutableStateOf<String?>(null) } // Şu an hangi takım oynuyor
    var showBallAnimation by remember { mutableStateOf(false) }
    var showTeamAnimation by remember { mutableStateOf(false) }

    // State'leri tanımladığımız yere ekleyelim
    var isMatchInProgress by remember { mutableStateOf(false) }

    // Tüm fonksiyonları remember içinde tanımlayalım (en üste taşındı)
    val functions = remember {
        object {
            var checkRandomGoal: ((String) -> Unit)? = null
            var checkAwayTeamGoal: (() -> Unit)? = null
            var checkHomeTeamGoal: (() -> Unit)? = null
            var showMatchEndAnimation: (() -> Unit)? = null
            var simulateMatch: (() -> Unit)? = null
        }
    }

    // Animasyon state'leri
    val ballPosition by animateFloatAsState(
        targetValue = if (showBallAnimation) 1f else 0f,
        animationSpec = tween(1000),
        finishedListener = {
            if (showBallAnimation) {
                showBallAnimation = false
                showTeamAnimation = true // Top animasyonu bitince takım animasyonu başlasın
            }
        }
    )

    val teamTextAlpha by animateFloatAsState(
        targetValue = if (showTeamAnimation) 1f else 0f,
        animationSpec = tween(500),
        finishedListener = {
            if (showTeamAnimation) {
                showTeamAnimation = false
                when (matchPhase) {
                    1 -> {
                        if (currentTeamScoring == "home") {
                            currentTeamScoring = "away"
                            functions.checkAwayTeamGoal?.invoke()
                        } else {
                            matchPhase = 2
                            currentTeamScoring = "home"
                            functions.checkRandomGoal?.invoke("home")
                        }
                    }
                    2, 3, 4 -> {
                        if (currentTeamScoring == "home") {
                            currentTeamScoring = "away"
                            functions.checkRandomGoal?.invoke("away")
                        } else {
                            matchPhase++
                            if (matchPhase <= 4) {
                                currentTeamScoring = "home"
                                functions.checkRandomGoal?.invoke("home")
                            } else {
                                isMatchEnded = true
                                functions.showMatchEndAnimation?.invoke()
                            }
                        }
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
    functions.checkRandomGoal = { team: String ->
        if ((0..1).random() == 1) {
            if (team == "home") homeScore++ else awayScore++
            currentTeamScoring = team
            showBallAnimation = true
        } else {
            if (team == "home") {
                currentTeamScoring = "away"
                functions.checkRandomGoal?.invoke("away")
            } else {
                matchPhase++
                if (matchPhase <= 4) {
                    currentTeamScoring = "home"
                    functions.checkRandomGoal?.invoke("home")
                } else {
                    isMatchEnded = true
                    functions.showMatchEndAnimation?.invoke()
                }
            }
        }
    }

    functions.checkAwayTeamGoal = {
        homePlayers?.value?.let { hPlayers ->
            awayPlayers?.value?.let { aPlayers ->
                val awayAttackPoints = calculateAttackPoints(aPlayers)
                val homeDefensePoints = calculateDefensePoints(hPlayers)

                if (awayAttackPoints > homeDefensePoints) {
                    awayScore++
                    currentTeamScoring = "away"
                    showBallAnimation = true
                } else {
                    matchPhase = 2
                    currentTeamScoring = "home"
                    functions.checkRandomGoal?.invoke("home")
                }
            }
        }
    }

    functions.checkHomeTeamGoal = {
        homePlayers?.value?.let { hPlayers ->
            awayPlayers?.value?.let { aPlayers ->
                val homeAttackPoints = calculateAttackPoints(hPlayers)
                val awayDefensePoints = calculateDefensePoints(aPlayers)

                if (homeAttackPoints > awayDefensePoints) {
                    homeScore++
                    currentTeamScoring = "home"
                    showBallAnimation = true
                } else {
                    currentTeamScoring = "away"
                    functions.checkAwayTeamGoal?.invoke()
                }
            }
        }
    }

    functions.showMatchEndAnimation = {
        isMatchEnded = true
        isMatchInProgress = false  // Maç bitti
        
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

                // Aynı haftadaki diğer maçları al
                val allFixtures = fixtureDao.getFixturesByWeek(
                    leagueId = currentFixture.leagueId,
                    week = currentFixture.week
                )

                // Diğer maçların sonuçlarını da kaydet ve tablolarını güncelle
                allFixtures
                    .filter { it.fixtureId != currentFixture.fixtureId }
                    .forEach { fixture ->
                        leagueTableDao.updateAfterMatch(
                            leagueId = fixture.leagueId,
                            homeTeamId = fixture.homeTeamId,
                            awayTeamId = fixture.awayTeamId,
                            homeScore = fixture.homeScore,
                            awayScore = fixture.awayScore
                        )
                    }

                delay(2000)
                onMatchEnd()
            }
        }
    }

    functions.simulateMatch = {
        isMatchInProgress = true  // Maç başladı
        homeScore = 0
        awayScore = 0
        matchPhase = 1
        currentTeamScoring = "home"

        // Aynı haftadaki tüm maçları al
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
                        .filter { it.fixtureId != currentFixture.fixtureId }  // Bizim maçı filtrele
                        .forEach { weekFixture ->
                            // Takımların oyuncularını al
                            val homeTeamPlayers = playerDao.getPlayersByTeamId(weekFixture.homeTeamId).first()
                            val awayTeamPlayers = playerDao.getPlayersByTeamId(weekFixture.awayTeamId).first()

                            // Puanları hesapla
                            val homeAttackPoints = calculateAttackPoints(homeTeamPlayers)
                            val homeDefensePoints = calculateDefensePoints(homeTeamPlayers)
                            val awayAttackPoints = calculateAttackPoints(awayTeamPlayers)
                            val awayDefensePoints = calculateDefensePoints(awayTeamPlayers)

                            // Skorları hesapla
                            var homeGoals = 0
                            var awayGoals = 0

                            // İlk tur (puanlara göre)
                            if (homeAttackPoints > awayDefensePoints) homeGoals++
                            if (awayAttackPoints > homeDefensePoints) awayGoals++

                            // Random turlar (3 tur)
                            repeat(3) {
                                if ((0..1).random() == 1) homeGoals++
                                if ((0..1).random() == 1) awayGoals++
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
        functions.checkHomeTeamGoal?.invoke()
    }

    // UI kısmı
    Scaffold(
        topBar = {
            TopBar(
                title = "Match Day",
                onBackClick = onBackClick
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF4CAF50))
                    .padding(paddingValues)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Hafta bilgisi
                fixture.value?.let { currentFixture ->
                    Text(
                        text = "Week ${currentFixture.week}",
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
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
                    onClick = { functions.simulateMatch?.invoke() },
                    enabled = !isMatchEnded && !isMatchInProgress,  // İki durumda da disable
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (!isMatchEnded && !isMatchInProgress) Color(0xFF388E3C) else Color.Gray
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
                    if (showBallAnimation) {
                        // Top animasyonu
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_sports_soccer_24),
                            contentDescription = "Goal",
                            modifier = Modifier
                                .size(48.dp)
                                .offset(
                                    x = (LocalConfiguration.current.screenWidthDp * ballPosition).dp,
                                    y = (LocalConfiguration.current.screenHeightDp * 0.66f).dp  // 2/3 oranında aşağıda
                                )
                                .rotate(ballRotation),
                            tint = Color.White
                        )
                    }
                    
                    if (showTeamAnimation) {
                        // Takım gol yazısı
                        Text(
                            text = when (currentTeamScoring) {
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
                                .offset(y = (LocalConfiguration.current.screenHeightDp * 0.55f).dp)
                                .alpha(teamTextAlpha)
                        )
                    }
                }
            }
        }
    }
} 