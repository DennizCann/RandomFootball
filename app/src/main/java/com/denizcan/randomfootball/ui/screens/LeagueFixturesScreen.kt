package com.denizcan.randomfootball.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import kotlinx.coroutines.flow.flowOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeagueFixturesScreen(
    leagueId: Long,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val database = remember { AppDatabase.getDatabase(context) }
    val fixtureDao = remember { database.fixtureDao() }
    val teamDao = remember { database.teamDao() }
    val leagueDao = remember { database.leagueDao() }

    // Lig bilgisini al
    val league = remember(leagueId) {
        leagueDao.getLeagueById(leagueId)
    }.collectAsState(initial = null)

    // Fikstürü al
    val fixtures = remember(leagueId) {
        league.value?.let {
            fixtureDao.getFixturesByLeague(it.gameId, leagueId)
        } ?: flowOf(emptyList())
    }.collectAsState(initial = emptyList())

    // Takım isimlerini al
    val teams = remember(leagueId) {
        teamDao.getTeamsByLeagueId(leagueId)
    }.collectAsState(initial = emptyList())

    val teamNames = teams.value.associate { it.teamId to it.name }

    // Verileri kontrol etmek için log ekleyelim
    LaunchedEffect(leagueId) {
        println("LeagueID: $leagueId")
        println("League: ${league.value}")
        
        league.value?.let { currentLeague ->
            println("GameID: ${currentLeague.gameId}")
            val fixtureCount = fixtureDao.getFixtureCountForLeague(leagueId)
            println("Fixture Count in DB: $fixtureCount")
        }
        
        println("Teams Count: ${teams.value.size}")
        println("Teams: ${teams.value.map { it.name }}")
        println("Fixtures Count: ${fixtures.value.size}")
        println("Fixtures: ${fixtures.value}")
    }

    Scaffold(
        topBar = {
            TopBar(
                title = league.value?.name ?: "Fixtures",
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
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                var currentWeek = -1

                items(fixtures.value) { fixture ->
                    if (currentWeek != fixture.week) {
                        currentWeek = fixture.week
                        // Hafta başlığı
                        Text(
                            text = "Week ${fixture.week}",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Ev sahibi
                            Text(
                                text = teamNames[fixture.homeTeamId] ?: "",
                                modifier = Modifier.weight(1f),
                                textAlign = TextAlign.End
                            )
                            
                            // Skor
                            Row(
                                modifier = Modifier
                                    .padding(horizontal = 16.dp)
                                    .width(60.dp),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = if (fixture.isPlayed) fixture.homeScore.toString() else "-",
                                    fontWeight = FontWeight.Bold
                                )
                                Text(text = " - ")
                                Text(
                                    text = if (fixture.isPlayed) fixture.awayScore.toString() else "-",
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            
                            // Deplasman
                            Text(
                                text = teamNames[fixture.awayTeamId] ?: "",
                                modifier = Modifier.weight(1f),
                                textAlign = TextAlign.Start
                            )
                        }
                    }
                }
            }
        }
    }
} 