package com.pedro.ChamaKids

import androidx.compose.runtime.Composable

import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

import com.pedro.ChamaKids.ui.AttendanceScreen
import com.pedro.ChamaKids.ui.HomeScreen
import com.pedro.ChamaKids.ui.MenuScreen
import com.pedro.ChamaKids.ui.MembersScreen
import com.pedro.ChamaKids.ui.AddMemberScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pedro.ChamaKids.ui.AttendanceViewModel
import com.pedro.ChamaKids.ui.MemberViewModel
import com.pedro.ChamaKids.ui.MemberDetailScreen
import com.pedro.ChamaKids.ui.HistoryScreen
import com.pedro.ChamaKids.ui.AttendanceDetailScreen

/*
 * AppNavigation
 *
 * Centraliza a navegação entre as telas do ChamaKids.
 *
 * Neste momento existem três rotas:
 *
 * home
 *     Tela inicial.
 *
 * chamada
 *     Tela onde a presença dos membros é registrada.
 *
 * menu
 *     Tela que futuramente dará acesso a:
 *     - membros;
 *     - lista;
 *     - histórico;
 *     - outras opções.
 */
@Composable
fun AppNavigation() {

    /*
     * NavController
     *
     * É o objeto responsável por mandar o aplicativo
     * de uma tela para outra.
     */
    val navController =
        rememberNavController()

    val memberViewModel: MemberViewModel =
        viewModel()

    val attendanceViewModel: AttendanceViewModel =
        viewModel()

    /*
     * NavHost
     *
     * Contém todas as telas disponíveis na navegação.
     *
     * startDestination define qual tela será aberta
     * quando o aplicativo iniciar.
     */
    NavHost(
        navController = navController,
        startDestination = "home"
    ) {


        // =====================================================
        // TELA PRINCIPAL
        // =====================================================

        composable("home") {

            HomeScreen(

                /*
                 * Quando o usuário tocar em
                 * "INICIAR CHAMADA",
                 * abrimos a tela de chamada.
                 */
                onIniciarChamada = {

                    navController.navigate(
                        "chamada"
                    )
                },


                /*
                 * Quando o usuário tocar nos três pontos,
                 * abrimos o menu principal.
                 */
                onMenuClick = {

                    navController.navigate(
                        "menu"
                    )
                }
            )
        }


        // =====================================================
        // TELA DE CHAMADA
        // =====================================================

        composable("chamada") {

            AttendanceScreen(
                onSalvar = {
                    navController.popBackStack()
                },

                onVoltar = {
                    navController.popBackStack()
                },

                memberViewModel = memberViewModel,
                attendanceViewModel = attendanceViewModel
            )
        }


        // =====================================================
        // MENU
        // =====================================================

        composable("menu") {

            MenuScreen(

                /*
                 * Retorna para a tela anterior.
                 */
                onVoltar = {

                    navController.popBackStack()
                },


                /*
                 * Essas três ações ainda serão
                 * implementadas futuramente.
                 */

                onMembros = {

                    navController.navigate(
                        "membros"
                    )
                },

                onLista = {

                    // Futuramente abrirá
                    // a tela geral da lista.
                },

                onHistorico = {

                    navController.navigate(
                        "historico"
                    )
                }
            )
        }

        // =====================================================
        // MEMBROS
        // =====================================================

        composable("membros") {

            MembersScreen(
                onVoltar = {
                    navController.popBackStack()
                },

                onAdicionarMembro = {
                    navController.navigate(
                        "adicionar_membro"
                    )
                },

                onAbrirMembro = { membroId ->

                    navController.navigate(
                        "membro/$membroId"
                    )
                },

                viewModel = memberViewModel
            )
        }

        // =====================================================
        // ADICIONAR MEMBROS
        // =====================================================

        composable("adicionar_membro") {

            AddMemberScreen(
                onVoltar = {
                    navController.popBackStack()
                },
                viewModel = memberViewModel
            )
        }

        // =====================================================
        // FICHA DE MEMBROS
        // =====================================================

        composable(
            route = "membro/{membroId}"
        ) { backStackEntry ->

            val membroId =
                backStackEntry
                    .arguments
                    ?.getString("membroId")
                    ?.toIntOrNull()

            if (membroId != null) {

                MemberDetailScreen(
                    membroId = membroId,
                    viewModel = memberViewModel,
                    onVoltar = {
                        navController.popBackStack()
                    }
                )
            }
        }

        // =====================================================
        // HISTORICO
        // =====================================================

        composable("historico") {

            HistoryScreen(
                viewModel =
                    attendanceViewModel,

                onVoltar = {
                    navController.popBackStack()
                },

                onAbrirChamada = { chamadaId ->

                    navController.navigate(
                        "detalhes_chamada/$chamadaId"
                    )
                }
            )
        }

        // =====================================================
        // DETALHES DA CHAMADA
        // =====================================================

        composable(
            route = "detalhes_chamada/{chamadaId}"
        ) { backStackEntry ->

            val chamadaId =
                backStackEntry
                    .arguments
                    ?.getString("chamadaId")
                    ?.toIntOrNull()

            if (chamadaId != null) {

                AttendanceDetailScreen(
                    chamadaId = chamadaId,

                    attendanceViewModel =
                        attendanceViewModel,

                    memberViewModel =
                        memberViewModel,

                    onVoltar = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}