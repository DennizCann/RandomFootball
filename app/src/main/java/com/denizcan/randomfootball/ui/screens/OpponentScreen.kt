package com.denizcan.randomfootball.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import com.denizcan.randomfootball.data.model.Player
import com.denizcan.randomfootball.ui.components.TopBar


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OpponentScreen(
    teamId: Long,
    opponentId: Long,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val database = remember { AppDatabase.getDatabase(context) }
    val teamDao = remember { database.teamDao() }
    val managerDao = remember { database.managerDao() }
    val playerDao = remember { database.playerDao() }

    val team = teamDao.getTeamById(opponentId).collectAsState(initial = null)
    val teamColors = remember(team.value) {
        team.value?.let {
            Color(android.graphics.Color.parseColor(it.primaryColor)) to
            Color(android.graphics.Color.parseColor(it.secondaryColor))
        } ?: (Color.White to Color.Black)
    }
    val manager = managerDao.getManagerByTeamId(opponentId).collectAsState(initial = null)
    val players = playerDao.getPlayersByTeamId(opponentId).collectAsState(initial = emptyList())

    // En iyi 11'i belirle
    val bestEleven = remember(players.value, manager.value?.formation) {
        manager.value?.let { currentManager ->
            val formationRows = currentManager.formation.split("-").map { it.toInt() }
            
            buildList {
                // En iyi kaleci
                players.value
                    .filter { it.position == "Goalkeeper" }
                    .sortedWith(
                        compareByDescending<Player> { it.skill }
                            .thenBy { it.shirtNumber }
                    )
                    .firstOrNull()?.let { add(it) }

                // En iyi defanslar
                val defenders = players.value
                    .filter { it.position == "Defender" }
                    .sortedWith(
                        compareByDescending<Player> { it.skill }
                            .thenBy { it.shirtNumber }
                    )
                    .take(formationRows[0].coerceAtMost(
                        players.value.count { it.position == "Defender" }
                    ))
                addAll(defenders)

                // En iyi orta sahalar
                val midfielders = players.value
                    .filter { it.position == "Midfielder" }
                    .sortedWith(
                        compareByDescending<Player> { it.skill }
                            .thenBy { it.shirtNumber }
                    )
                    .take(formationRows[1].coerceAtMost(
                        players.value.count { it.position == "Midfielder" }
                    ))
                addAll(midfielders)

                // En iyi forvetler
                val forwards = players.value
                    .filter { it.position == "Forward" }
                    .sortedWith(
                        compareByDescending<Player> { it.skill }
                            .thenBy { it.shirtNumber }
                    )
                    .take(formationRows[2].coerceAtMost(
                        players.value.count { it.position == "Forward" }
                    ))
                addAll(forwards)
            }
        } ?: emptyList()
    }

    Scaffold(
        topBar = {
            TopBar(
                title = team.value?.name ?: "Opponent",
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Taktik tahtası
            manager.value?.let { currentManager ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1B5E20))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .border(2.dp, Color.White.copy(alpha = 0.7f))
                            .padding(2.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            val formationRows = currentManager.formation.split("-").map { it.toInt() }
                            val allRows = listOf(1) + formationRows

                            allRows.forEachIndexed { index, playerCount ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceEvenly,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    val positionPlayers = when(index) {
                                        0 -> bestEleven.filter { it.position == "Goalkeeper" }
                                        1 -> bestEleven.filter { it.position == "Defender" }
                                        2 -> bestEleven.filter { it.position == "Midfielder" }
                                        3 -> bestEleven.filter { it.position == "Forward" }
                                        else -> emptyList()
                                    }

                                    positionPlayers.forEach { player ->
                                        Box(
                                            modifier = Modifier
                                                .size(32.dp)
                                                .background(teamColors.first, CircleShape)
                                                .border(1.dp, teamColors.second, CircleShape),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = player.shirtNumber.toString(),
                                                color = teamColors.second,
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Oyuncu listesi
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(players.value) { player ->
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
                            Column {
                                Text(
                                    text = player.name,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "${player.position} - ${player.nationality}",
                                    fontSize = 14.sp,
                                    color = Color.Gray
                                )
                            }
                            
                            Text(
                                text = "#${player.shirtNumber}",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF4CAF50)
                            )
                        }
                    }
                }
            }
        }
    }
}