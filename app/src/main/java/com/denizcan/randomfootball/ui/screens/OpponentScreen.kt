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
import com.denizcan.randomfootball.util.TeamUtils
import com.denizcan.randomfootball.util.TacticalBoard


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OpponentScreen(
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
            TeamUtils.getBestEleven(players.value, currentManager.formation)
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
                TacticalBoard(
                    players = bestEleven,
                    formation = currentManager.formation,
                    primaryColor = teamColors.first,
                    secondaryColor = teamColors.second,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                )
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