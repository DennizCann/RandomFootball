package com.denizcan.randomfootball.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
fun DashboardScreen(
    teamId: Long,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val database = remember { AppDatabase.getDatabase(context) }
    val teamDao = remember { database.teamDao() }
    val managerDao = remember { database.managerDao() }
    val playerDao = remember { database.playerDao() }
    
    val team = teamDao.getTeamById(teamId).collectAsState(initial = null)
    val manager = managerDao.getManagerByTeamId(teamId).collectAsState(initial = null)
    val players = playerDao.getPlayersByTeamId(teamId).collectAsState(initial = emptyList())

    Scaffold(
        topBar = {
            TopBar(
                title = "Dashboard",
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
            // Takım Kartı
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = team.value?.name ?: "",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4CAF50)
                    )
                    
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        team.value?.let { currentTeam ->
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(currentTeam.primaryColor.toComposeColor())
                                    .border(2.dp, Color.Black, CircleShape)
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(currentTeam.secondaryColor.toComposeColor())
                                    .border(2.dp, Color.Black, CircleShape)
                            )
                        }
                    }
                }
            }

            // Menajer Kartı
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Manager",
                        fontSize = 20.sp,
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
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = currentManager.nationality,
                                    fontSize = 16.sp,
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
                                    modifier = Modifier.padding(8.dp),
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
            
            // Taktik Tahtası Kartı
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Tactical Board",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4CAF50)
                    )
                    
                    Divider(color = Color(0xFF4CAF50), thickness = 1.dp)
                    
                    team.value?.let { currentTeam ->
                        manager.value?.let { currentManager ->
                            TacticalBoard(
                                players = players.value,
                                formation = currentManager.formation,
                                primaryColor = currentTeam.primaryColor.toComposeColor(),
                                secondaryColor = currentTeam.secondaryColor.toComposeColor(),
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                    }
                }
            }
            
            // İlerleyen zamanlarda eklenecek diğer kartlar için boşluk
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Composable
fun TacticalBoard(
    players: List<Player>,
    formation: String,
    primaryColor: Color,
    secondaryColor: Color,
    modifier: Modifier = Modifier
) {
    val formationRows = formation.split("-").map { it.toInt() }
    val allRows = listOf(1) + formationRows // Kaleci + diğer mevkiler
    
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFF1B5E20))
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Her satır için (Kaleci, Defans, Orta Saha, Forvet)
        allRows.forEachIndexed { index, playerCount ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // O satırdaki oyuncular
                val positionPlayers = when(index) {
                    0 -> players.filter { it.position == "Goalkeeper" }
                    1 -> players.filter { it.position == "Defender" }.take(playerCount)
                    2 -> players.filter { it.position == "Midfielder" }.take(playerCount)
                    3 -> players.filter { it.position == "Forward" }.take(playerCount)
                    else -> emptyList()
                }

                positionPlayers.forEach { player ->
                    PlayerCircle(
                        number = player.shirtNumber,
                        primaryColor = primaryColor,
                        secondaryColor = secondaryColor
                    )
                }
            }
        }
    }
}

@Composable
fun PlayerCircle(
    number: Int,
    primaryColor: Color,
    secondaryColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(32.dp)
            .background(primaryColor, CircleShape)
            .border(1.dp, secondaryColor, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = number.toString(),
            color = secondaryColor,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
