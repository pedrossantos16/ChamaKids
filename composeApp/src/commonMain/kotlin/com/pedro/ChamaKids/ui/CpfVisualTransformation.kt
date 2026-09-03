package com.pedro.ChamaKids.ui

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

class CpfVisualTransformation : VisualTransformation {

    override fun filter(text: AnnotatedString): TransformedText {

        val numeros = text.text
            .filter { it.isDigit() }
            .take(11)

        val formatado = buildString {

            numeros.forEachIndexed { index, char ->

                when (index) {
                    3, 6 -> append(".")
                    9 -> append("-")
                }

                append(char)
            }
        }

        val offsetMapping = object : OffsetMapping {

            override fun originalToTransformed(offset: Int): Int {
                return when {
                    offset <= 3 -> offset
                    offset <= 6 -> offset + 1
                    offset <= 9 -> offset + 2
                    else -> offset + 3
                }.coerceAtMost(formatado.length)
            }

            override fun transformedToOriginal(offset: Int): Int {
                return when {
                    offset <= 3 -> offset
                    offset <= 7 -> offset - 1
                    offset <= 11 -> offset - 2
                    else -> offset - 3
                }.coerceIn(0, numeros.length)
            }
        }

        return TransformedText(
            text = AnnotatedString(formatado),
            offsetMapping = offsetMapping
        )
    }
}