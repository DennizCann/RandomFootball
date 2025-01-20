package com.denizcan.randomfootball.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import com.denizcan.randomfootball.data.model.Manager
import com.denizcan.randomfootball.ui.components.TopBar
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateManagerScreen(
    teamId: Long,
    gameId: Long,
    onBackClick: () -> Unit,
    onDashboardNavigate: () -> Unit
) {
    val context = LocalContext.current
    val database = remember { AppDatabase.getDatabase(context) }
    val managerDao = remember { database.managerDao() }
    val gameDao = remember { database.gameDao() }

    var managerName by remember { mutableStateOf("") }
    var selectedNationality by remember { mutableStateOf("") }
    val nationalities = listOf(
        "Brazil", "Germany", "Italy", "Argentina", "France",
        "England", "Spain", "Netherlands", "Portugal", "Turkey"
    )

    val scope = rememberCoroutineScope()

    // Mevcut menajeri yükle
    LaunchedEffect(teamId) {
        managerDao.getManagerByTeamIdSync(teamId)?.let { manager ->
            managerName = manager.name
            selectedNationality = manager.nationality
        }
    }

    Scaffold(
        topBar = {
            TopBar(
                title = "Create Manager",
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
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Create Manager",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            OutlinedTextField(
                value = managerName,
                onValueChange = { managerName = it },
                label = { Text("Manager Name") },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.White,
                    unfocusedBorderColor = Color.White,
                    focusedLabelColor = Color.White,
                    cursorColor = Color.White
                ),
                modifier = Modifier.fillMaxWidth()
            )

            DropdownMenuWithLabel(
                label = "Nationality",
                items = nationalities,
                selectedItem = selectedNationality,
                onItemSelected = { selectedNationality = it }
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    if (managerName.isNotBlank() && selectedNationality.isNotBlank()) {
                        scope.launch {
                            val currentManager = managerDao.getManagerByTeamIdSync(teamId)

                            if (currentManager != null) {
                                // Mevcut menajeri güncelle
                                val updatedManager = currentManager.copy(
                                    name = managerName,
                                    nationality = selectedNationality
                                    // formation değişmiyor
                                )
                                managerDao.updateManager(updatedManager)
                            } else {
                                // Yeni menajer oluştur
                                val newManager = Manager(
                                    teamId = teamId,
                                    gameId = gameId,
                                    name = managerName,
                                    nationality = selectedNationality,
                                    formation = "4-4-2" // Varsayılan diziliş
                                )
                                managerDao.insertManager(newManager)
                            }

                            // Game tablosunu güncelle
                            gameDao.updateSelectedTeam(gameId, teamId)

                            onDashboardNavigate()
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color(0xFF4CAF50)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text(
                    text = "Save Manager",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun DropdownMenuWithLabel(
    label: String,
    items: List<String>,
    selectedItem: String,
    onItemSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column {
        Text(
            text = label,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White.copy(alpha = 0.1f))
                .padding(8.dp)
        ) {
            Text(
                text = selectedItem.ifBlank { "Select $label" },
                color = Color.White,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = true }
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            items.forEach { item ->
                DropdownMenuItem(
                    onClick = {
                        onItemSelected(item)
                        expanded = false
                    },
                    text = { Text(text = item) }
                )
            }
        }
    }
}