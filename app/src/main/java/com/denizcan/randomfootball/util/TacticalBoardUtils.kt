package com.denizcan.randomfootball.util

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.denizcan.randomfootball.data.model.Player

@Composable
fun TacticalBoard(
    players: List<Player>,
    formation: String,
    primaryColor: Color,
    secondaryColor: Color,
    modifier: Modifier = Modifier
) {
    val formationRows = formation.split("-").map { it.toInt() }
    val allRows = listOf(1) + formationRows // [1, 4, 3, 3] gibi

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
                            1 -> players.filter { it.position == "Defender" }
                            2 -> players.filter { it.position == "Midfielder" }
                            3 -> players.filter { it.position == "Forward" }
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