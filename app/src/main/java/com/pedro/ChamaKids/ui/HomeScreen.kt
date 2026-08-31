package com.pedro.ChamaKids.ui

import androidx.compose.foundation.clickable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding

import androidx.compose.material3.Button
import androidx.compose.material3.Text

import androidx.compose.runtime.Composable

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


/*
 * HomeScreen
 *
 * Tela inicial do ChamaKids.
 *
 * Contém apenas:
 *
 * - nome do aplicativo;
 * - botão para iniciar uma chamada;
 * - botão de menu no canto superior direito.
 *
 * Mantemos essa tela propositalmente simples.
 */
@Composable
fun HomeScreen(
    onIniciarChamada: () -> Unit,
    onMenuClick: () -> Unit
) {

    /*
     * Box permite colocar elementos em diferentes
     * posições dentro da mesma tela.
     */
    Box(
        modifier = Modifier.fillMaxSize()
    ) {


        // =====================================================
        // MENU SUPERIOR DIREITO
        // =====================================================

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 16.dp,

                    /*
                     * Quanto maior o "end",
                     * mais afastados os três pontos ficam
                     * da borda direita.
                     */
                    end = 28.dp,

                    /*
                     * Quanto maior o "top",
                     * mais para baixo o menu fica.
                     */
                    top = 35.dp,

                    bottom = 16.dp
                ),

            horizontalArrangement =
                Arrangement.End
        ) {

            /*
             * Utilizamos o caractere ⋮ em vez de
             * adicionar uma biblioteca somente
             * para ter um ícone de menu.
             */
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .clickable {
                        onMenuClick()
                    },
                contentAlignment = Alignment.Center
            ) {

                Text(
                    text = "⋮",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }


        // =====================================================
        // CONTEÚDO CENTRAL
        // =====================================================

        Column(

            /*
             * Alignment.Center posiciona o conjunto
             * aproximadamente no centro da tela.
             *
             * offset pode ser usado para subir/descer
             * todo esse bloco posteriormente.
             */
            modifier = Modifier
                .align(
                    Alignment.Center
                )
                .offset(
                    x = 0.dp,
                    y = 0.dp
                ),

            horizontalAlignment =
                Alignment.CenterHorizontally,

            /*
             * Define o espaço entre:
             *
             * ChamaKids
             * e
             * INICIAR CHAMADA
             */
            verticalArrangement =
                Arrangement.spacedBy(
                    40.dp
                )
        ) {


            // =================================================
            // NOME DO APLICATIVO
            // =================================================

            Text(
                text = "ChamaKids",
                fontSize = 42.sp
            )


            // =================================================
            // INICIAR CHAMADA
            // =================================================

            Button(
                onClick = onIniciarChamada
            ) {

                Text(
                    text = "INICIAR CHAMADA"
                )
            }
        }
    }
}