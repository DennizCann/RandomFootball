package com.denizcan.randomfootball.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.denizcan.randomfootball.data.AppDatabase
import com.denizcan.randomfootball.ui.components.TopBar
import kotlinx.coroutines.flow.flowOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(
    teamId: Long,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val database = remember { AppDatabase.getDatabase(context) }
    val leagueDao = remember { database.leagueDao() }
    val teamDao = remember { database.teamDao() }
    
    val team = teamDao.getTeamById(teamId).collectAsState(initial = null)
    val league = remember(team.value?.leagueId) {
        team.value?.let { 
            leagueDao.getLeagueById(it.leagueId)
        } ?: flowOf(null)
    }.collectAsState(initial = null)
    
    val leagues = remember(league.value?.gameId) {
        league.value?.let {
            leagueDao.getLeaguesByGameId(it.gameId)
        } ?: flowOf(emptyList())
    }.collectAsState(initial = emptyList())

    Scaffold(
        topBar = {
            TopBar(
                title = "Statistics",
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
                items(leagues.value) { league ->
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
                            // Sıralama
                            Text(
                                text = "${leagues.value.indexOf(league) + 1}",
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.width(30.dp)
                            )

                            // Lig İsmi
                            Text(
                                text = league.name,
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(horizontal = 12.dp),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
} 