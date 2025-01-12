package com.denizcan.randomfootball

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import com.denizcan.randomfootball.ui.screens.*
import com.denizcan.randomfootball.ui.theme.RandomFootballTheme
import androidx.navigation.compose.rememberNavController
import com.denizcan.randomfootball.navigation.NavGraph

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

sealed class Screen {
    object Welcome : Screen()
    object Menu : Screen()
    object NewGame : Screen()
    object LoadGame : Screen()
    data class Leagues(val gameId: Long) : Screen()
    data class Teams(val leagueId: Long, val gameId: Long) : Screen()
    data class TeamDetails(val teamId: Long, val gameId: Long) : Screen()
    data class CreateManager(val teamId: Long, val gameId: Long) : Screen()
    data class Dashboard(val teamId: Long) : Screen()
}

@Composable
fun App() {
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Welcome) }

    when (currentScreen) {
        is Screen.Welcome -> WelcomeScreen(
            onScreenClick = { currentScreen = Screen.Menu }
        )
        is Screen.Menu -> MenuScreen(
            onNewGameClick = { currentScreen = Screen.NewGame },
            onLoadGameClick = { currentScreen = Screen.LoadGame }
        )
        is Screen.NewGame -> NewGameScreen(
            onBackClick = { currentScreen = Screen.Menu },
            onGameCreated = { gameId -> currentScreen = Screen.Leagues(gameId) }
        )
        is Screen.LoadGame -> LoadGameScreen(
            onBackClick = { currentScreen = Screen.Menu },
            onGameClick = { id -> 
                if (id > 1000000) {
                    currentScreen = Screen.Dashboard(id - 1000000)
                } else {
                    currentScreen = Screen.Leagues(id)
                }
            }
        )
        is Screen.Leagues -> LeaguesScreen(
            gameId = (currentScreen as Screen.Leagues).gameId,
            onBackClick = { currentScreen = Screen.Menu },
            onLeagueClick = { leagueId -> 
                val gameId = (currentScreen as Screen.Leagues).gameId
                currentScreen = Screen.Teams(leagueId, gameId)
            }
        )
        is Screen.Teams -> TeamsScreen(
            leagueId = (currentScreen as Screen.Teams).leagueId,
            onBackClick = {
                val gameId = (currentScreen as Screen.Teams).gameId
                currentScreen = Screen.Leagues(gameId)
            },
            onTeamClick = { teamId ->
                val gameId = (currentScreen as Screen.Teams).gameId
                currentScreen = Screen.TeamDetails(
                    teamId = teamId,
                    gameId = gameId
                )
            }
        )
        is Screen.TeamDetails -> TeamDetailsScreen(
            teamId = (currentScreen as Screen.TeamDetails).teamId,
            gameId = (currentScreen as Screen.TeamDetails).gameId,
            onBackClick = {
                val leagueId = (currentScreen as Screen.TeamDetails).gameId
                currentScreen = Screen.Teams(leagueId, (currentScreen as Screen.TeamDetails).gameId)
            },
            onTeamSelected = { teamId ->
                currentScreen = Screen.CreateManager(teamId, (currentScreen as Screen.TeamDetails).gameId)
            }
        )
        is Screen.CreateManager -> CreateManagerScreen(
            teamId = (currentScreen as Screen.CreateManager).teamId,
            gameId = (currentScreen as Screen.CreateManager).gameId,
            onBackClick = {
                currentScreen = Screen.TeamDetails(
                    teamId = (currentScreen as Screen.CreateManager).teamId,
                    gameId = (currentScreen as Screen.CreateManager).gameId
                )
            },
            onDashboardNavigate = { 
                val teamId = (currentScreen as Screen.CreateManager).teamId
                currentScreen = Screen.Dashboard(teamId)
            }
        )
        is Screen.Dashboard -> DashboardScreen(
            teamId = (currentScreen as Screen.Dashboard).teamId,
            onBackClick = { currentScreen = Screen.Menu }
        )
    }
}