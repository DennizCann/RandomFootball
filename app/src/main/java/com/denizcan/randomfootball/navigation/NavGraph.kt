package com.denizcan.randomfootball.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.denizcan.randomfootball.ui.screens.*

sealed class Screen(val route: String) {
    object Welcome : Screen("welcome")
    object Menu : Screen("menu")
    object NewGame : Screen("new_game")
    object LoadGame : Screen("load_game")
    object Leagues : Screen("leagues/{gameId}") {
        fun createRoute(gameId: Long) = "leagues/$gameId"
    }
    object Teams : Screen("teams/{leagueId}/{gameId}") {
        fun createRoute(leagueId: Long, gameId: Long) = "teams/$leagueId/$gameId"
    }
    object TeamDetails : Screen("team_details/{teamId}/{gameId}") {
        fun createRoute(teamId: Long, gameId: Long) = "team_details/$teamId/$gameId"
    }
    object CreateManager : Screen("create_manager/{teamId}/{gameId}") {
        fun createRoute(teamId: Long, gameId: Long) = "create_manager/$teamId/$gameId"
    }
    object Dashboard : Screen("dashboard/{teamId}") {
        fun createRoute(teamId: Long) = "dashboard/$teamId"
    }
}

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Welcome.route
    ) {
        composable(Screen.Welcome.route) {
            WelcomeScreen(
                onScreenClick = { navController.navigate(Screen.Menu.route) }
            )
        }

        composable(Screen.Menu.route) {
            MenuScreen(
                onNewGameClick = { navController.navigate(Screen.NewGame.route) },
                onLoadGameClick = { navController.navigate(Screen.LoadGame.route) }
            )
        }

        composable(Screen.NewGame.route) {
            NewGameScreen(
                onBackClick = { navController.popBackStack() },
                onGameCreated = { gameId -> 
                    navController.navigate(Screen.Leagues.createRoute(gameId))
                }
            )
        }

        composable(Screen.LoadGame.route) {
            LoadGameScreen(
                onBackClick = { navController.popBackStack() },
                onGameClick = { id -> 
                    if (id > 1000000) {
                        navController.navigate(Screen.Dashboard.createRoute(id - 1000000))
                    } else {
                        navController.navigate(Screen.Leagues.createRoute(id))
                    }
                }
            )
        }

        composable(
            route = Screen.Leagues.route,
            arguments = listOf(navArgument("gameId") { type = NavType.LongType })
        ) { backStackEntry ->
            val gameId = backStackEntry.arguments?.getLong("gameId") ?: return@composable
            LeaguesScreen(
                gameId = gameId,
                onBackClick = { navController.popBackStack() },
                onLeagueClick = { leagueId -> 
                    navController.navigate(Screen.Teams.createRoute(leagueId, gameId))
                }
            )
        }

        composable(
            route = Screen.Teams.route,
            arguments = listOf(
                navArgument("leagueId") { type = NavType.LongType },
                navArgument("gameId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val leagueId = backStackEntry.arguments?.getLong("leagueId") ?: return@composable
            val gameId = backStackEntry.arguments?.getLong("gameId") ?: return@composable
            TeamsScreen(
                leagueId = leagueId,
                onBackClick = { navController.popBackStack() },
                onTeamClick = { teamId ->
                    navController.navigate(Screen.TeamDetails.createRoute(teamId, gameId))
                }
            )
        }

        composable(
            route = Screen.TeamDetails.route,
            arguments = listOf(
                navArgument("teamId") { type = NavType.LongType },
                navArgument("gameId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val teamId = backStackEntry.arguments?.getLong("teamId") ?: return@composable
            val gameId = backStackEntry.arguments?.getLong("gameId") ?: return@composable
            TeamDetailsScreen(
                teamId = teamId,
                gameId = gameId,
                onBackClick = { navController.popBackStack() },
                onTeamSelected = { selectedTeamId ->
                    navController.navigate(Screen.CreateManager.createRoute(selectedTeamId, gameId))
                }
            )
        }

        composable(
            route = Screen.CreateManager.route,
            arguments = listOf(
                navArgument("teamId") { type = NavType.LongType },
                navArgument("gameId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val teamId = backStackEntry.arguments?.getLong("teamId") ?: return@composable
            val gameId = backStackEntry.arguments?.getLong("gameId") ?: return@composable
            CreateManagerScreen(
                teamId = teamId,
                gameId = gameId,
                onBackClick = { navController.popBackStack() },
                onDashboardNavigate = {
                    navController.navigate(Screen.Dashboard.createRoute(teamId)) {
                        popUpTo(Screen.Welcome.route)
                    }
                }
            )
        }

        composable(
            route = Screen.Dashboard.route,
            arguments = listOf(
                navArgument("teamId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val teamId = backStackEntry.arguments?.getLong("teamId") ?: return@composable
            DashboardScreen(
                teamId = teamId,
                onBackClick = {
                    navController.navigate(Screen.Menu.route) {
                        popUpTo(Screen.Welcome.route)
                    }
                }
            )
        }
    }
} 