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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerStatisticsScreen(
    teamId: Long,
    gameId: Long,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val database = remember { AppDatabase.getDatabase(context) }
    val playerStatsDao = remember { database.playerStatsDao() }
    val teamDao = remember { database.teamDao() }

    val team = teamDao.getTeamById(teamId).collectAsState(initial = null)
    val playersWithStats = playerStatsDao.getPlayerStatsWithPlayers(teamId, gameId)
        .collectAsState(initial = emptyList())

    Scaffold(
        topBar = {
            TopBar(
                title = team.value?.name ?: "Player Statistics",
                onBackClick = onBackClick
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF4CAF50))
                .padding(paddingValues)
                .padding(horizontal = 8.dp)
        ) {
            // Tablo başlıkları
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF388E3C))
                    .padding(vertical = 12.dp, horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Player",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(2f)
                )
                Text(
                    text = "M",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(0.5f),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "G",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(0.5f),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "A",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(0.5f),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "CS",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(0.5f),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Cards",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
            }

            // Oyuncu listesi
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(playersWithStats.value) { playerWithStats ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp, horizontal = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Oyuncu adı ve mevkisi
                            Column(
                                modifier = Modifier.weight(2f)
                            ) {
                                Text(
                                    text = playerWithStats.player.name,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = playerWithStats.player.position,
                                    color = Color.Gray,
                                    fontSize = 12.sp
                                )
                            }

                            // Maç sayısı
                            Text(
                                text = (playerWithStats.stats?.appearances ?: 0).toString(),
                                modifier = Modifier.weight(0.5f),
                                textAlign = TextAlign.Center
                            )

                            // Gol sayısı
                            Text(
                                text = (playerWithStats.stats?.goals ?: 0).toString(),
                                modifier = Modifier.weight(0.5f),
                                textAlign = TextAlign.Center
                            )

                            // Asist sayısı
                            Text(
                                text = (playerWithStats.stats?.assists ?: 0).toString(),
                                modifier = Modifier.weight(0.5f),
                                textAlign = TextAlign.Center
                            )

                            // Clean sheet sayısı
                            Text(
                                text = (playerWithStats.stats?.cleanSheets ?: 0).toString(),
                                modifier = Modifier.weight(0.5f),
                                textAlign = TextAlign.Center
                            )

                            // Kartlar
                            Row(
                                modifier = Modifier.weight(1f),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                // Sarı kart
                                Text(
                                    text = (playerWithStats.stats?.yellowCards ?: 0).toString(),
                                    fontSize = 12.sp,
                                    color = Color(0xFFFFB300) // Koyu sarı
                                )
                                Text(
                                    text = "/", // Ayırıcı
                                    fontSize = 12.sp,
                                    color = Color.Gray
                                )
                                // Kırmızı kart
                                Text(
                                    text = (playerWithStats.stats?.redCards ?: 0).toString(),
                                    fontSize = 12.sp,
                                    color = Color(0xFFD32F2F) // Koyu kırmızı
                                )
                            }
                        }
                    }
                }
            }
        }
    }
} 