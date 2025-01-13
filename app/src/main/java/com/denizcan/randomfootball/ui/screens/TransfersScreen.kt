package com.denizcan.randomfootball.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
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
import com.denizcan.randomfootball.util.toComposeColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransfersScreen(
    teamId: Long,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val database = remember { AppDatabase.getDatabase(context) }
    val playerDao = remember { database.playerDao() }
    val teamDao = remember { database.teamDao() }
    
    val players = playerDao.getPlayersByTeamId(teamId).collectAsState(initial = emptyList())
    val team = teamDao.getTeamById(teamId).collectAsState(initial = null)

    Scaffold(
        topBar = {
            TopBar(
                title = "Transfers",
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
                // Pozisyonlara göre sıralama
                val positionOrder = mapOf(
                    "Goalkeeper" to 1,
                    "Defender" to 2,
                    "Midfielder" to 3,
                    "Forward" to 4
                )

                // Oyuncuları pozisyona ve forma numarasına göre sırala
                val sortedPlayers = players.value.sortedWith(
                    compareBy<Player> { positionOrder[it.position] ?: 0 }
                        .thenBy { it.shirtNumber }
                )

                items(sortedPlayers) { player ->
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
                            // Forma Numarası
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(
                                        team.value?.primaryColor?.toComposeColor() ?: Color.Gray,
                                        CircleShape
                                    )
                                    .border(
                                        1.dp,
                                        team.value?.secondaryColor?.toComposeColor() ?: Color.DarkGray,
                                        CircleShape
                                    ),
                                contentAlignment = Alignment.Center
        ) {
            Text(
                                    text = player.shirtNumber.toString(),
                                    color = team.value?.secondaryColor?.toComposeColor() ?: Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            // İsim
                            Text(
                                text = player.name,
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(horizontal = 12.dp),
                                fontWeight = FontWeight.Bold
                            )

                            // Mevki
                            Text(
                                text = player.position,
                                color = Color.Gray,
                                fontSize = 12.sp,
                                modifier = Modifier.width(70.dp)
                            )

                            // Yetenek
                            Text(
                                text = player.skill.toString(),
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