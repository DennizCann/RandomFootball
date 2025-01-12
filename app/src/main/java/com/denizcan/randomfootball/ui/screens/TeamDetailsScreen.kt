package com.denizcan.randomfootball.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
fun TeamDetailsScreen(
    teamId: Long,
    gameId: Long,
    onBackClick: () -> Unit,
    onTeamSelected: (Long) -> Unit
) {
    val context = LocalContext.current
    val database = remember { AppDatabase.getDatabase(context) }
    val playerDao = remember { database.playerDao() }
    val managerDao = remember { database.managerDao() }
    val players = playerDao.getPlayersByTeamId(teamId).collectAsState(initial = emptyList())
    val manager = managerDao.getManagerByTeamId(teamId).collectAsState(initial = null)

    // Pozisyonlara göre sıralama için sıra değerleri
    val positionOrderMap = mapOf(
        "Goalkeeper" to 1,
        "Defender" to 2,
        "Midfielder" to 3,
        "Forward" to 4
    )

    // Oyuncuları sırala
    val sortedPlayers = players.value.sortedWith(
        compareBy<Player> { positionOrderMap[it.position] ?: Int.MAX_VALUE }
            .thenByDescending { it.skill }
    )

    // Oyuncuları pozisyonlarına göre grupla
    val groupedPlayers = sortedPlayers.groupBy { it.position }

    // Pozisyonların sırası
    val positionOrder = listOf("Goalkeeper", "Defender", "Midfielder", "Forward")

    Scaffold(
        topBar = {
            TopBar(
                title = "Team Details",
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
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Menajer Kartı veya "Create Manager" Kartı
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onTeamSelected(teamId) },
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = if (manager.value != null) "Manager" else "Create Manager",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4CAF50)
                    )
                    
                    Divider(color = Color(0xFF4CAF50), thickness = 1.dp)
                    
                    manager.value?.let { currentManager ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = currentManager.name,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = currentManager.nationality,
                                    fontSize = 14.sp,
                                    color = Color.Gray
                                )
                            }
                            
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = Color(0xFF4CAF50)
                                ),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = currentManager.formation,
                                    modifier = Modifier.padding(6.dp),
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    } ?: run {
                        // Menajer yoksa "Tap to create manager" mesajı göster
                        Text(
                            text = "Tap to create manager",
                            fontSize = 14.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                }
            }

            // Oyuncu Listesi
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                positionOrder.forEach { position ->
                    groupedPlayers[position]?.let { playersInPosition ->
                        item {
                            Text(
                                text = position,
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }

                        items(
                            items = playersInPosition,
                            key = { it.playerId } // Önemli: Her oyuncu için benzersiz bir key kullan
                        ) { player ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = Color.White
                                )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        // Forma numarası
                                        Box(
                                            modifier = Modifier
                                                .size(32.dp)
                                                .background(Color(0xFF4CAF50), CircleShape),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = player.shirtNumber.toString(),
                                                color = Color.White,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                                
                                        // Oyuncu bilgileri
                                        Column {
                                            Text(
                                                text = player.name,
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text(
                                                text = player.nationality,
                                                fontSize = 14.sp,
                                                color = Color.Gray
                                            )
                                        }
                                    }
                                            
                                    // Yetenek puanı
                                    Text(
                                        text = player.skill.toString(),
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
    }
}
