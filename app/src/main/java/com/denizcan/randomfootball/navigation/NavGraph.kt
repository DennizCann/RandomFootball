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
    object TeamManagement : Screen("team_management/{teamId}") {
        fun createRoute(teamId: Long) = "team_management/$teamId"
    }
    object LeagueTable : Screen("league_table/{teamId}") {
        fun createRoute(teamId: Long) = "league_table/$teamId"
    }
    object Transfers : Screen("transfers/{teamId}") {
        fun createRoute(teamId: Long) = "transfers/$teamId"
    }
    object Statistics : Screen("statistics/{gameId}") {
        fun createRoute(gameId: Long) = "statistics/$gameId"
    }
    object NextMatch : Screen("next_match/{teamId}") {
        fun createRoute(teamId: Long) = "next_match/$teamId"
    }
    object FixturesScreen : Screen("fixtures/{teamId}") {
        fun createRoute(teamId: Long) = "fixtures/$teamId"
    }
    object LeagueFixtures : Screen("league_fixtures/{leagueId}") {
        fun createRoute(leagueId: Long) = "league_fixtures/$leagueId"
    }
    object LeagueTableDetail : Screen("league_table_detail/{leagueId}") {
        fun createRoute(leagueId: Long) = "league_table_detail/$leagueId"
    }
    object Opponent : Screen("opponent/{teamId}/{opponentId}") {
        fun createRoute(teamId: Long, opponentId: Long) = "opponent/$teamId/$opponentId"
    }
    object PlayMatch : Screen("play_match/{fixtureId}") {
        fun createRoute(fixtureId: Long) = "play_match/$fixtureId"
    }
    object TeamStatistics : Screen("team_statistics/{leagueId}/{gameId}") {
        fun createRoute(leagueId: Long, gameId: Long) = "team_statistics/$leagueId/$gameId"
    }
    object PlayerStatistics : Screen("player_statistics/{teamId}/{gameId}") {
        fun createRoute(teamId: Long, gameId: Long) = "player_statistics/$teamId/$gameId"
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
                onGameSaved = { gameId ->
                    navController.navigate(Screen.Leagues.createRoute(gameId))
                }
            )
        }

        composable(Screen.LoadGame.route) {
            LoadGameScreen(
                onBackClick = { navController.popBackStack() },
                onGameClick = { gameId ->
                    if (gameId > 1000000) {
                        navController.navigate(Screen.Dashboard.createRoute(gameId - 1000000))
                    } else {
                        navController.navigate(Screen.Leagues.createRoute(gameId))
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
            arguments = listOf(navArgument("teamId") { type = NavType.LongType})
        ) { backStackEntry ->
            val teamId = backStackEntry.arguments?.getLong("teamId") ?: return@composable
            DashboardScreen(
                teamId = teamId,
                onBackClick = {
                    navController.navigate(Screen.Menu.route) {
                        popUpTo(Screen.Welcome.route)
                    }
                },
                onTeamManagementClick = { navController.navigate(Screen.TeamManagement.createRoute(it)) },
                onLeagueTableClick = { navController.navigate(Screen.LeagueTable.createRoute(it)) },
                onTransfersClick = { navController.navigate(Screen.Transfers.createRoute(it)) },
                onStatisticsClick = { navController.navigate(Screen.Statistics.createRoute(it)) },
                onNextMatchClick = { navController.navigate(Screen.NextMatch.createRoute(it)) }
            )
        }

        composable(
            route = Screen.TeamManagement.route,
            arguments = listOf(navArgument("teamId") { type = NavType.LongType})
        ) { backStackEntry ->
            val teamId = backStackEntry.arguments?.getLong("teamId") ?: return@composable
            TeamManagementScreen(
                teamId = teamId,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.LeagueTable.route,
            arguments = listOf(navArgument("teamId") { type = NavType.LongType})
        ) { backStackEntry ->
            val teamId = backStackEntry.arguments?.getLong("teamId") ?: return@composable
            LeagueTableScreen(
                teamId = teamId,
                onBackClick = { navController.popBackStack() },
                onLeagueClick = { leagueId ->
                    navController.navigate(Screen.LeagueTableDetail.createRoute(leagueId))
                }
            )
        }

        composable(
            route = Screen.Transfers.route,
            arguments = listOf(navArgument("teamId") { type = NavType.LongType})
        ) { backStackEntry ->
            val teamId = backStackEntry.arguments?.getLong("teamId") ?: return@composable
            TransfersScreen(
                teamId = teamId,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.Statistics.route,
            arguments = listOf(navArgument("gameId") { type = NavType.LongType })
        ) { backStackEntry ->
            val gameId = backStackEntry.arguments?.getLong("gameId") ?: return@composable
            StatisticsScreen(
                gameId = gameId,
                onBackClick = { navController.popBackStack() },
                onLeagueClick = { leagueId ->
                    navController.navigate(Screen.TeamStatistics.createRoute(leagueId, gameId))
                }
            )
        }

        composable(
            route = Screen.TeamStatistics.route,
            arguments = listOf(
                navArgument("leagueId") { type = NavType.LongType },
                navArgument("gameId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val leagueId = backStackEntry.arguments?.getLong("leagueId") ?: return@composable
            val gameId = backStackEntry.arguments?.getLong("gameId") ?: return@composable
            TeamStatisticsScreen(
                leagueId = leagueId,
                onBackClick = { navController.popBackStack() },
                onTeamClick = { teamId ->
                    navController.navigate(Screen.PlayerStatistics.createRoute(teamId, gameId))
                }
            )
        }

        composable(
            route = Screen.NextMatch.route,
            arguments = listOf(navArgument("teamId") { type = NavType.LongType})
        ) { backStackEntry ->
            val teamId = backStackEntry.arguments?.getLong("teamId") ?: return@composable
            NextMatchScreen(
                teamId = teamId,
                onBackClick = { navController.popBackStack() },
                onFixturesClick = { id ->
                    navController.navigate(Screen.FixturesScreen.createRoute(id))
                },
                onOpponentClick = { opponentId ->
                    navController.navigate(Screen.Opponent.createRoute(teamId, opponentId))
                },
                onPlayMatchClick = { fixtureId ->
                    navController.navigate(Screen.PlayMatch.createRoute(fixtureId))
                }
            )
        }

        composable(
            route = Screen.FixturesScreen.route,
            arguments = listOf(navArgument("teamId") { type = NavType.LongType})
        ) { backStackEntry ->
            val teamId = backStackEntry.arguments?.getLong("teamId") ?: return@composable
            FixturesScreen(
                teamId = teamId,
                onBackClick = { navController.popBackStack() },
                onLeagueClick = { leagueId ->
                    navController.navigate(Screen.LeagueFixtures.createRoute(leagueId))
                }
            )
        }

        composable(
            route = Screen.LeagueFixtures.route,
            arguments = listOf(navArgument("leagueId") { type = NavType.LongType })
        ) { backStackEntry ->
            val leagueId = backStackEntry.arguments?.getLong("leagueId") ?: return@composable
            LeagueFixturesScreen(
                leagueId = leagueId,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.LeagueTableDetail.route,
            arguments = listOf(navArgument("leagueId") { type = NavType.LongType })
        ) { backStackEntry ->
            val leagueId = backStackEntry.arguments?.getLong("leagueId") ?: return@composable
            LeagueTableDetailScreen(
                leagueId = leagueId,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.Opponent.route,
            arguments = listOf(
                navArgument("teamId") { type = NavType.LongType },
                navArgument("opponentId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val teamId = backStackEntry.arguments?.getLong("teamId") ?: return@composable
            val opponentId = backStackEntry.arguments?.getLong("opponentId") ?: return@composable
            OpponentScreen(
                opponentId = opponentId,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.PlayMatch.route,
            arguments = listOf(navArgument("fixtureId") { type = NavType.LongType })
        ) { backStackEntry ->
            val fixtureId = backStackEntry.arguments?.getLong("fixtureId") ?: return@composable
            PlayMatchScreen(
                fixtureId = fixtureId,
                onBackClick = { navController.popBackStack() },
                onMatchEnd = {
                    val teamId = navController.previousBackStackEntry
                        ?.arguments?.getLong("teamId")
                        ?: return@PlayMatchScreen

                    navController.navigate(Screen.Dashboard.createRoute(teamId)) {
                        popUpTo(Screen.Dashboard.route) {
                            inclusive = true
                        }
                    }
                }
            )
        }

        composable(
            route = Screen.PlayerStatistics.route,
            arguments = listOf(
                navArgument("teamId") { type = NavType.LongType },
                navArgument("gameId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val teamId = backStackEntry.arguments?.getLong("teamId") ?: return@composable
            val gameId = backStackEntry.arguments?.getLong("gameId") ?: return@composable
            PlayerStatisticsScreen(
                teamId = teamId,
                gameId = gameId,
                onBackClick = { navController.popBackStack() }
            )
        }
    }
} 