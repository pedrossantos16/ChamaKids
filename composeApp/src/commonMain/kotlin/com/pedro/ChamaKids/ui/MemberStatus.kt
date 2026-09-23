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
            if (porcentagem == null) return NENHUM
            // Suporta porcentagem em escala 0.0 .. 1.0 (ex: 1.0f = 100%) ou 0.0 .. 100.0
            val valor = if (porcentagem in 0.0f..1.0f && porcentagem > 0.0f) porcentagem * 100f else porcentagem
            return when {
                valor >= 75f -> BOM
                valor >= 50f -> REGULAR
                else -> RUIM
            }
        }
    }
}
