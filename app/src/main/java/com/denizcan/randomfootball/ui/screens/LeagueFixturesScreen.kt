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
import android.util.Log

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
    val league = leagueDao.getLeagueById(leagueId).collectAsState(initial = null)

    // Takımları al
    val teams = teamDao.getTeamsByLeagueId(leagueId).collectAsState(initial = emptyList())
    val teamNames = teams.value.associate { it.teamId to it.name }

    // Fikstürleri al - gameId'yi league'den alıyoruz
    val fixtures = remember(league.value?.gameId) {
        league.value?.let { currentLeague ->
            fixtureDao.getFixturesByLeague(currentLeague.gameId, leagueId)
        } ?: flowOf(emptyList())
    }.collectAsState(initial = emptyList())

    // Hafta gruplarını oluştur
    val weekGroups = fixtures.value.groupBy { it.week }.toSortedMap()

    // Debug logları
    LaunchedEffect(leagueId, league.value, teams.value, fixtures.value) {
        Log.d("LeagueFixtures", """
            LeagueID: $leagueId
            League: ${league.value}
            GameID: ${league.value?.gameId}
            Teams Count: ${teams.value.size}
            Teams: ${teams.value.map { it.name }}
            Fixtures Count: ${fixtures.value.size}
            Fixtures: ${fixtures.value}
        """.trimIndent())
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
                weekGroups.forEach { (week, weekFixtures) ->
                    item(key = "week_$week") {
                        Text(
                            text = "Week $week",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }

                    items(
                        items = weekFixtures,
                        key = { it.fixtureId }  // Benzersiz key kullan
                    ) { fixture ->
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
} 