package com.pedro.ChamaKids.ui

import androidx.compose.ui.graphics.Color

/**
 * Centraliza a lógica de status de frequência do projeto.
 */
enum class StatusFrequencia(val label: String, val cor: Color) {
    BOM("BOM", Color(0xFF00A381)),
    REGULAR("REGULAR", Color(0xFFFBC02D)),
    RUIM("RUIM", Color(0xFFD32F2F)),
    NENHUM("---", Color.Gray);

    companion object {
        fun aPartirDaPorcentagem(porcentagem: Float?): StatusFrequencia {
            return when {
                porcentagem == null -> NENHUM
                porcentagem >= 75f -> BOM
                porcentagem >= 50f -> REGULAR
                else -> RUIM
            }
        }
    }
}
