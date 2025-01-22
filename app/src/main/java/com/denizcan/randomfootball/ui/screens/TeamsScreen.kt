package com.denizcan.randomfootball.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import com.denizcan.randomfootball.ui.components.TopBar
import com.denizcan.randomfootball.util.TeamUtils
import kotlinx.coroutines.flow.first

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeamsScreen(
    leagueId: Long,
    onBackClick: () -> Unit,
    onTeamClick: (Long) -> Unit
) {
    val context = LocalContext.current
    val database = remember { AppDatabase.getDatabase(context) }
    val teamDao = remember { database.teamDao() }
    val playerDao = remember { database.playerDao() }
    val managerDao = remember { database.managerDao() }
    
    val teams = teamDao.getTeamsByLeagueId(leagueId).collectAsState(initial = emptyList())
    
    // Takım istatistiklerini tutacak state
    var teamStats by remember { mutableStateOf(mapOf<Long, TeamUtils.TeamStats>()) }
    
    // Her takım için istatistikleri hesapla
    LaunchedEffect(teams.value) {
        val newStats = mutableMapOf<Long, TeamUtils.TeamStats>()
        
        teams.value.forEach { team ->
            // Takımın oyuncularını al
            val players = playerDao.getPlayersByTeamId(team.teamId).first()
            // Takımın menajerini al
            val manager = managerDao.getManagerById(team.managerId).first()
            
            // Menajer varsa formasyon bilgisini kullan, yoksa varsayılan formasyon
            val formation = manager?.formation ?: "4-4-2"
            
            // İlk 11'i seç ve puanları hesapla
            val firstEleven = TeamUtils.selectFirstEleven(players, formation)
            val stats = TeamUtils.calculateTeamStats(players, formation)
            
            newStats[team.teamId] = stats
        }
        
        teamStats = newStats
    }

    Scaffold(
        topBar = {
            TopBar(
                title = "Teams",
                onBackClick = onBackClick
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF4CAF50))
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(teams.value) { team ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onTeamClick(team.teamId) },
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        // Üst kısım - Takım adı ve renkler
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = team.name,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF4CAF50)
                            )

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .background(
                                            Color(android.graphics.Color.parseColor(team.primaryColor)),
                                            shape = CircleShape
                                        )
                                        .border(
                                            width = 2.dp,
                                            color = Color.Black,
                                            shape = CircleShape
                                        )
                                )

                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .background(
                                            Color(android.graphics.Color.parseColor(team.secondaryColor)),
                                            shape = CircleShape
                                        )
                                        .border(
                                            width = 2.dp,
                                            color = Color.Black,
                                            shape = CircleShape
                                        )
                                )
                            }
                        }

                        // Alt kısım - Takım istatistikleri
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Attack",
                                    fontSize = 12.sp,
                                    color = Color.Gray
                                )
                                Text(
                                    text = teamStats[team.teamId]?.attackPoints?.toString() ?: "...",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF4CAF50)
                                )
                            }

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Defense",
                                    fontSize = 12.sp,
                                    color = Color.Gray
                                )
                                Text(
                                    text = teamStats[team.teamId]?.defensePoints?.toString() ?: "...",
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
