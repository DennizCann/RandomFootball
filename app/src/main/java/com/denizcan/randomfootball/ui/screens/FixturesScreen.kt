package com.denizcan.randomfootball.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
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
import kotlinx.coroutines.flow.flowOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FixturesScreen(
    teamId: Long,
    onBackClick: () -> Unit,
    onLeagueClick: (Long) -> Unit = {} // Lig seçildiğinde çağrılacak
) {
    val context = LocalContext.current
    val database = remember { AppDatabase.getDatabase(context) }
    val teamDao = remember { database.teamDao() }
    val leagueDao = remember { database.leagueDao() }
    
    // Önce takımın lig ID'sini alalım
    val team = teamDao.getTeamById(teamId).collectAsState(initial = null)
    val league = remember(team.value?.leagueId) {
        team.value?.let { 
            leagueDao.getLeagueById(it.leagueId)
        } ?: flowOf(null)
    }.collectAsState(initial = null)
    
    // Oyundaki tüm ligleri alalım
    val leagues = remember(league.value?.gameId) {
        league.value?.let {
            leagueDao.getLeaguesByGameId(it.gameId)
        } ?: flowOf(emptyList())
    }.collectAsState(initial = emptyList())

    Scaffold(
        topBar = {
            TopBar(
                title = "Fixtures",
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
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onLeagueClick(league.leagueId) },
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = league.name,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF4CAF50)
                                )
                            }
                            
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowRight,
                                contentDescription = "View League Fixtures",
                                tint = Color(0xFF4CAF50)
                            )
                        }
                    }
                }
            }
        }
    }
} 