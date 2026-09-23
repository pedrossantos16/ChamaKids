package com.pedro.ChamaKids

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.pedro.ChamaKids.ui.*
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val memberViewModel: MemberViewModel = viewModel()
    val attendanceViewModel: AttendanceViewModel = viewModel()
    val userViewModel: UserViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") {
            HomeScreen(
                onMenuClick = {
                    navController.navigate("menu")
                },
                userViewModel = userViewModel
            )
        }

        composable("chamada") {
            AttendanceScreen(
                onVoltar = { navController.popBackStack() },
                memberViewModel = memberViewModel,
                attendanceViewModel = attendanceViewModel,
                userViewModel = userViewModel
            )
        }

        composable("menu") {
            val isFirstAccess by userViewModel.isFirstAccess.collectAsState()
            val isFrozen by userViewModel.isFrozen.collectAsState()
            val currentUser by userViewModel.currentUser.collectAsState()
            
            MenuScreen(
                onVoltar = { navController.popBackStack() },
                onChamada = { navController.navigate("chamada") },
                onMembros = { navController.navigate("membros") },
                onLista = { navController.navigate("lista") },
                onClassificar = { navController.navigate("classificar") },
                onRanking = { navController.navigate("ranking") },
                onRelatorio = { navController.navigate("relatorio") },
                onGuia = { navController.navigate("guia") },
                onHistorico = { navController.navigate("historico") },
                onUsuarios = { navController.navigate("usuarios") },
                onSoftware = { navController.navigate("software") },
                isFirstAccess = isFirstAccess,
                isFrozen = isFrozen,
                currentUser = currentUser
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
                onAbrirMembro = { serverId ->
                    navController.navigate("detalhes_lista/$serverId")
                },
                viewModel = memberViewModel
            )
        }

        composable(
            route = "detalhes_lista/{membroId}"
        ) { backStackEntry ->
            val membroId = backStackEntry.arguments?.getString("membroId") ?: ""
            ListDetailScreen(
                membroId = membroId,
                memberViewModel = memberViewModel,
                attendanceViewModel = attendanceViewModel,
                userViewModel = userViewModel,
                onVoltar = { navController.popBackStack() }
            )
        }

        composable("membros") {
            com.pedro.ChamaKids.ui.MembersScreen(
                onVoltar = { navController.popBackStack() },
                onAdicionarMembro = { navController.navigate("adicionar_membro") },
                onAbrirMembro = { serverId -> navController.navigate("membro/$serverId") },
                viewModel = memberViewModel
            )
        }

        composable("adicionar_membro") {
            AddMemberScreen(
                onVoltar = { navController.popBackStack() },
                viewModel = memberViewModel,
                userViewModel = userViewModel
            )
        }

        composable(
            route = "membro/{membroId}"
        ) { backStackEntry ->
            val membroId = backStackEntry.arguments?.getString("membroId") ?: ""
            MemberDetailScreen(
                serverId = membroId,
                viewModel = memberViewModel,
                userViewModel = userViewModel,
                onVoltar = { navController.popBackStack() }
            )
        }

        composable("classificar") {
            ClassifyScreen(
                onVoltar = { navController.popBackStack() },
                onClassificarMembro = { serverId ->
                    navController.navigate("detalhes_classificar/$serverId")
                },
                viewModel = memberViewModel
            )
        }

        composable(
            route = "detalhes_classificar/{membroId}"
        ) { backStackEntry ->
            val membroId = backStackEntry.arguments?.getString("membroId") ?: ""
            ClassifyDetailScreen(
                serverId = membroId,
                viewModel = memberViewModel,
                userViewModel = userViewModel,
                onVoltar = { navController.popBackStack() }
            )
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
                onAbrirChamada = { serverId ->
                    navController.navigate("detalhes_chamada/$serverId")
                }
            )
        }

        composable(
            route = "detalhes_chamada/{chamadaId}"
        ) { backStackEntry ->
            val chamadaId = backStackEntry.arguments?.getString("chamadaId") ?: ""
            AttendanceDetailScreen(
                chamadaId = chamadaId,
                attendanceViewModel = attendanceViewModel,
                memberViewModel = memberViewModel,
                onVoltar = { navController.popBackStack() }
            )
        }

        composable("usuarios") {
            UsersScreen(
                onVoltar = { navController.popBackStack() },
                viewModel = userViewModel
            )
        }

        composable("software") {
            SoftwareScreen(
                onVoltar = { navController.popBackStack() },
                viewModel = userViewModel
            )
        }
    }
}
