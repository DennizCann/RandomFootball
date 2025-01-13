package com.denizcan.randomfootball.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeamManagementScreen(
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
                title = "Team Management",
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
            team.value?.let { currentTeam ->
                manager.value?.let { currentManager ->
                    TacticalBoard(
                        players = players.value,
                        formation = currentManager.formation,
                        primaryColor = currentTeam.primaryColor.toComposeColor(),
                        secondaryColor = currentTeam.secondaryColor.toComposeColor(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    )

                    // Oyuncu Listesi
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .padding(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Pozisyonlara göre sıralama için bir map
                            val positionOrder = mapOf(
                                "Goalkeeper" to 1,
                                "Defender" to 2,
                                "Midfielder" to 3,
                                "Forward" to 4
                            )

                            // Oyuncuları önce pozisyona göre, sonra forma numarasına göre sırala
                            val sortedPlayers = players.value.sortedWith(
                                compareBy<Player> { positionOrder[it.position] ?: 0 }
                                    .thenBy { it.shirtNumber }
                            )

                            items(sortedPlayers) { player ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color(0xFFF5F5F5), shape = RoundedCornerShape(8.dp))
                                        .padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Forma Numarası
                                    Box(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .background(currentTeam.primaryColor.toComposeColor(), CircleShape)
                                            .border(1.dp, currentTeam.secondaryColor.toComposeColor(), CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = player.shirtNumber.toString(),
                                            color = currentTeam.secondaryColor.toComposeColor(),
                                            fontWeight = FontWeight.Bold
                                        )
                                    }

                                    // Oyuncu İsmi
                                    Text(
                                        text = player.name,
                                        modifier = Modifier.weight(1f).padding(horizontal = 12.dp),
                                        fontWeight = FontWeight.Bold
                                    )

                                    // Mevki
                                    Text(
                                        text = player.position,
                                        color = Color.Gray,
                                        modifier = Modifier.width(70.dp),
                                        fontSize = 12.sp,
                                        maxLines = 1
                                    )

                                    // Yetenek Puanı
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
    }
}

@Composable
internal fun TacticalBoard(
    players: List<Player>,
    formation: String,
    primaryColor: Color,
    secondaryColor: Color,
    modifier: Modifier = Modifier
) {
    val formationRows = formation.split("-").map { it.toInt() }
    val allRows = listOf(1) + formationRows

    Card(
        modifier = modifier.padding(16.dp),
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
                allRows.forEachIndexed { index, playerCount ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
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
    }
}

@Composable
private fun PlayerCircle(
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