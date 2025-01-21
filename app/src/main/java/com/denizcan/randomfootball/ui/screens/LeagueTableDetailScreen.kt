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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.denizcan.randomfootball.data.AppDatabase
import com.denizcan.randomfootball.ui.components.TopBar

data class TeamTableStats(
    val teamId: Long,
    val teamName: String,
    val position: Int,
    val played: Int,
    val won: Int,
    val drawn: Int,
    val lost: Int,
    val goalsFor: Int,
    val goalsAgainst: Int,
    val goalDifference: Int,
    val points: Int
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeagueTableDetailScreen(
    leagueId: Long,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val database = remember { AppDatabase.getDatabase(context) }
    val leagueDao = remember { database.leagueDao() }
    val teamDao = remember { database.teamDao() }
    val leagueTableDao = remember { database.leagueTableDao() }

    val league = leagueDao.getLeagueById(leagueId).collectAsState(initial = null)
    val leagueTable = leagueTableDao.getLeagueTableByLeagueId(leagueId).collectAsState(initial = emptyList())
    val teams = teamDao.getTeamsByLeagueId(leagueId).collectAsState(initial = emptyList())

    // Takım istatistiklerini birleştir ve sırala
    val teamStats = remember(leagueTable.value, teams.value) {
        leagueTable.value.mapNotNull { tableEntry ->
            teams.value.find { it.teamId == tableEntry.teamId }?.let { team ->
                TeamTableStats(
                    teamId = team.teamId,
                    teamName = team.name,
                    position = tableEntry.position,
                    played = tableEntry.played,
                    won = tableEntry.won,
                    drawn = tableEntry.drawn,
                    lost = tableEntry.lost,
                    goalsFor = tableEntry.goalsFor,
                    goalsAgainst = tableEntry.goalsAgainst,
                    goalDifference = tableEntry.goalDifference,
                    points = tableEntry.points
                )
            }
        }.sortedWith(
            compareByDescending<TeamTableStats> { it.points }
                .thenByDescending { it.goalDifference }
                .thenByDescending { it.goalsFor }
                .thenBy { it.teamName }
        )
    }

    Scaffold(
        topBar = {
            TopBar(
                title = league.value?.name ?: "League Table",
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
            // Tablo başlıkları
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF388E3C))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Pos",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.width(30.dp),
                    textAlign = TextAlign.Center,
                    fontSize = 12.sp
                )
                Text(
                    text = "Team",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.width(120.dp),
                    textAlign = TextAlign.Left,
                    fontSize = 12.sp,
                    maxLines = 1
                )
                Text(
                    text = "P",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.width(25.dp),
                    textAlign = TextAlign.Center,
                    fontSize = 12.sp
                )
                Text(
                    text = "W",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.width(25.dp),
                    textAlign = TextAlign.Center,
                    fontSize = 12.sp
                )
                Text(
                    text = "D",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.width(25.dp),
                    textAlign = TextAlign.Center,
                    fontSize = 12.sp
                )
                Text(
                    text = "L",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.width(25.dp),
                    textAlign = TextAlign.Center,
                    fontSize = 12.sp
                )
                Text(
                    text = "GF",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.width(25.dp),
                    textAlign = TextAlign.Center,
                    fontSize = 12.sp
                )
                Text(
                    text = "GA",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.width(25.dp),
                    textAlign = TextAlign.Center,
                    fontSize = 12.sp
                )
                Text(
                    text = "GD",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.width(25.dp),
                    textAlign = TextAlign.Center,
                    fontSize = 12.sp
                )
                Text(
                    text = "Pts",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.width(25.dp),
                    textAlign = TextAlign.Center,
                    fontSize = 12.sp
                )
            }

            // Takım listesi
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(teamStats) { stats ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp, vertical = 2.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stats.position.toString(),
                                modifier = Modifier.width(30.dp),
                                textAlign = TextAlign.Center,
                                fontSize = 12.sp
                            )
                            Text(
                                text = stats.teamName,
                                modifier = Modifier.width(120.dp),
                                textAlign = TextAlign.Left,
                                fontSize = 12.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = stats.played.toString(),
                                modifier = Modifier.width(25.dp),
                                textAlign = TextAlign.Center,
                                fontSize = 12.sp
                            )
                            Text(
                                text = stats.won.toString(),
                                modifier = Modifier.width(25.dp),
                                textAlign = TextAlign.Center,
                                fontSize = 12.sp
                            )
                            Text(
                                text = stats.drawn.toString(),
                                modifier = Modifier.width(25.dp),
                                textAlign = TextAlign.Center,
                                fontSize = 12.sp
                            )
                            Text(
                                text = stats.lost.toString(),
                                modifier = Modifier.width(25.dp),
                                textAlign = TextAlign.Center,
                                fontSize = 12.sp
                            )
                            Text(
                                text = stats.goalsFor.toString(),
                                modifier = Modifier.width(25.dp),
                                textAlign = TextAlign.Center,
                                fontSize = 12.sp
                            )
                            Text(
                                text = stats.goalsAgainst.toString(),
                                modifier = Modifier.width(25.dp),
                                textAlign = TextAlign.Center,
                                fontSize = 12.sp
                            )
                            Text(
                                text = stats.goalDifference.toString(),
                                modifier = Modifier.width(25.dp),
                                textAlign = TextAlign.Center,
                                fontSize = 12.sp
                            )
                            Text(
                                text = stats.points.toString(),
                                modifier = Modifier.width(25.dp),
                                textAlign = TextAlign.Center,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }
    }
} 