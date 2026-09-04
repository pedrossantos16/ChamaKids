package com.pedro.ChamaKids.ui

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

class CellphoneVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val trimmed = text.text.filter { it.isDigit() }.take(11)
        val out = buildString {
            for (i in trimmed.indices) {
                if (i == 0) append("(")
                append(trimmed[i])
                if (i == 1) append(") ")
                if (i == 6) append("-")
            }
        }

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                val realOffset = text.text.take(offset).filter { it.isDigit() }.length
                if (realOffset <= 0) return 0
                if (realOffset <= 2) return realOffset + 1
                if (realOffset <= 7) return realOffset + 3
                if (realOffset <= 11) return realOffset + 4
                return out.length
            }

            override fun transformedToOriginal(offset: Int): Int {
                if (offset <= 1) return 0
                if (offset <= 4) return (offset - 1).coerceIn(0, text.length)
                if (offset <= 10) return (offset - 3).coerceIn(0, text.length)
                return (offset - 4).coerceIn(0, text.length)
            }
        }

        return TransformedText(AnnotatedString(out), offsetMapping)
    }
}
