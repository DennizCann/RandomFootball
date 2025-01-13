package com.denizcan.randomfootball

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.denizcan.randomfootball.navigation.NavGraph
import com.denizcan.randomfootball.ui.theme.RandomFootballTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RandomFootballTheme {
                val navController = rememberNavController()
                NavGraph(navController = navController)
            }
        }
    }
}