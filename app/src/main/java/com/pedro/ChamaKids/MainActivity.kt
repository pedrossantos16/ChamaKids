package com.pedro.ChamaKids

import android.os.Bundle

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

import com.pedro.ChamaKids.ui.theme.ChamaKidsTheme


/*
 * MainActivity
 *
 * É o ponto de entrada principal do aplicativo.
 *
 * A ideia é manter esta Activity o mais enxuta possível.
 * Ela apenas:
 *
 * 1. inicia o aplicativo;
 * 2. aplica o tema visual;
 * 3. chama o sistema de navegação.
 *
 * As telas e regras do aplicativo não devem ficar aqui.
 */
class MainActivity : ComponentActivity() {

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {

        super.onCreate(savedInstanceState)

        setContent {

            // Aplica cores, tipografia e demais características
            // visuais definidas no tema do ChamaKids.
            ChamaKidsTheme {

                // Controla qual tela do aplicativo será exibida.
                AppNavigation()
            }
        }
    }
}