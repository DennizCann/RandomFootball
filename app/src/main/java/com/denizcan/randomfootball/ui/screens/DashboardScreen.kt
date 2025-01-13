package com.denizcan.randomfootball.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.denizcan.randomfootball.data.AppDatabase
import com.denizcan.randomfootball.ui.components.TopBar
import kotlinx.coroutines.flow.flowOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    teamId: Long,
    onBackClick: () -> Unit,
    onTeamManagementClick: (Long) -> Unit,
    onLeagueTableClick: (Long) -> Unit,
    onTransfersClick: (Long) -> Unit,
    onStatisticsClick: (Long) -> Unit,
    onNextMatchClick: (Long) -> Unit,
    onLeagueTableDetailClick: (Long) -> Unit
) {
    val context = LocalContext.current
    val database = remember { AppDatabase.getDatabase(context) }
    val teamDao = remember { database.teamDao() }
    val managerDao = remember { database.managerDao() }
    val leagueDao = remember { database.leagueDao() }
    
    val team = teamDao.getTeamById(teamId).collectAsState(initial = null)
    val manager = managerDao.getManagerByTeamId(teamId).collectAsState(initial = null)
    val league = remember(team.value?.leagueId) {
        team.value?.let { 
            leagueDao.getLeagueById(it.leagueId)
        } ?: flowOf(null)
    }.collectAsState(initial = null)

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
            // Takım Bilgi Kartı
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    team.value?.let { currentTeam ->
                        Text(
                            text = currentTeam.name,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF4CAF50)
                        )
                    }
                    
                    league.value?.let { currentLeague ->
                        Text(
                            text = currentLeague.name,
                            fontSize = 16.sp,
                            color = Color.Gray
                        )
                    }

                    manager.value?.let { currentManager ->
                        Text(
                            text = "Manager: ${currentManager.name}",
                            fontSize = 16.sp,
                            color = Color.Gray
                        )
                        Text(
                            text = "Formation: ${currentManager.formation}",
                            fontSize = 16.sp,
                            color = Color.Gray
                        )
                    }
                }
            }

            // Menü Kartları
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.weight(1f)
            ) {
                item {
                    MenuCard(
                        title = "Team Management",
                        icon = Icons.Default.Person,
                        onClick = { onTeamManagementClick(teamId) }
                    )
                }
                
                item {
                    MenuCard(
                        title = "League Table",
                        icon = Icons.Default.List,
                        onClick = { 
                            onLeagueTableClick(teamId)
                        }
                    )
                }
                
                item {
                    MenuCard(
                        title = "Transfers",
                        icon = Icons.Default.Refresh,
                        onClick = { onTransfersClick(teamId) }
                    )
                }
                
                item {
                    MenuCard(
                        title = "Statistics",
                        icon = Icons.Default.Info,
                        onClick = { onStatisticsClick(teamId) }
                    )
                }
            }

            // Sıradaki Maç Kartı
            NextMatchCard(
                onClick = { onNextMatchClick(teamId) }
            )
        }
    }
}

@Composable
private fun MenuCard(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                modifier = Modifier.size(48.dp),
                tint = Color(0xFF4CAF50)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF4CAF50)
            )
        }
    }
}

@Composable
private fun NextMatchCard(onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Next Match",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF4CAF50)
            )
            
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = "Play Next Match",
                tint = Color(0xFF4CAF50)
            )
        }
    }
}
