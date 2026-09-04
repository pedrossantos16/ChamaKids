package com.pedro.ChamaKids

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.pedro.ChamaKids.ui.AttendanceScreen
import com.pedro.ChamaKids.ui.HomeScreen
import com.pedro.ChamaKids.ui.MenuScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pedro.ChamaKids.ui.AttendanceViewModel
import com.pedro.ChamaKids.ui.MemberViewModel
import com.pedro.ChamaKids.ui.MemberDetailScreen
import com.pedro.ChamaKids.ui.HistoryScreen
import com.pedro.ChamaKids.ui.AttendanceDetailScreen
import com.pedro.ChamaKids.ui.ListScreen
import com.pedro.ChamaKids.ui.ListDetailScreen
import com.pedro.ChamaKids.ui.AddMemberScreen
import com.pedro.ChamaKids.ui.ClassifyScreen
import com.pedro.ChamaKids.ui.ClassifyDetailScreen
import com.pedro.ChamaKids.ui.RankingScreen
import com.pedro.ChamaKids.ui.ReportScreen
import com.pedro.ChamaKids.ui.GuideScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val memberViewModel: MemberViewModel = viewModel()
    val attendanceViewModel: AttendanceViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") {
            HomeScreen(
                onMenuClick = {
                    navController.navigate("menu")
                }
            )
        }

        composable("chamada") {
            AttendanceScreen(
                onVoltar = { navController.popBackStack() },
                memberViewModel = memberViewModel,
                attendanceViewModel = attendanceViewModel
            )
        }

        composable("menu") {
            MenuScreen(
                onVoltar = { navController.popBackStack() },
                onChamada = { navController.navigate("chamada") },
                onMembros = { navController.navigate("membros") },
                onLista = { navController.navigate("lista") },
                onClassificar = { navController.navigate("classificar") },
                onRanking = { navController.navigate("ranking") },
                onRelatorio = { navController.navigate("relatorio") },
                onGuia = { navController.navigate("guia") },
                onHistorico = { navController.navigate("historico") }
            )
        }

        composable("guia") {
            GuideScreen(
                onVoltar = { navController.popBackStack() }
            )
        }

        composable("lista") {
            ListScreen(
                onVoltar = { navController.popBackStack() },
                onAbrirMembro = { membroId ->
                    navController.navigate("detalhes_lista/$membroId")
                },
                viewModel = memberViewModel
            )
        }

        composable(
            route = "detalhes_lista/{membroId}"
        ) { backStackEntry ->
            val membroId = backStackEntry.arguments?.getString("membroId")?.toIntOrNull()
            if (membroId != null) {
                ListDetailScreen(
                    membroId = membroId,
                    memberViewModel = memberViewModel,
                    attendanceViewModel = attendanceViewModel,
                    onVoltar = { navController.popBackStack() }
                )
            }
        }

        composable("membros") {
            com.pedro.ChamaKids.ui.MembersScreen(
                onVoltar = { navController.popBackStack() },
                onAdicionarMembro = { navController.navigate("adicionar_membro") },
                onAbrirMembro = { membroId -> navController.navigate("membro/$membroId") },
                viewModel = memberViewModel
            )
        }

        composable("adicionar_membro") {
            AddMemberScreen(
                onVoltar = { navController.popBackStack() },
                viewModel = memberViewModel
            )
        }

        composable(
            route = "membro/{membroId}"
        ) { backStackEntry ->
            val membroId = backStackEntry.arguments?.getString("membroId")?.toIntOrNull()
            if (membroId != null) {
                MemberDetailScreen(
                    membroId = membroId,
                    viewModel = memberViewModel,
                    onVoltar = { navController.popBackStack() }
                )
            }
        }

        composable("classificar") {
            ClassifyScreen(
                onVoltar = { navController.popBackStack() },
                onClassificarMembro = { membroId ->
                    navController.navigate("detalhes_classificar/$membroId")
                },
                viewModel = memberViewModel
            )
        }

        composable(
            route = "detalhes_classificar/{membroId}"
        ) { backStackEntry ->
            val membroId = backStackEntry.arguments?.getString("membroId")?.toIntOrNull()
            if (membroId != null) {
                ClassifyDetailScreen(
                    membroId = membroId,
                    viewModel = memberViewModel,
                    onVoltar = { navController.popBackStack() }
                )
            }
        }

        composable("ranking") {
            RankingScreen(
                onVoltar = { navController.popBackStack() },
                viewModel = memberViewModel
            )
        }

        composable("relatorio") {
            ReportScreen(
                onVoltar = { navController.popBackStack() },
                memberViewModel = memberViewModel,
                attendanceViewModel = attendanceViewModel
            )
        }

        composable("historico") {
            HistoryScreen(
                viewModel = attendanceViewModel,
                onVoltar = { navController.popBackStack() },
                onAbrirChamada = { chamadaId ->
                    navController.navigate("detalhes_chamada/$chamadaId")
                }
            )
        }

        composable(
            route = "detalhes_chamada/{chamadaId}"
        ) { backStackEntry ->
            val chamadaId = backStackEntry.arguments?.getString("chamadaId")?.toIntOrNull()
            if (chamadaId != null) {
                AttendanceDetailScreen(
                    chamadaId = chamadaId,
                    attendanceViewModel = attendanceViewModel,
                    memberViewModel = memberViewModel,
                    onVoltar = { navController.popBackStack() }
                )
            }
        }
    }
}
