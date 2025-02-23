package com.denizcan.randomfootball.ui.screens

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import kotlinx.coroutines.launch
import com.denizcan.randomfootball.util.TeamUtils
import com.denizcan.randomfootball.util.Constants
import com.denizcan.randomfootball.util.TacticalBoard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeamManagementScreen(
    teamId: Long,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val database = remember { AppDatabase.getDatabase(context) }
    val teamDao = remember { database.teamDao() }
    val managerDao = remember { database.managerDao() }
    val playerDao = remember { database.playerDao() }

    val team = teamDao.getTeamById(teamId).collectAsState(initial = null)
    val manager = managerDao.getManagerByTeamId(teamId).collectAsState(initial = null)
    val players = playerDao.getPlayersByTeamId(teamId).collectAsState(initial = emptyList())

    var showFormationDropdown by remember { mutableStateOf(false) }

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
                    // Formasyon seçici
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        OutlinedButton(
                            onClick = { showFormationDropdown = true },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Color.White,
                                containerColor = Color.Transparent
                            ),
                            border = BorderStroke(1.dp, Color.White)
                        ) {
                            Text(
                                text = "Formation: ${currentManager.formation}",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        DropdownMenu(
                            expanded = showFormationDropdown,
                            onDismissRequest = { showFormationDropdown = false },
                            modifier = Modifier
                                .fillMaxWidth(0.9f)
                                .background(Color.White)
                        ) {
                            Constants.FORMATIONS.forEach { formation ->
                                DropdownMenuItem(
                                    text = { Text(formation) },
                                    onClick = {
                                        scope.launch {
                                            managerDao.updateManagerFormation(
                                                managerId = currentManager.managerId,
                                                formation = formation
                                            )
                                        }
                                        showFormationDropdown = false
                                    },
                                    colors = MenuDefaults.itemColors(
                                        textColor = if (formation == currentManager.formation)
                                            Color(0xFF4CAF50) else Color.Black
                                    )
                                )
                            }
                        }
                    }

                    // En iyi 11'i belirle
                    val bestEleven = remember(players.value, currentManager.formation) {
                        TeamUtils.getBestEleven(players.value, currentManager.formation)
                    }

                    // Eğer bestEleven listesi boşsa veya eksikse, taktik tahtasını gösterme
                    if (bestEleven.isNotEmpty()) {
                        TacticalBoard(
                            players = bestEleven,
                            formation = currentManager.formation,
                            primaryColor = currentTeam.primaryColor.toComposeColor(),
                            secondaryColor = currentTeam.secondaryColor.toComposeColor(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                        )
                    }

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
                            // İlk 11 başlığı
                            item {
                                Text(
                                    text = "First Team",
                                    color = Color(0xFF4CAF50),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )
                            }

                            // En iyi 11'i listele
                            items(bestEleven) { player ->
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

                            // Yedekler başlığı
                            item {
                                Text(
                                    text = "Substitutes",
                                    color = Color(0xFF4CAF50),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )
                            }

                            // Yedek oyuncuları listele (ilk 11'de olmayan oyuncular)
                            val substitutes = players.value.filter { player ->
                                player !in bestEleven
                            }.sortedWith(
                                compareBy<Player> {
                                    when(it.position) {
                                        "Goalkeeper" -> 1
                                        "Defender" -> 2
                                        "Midfielder" -> 3
                                        "Forward" -> 4
                                        else -> 5
                                    }
                                }.thenByDescending { it.skill }
                                    .thenBy { it.shirtNumber }
                            )

                            items(substitutes) { player ->
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