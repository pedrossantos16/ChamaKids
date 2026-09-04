package com.pedro.ChamaKids.ui

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

class RgVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val trimmed = text.text.filter { it.isDigit() }.take(9)
        val out = buildString {
            for (i in trimmed.indices) {
                append(trimmed[i])
                if (i == 1 || i == 4) append(".")
                if (i == 7) append("-")
            }
        }

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                val realOffset = text.text.take(offset).filter { it.isDigit() }.length
                if (realOffset <= 2) return realOffset
                if (realOffset <= 5) return realOffset + 1
                if (realOffset <= 8) return realOffset + 2
                return realOffset + 3
            }

            override fun transformedToOriginal(offset: Int): Int {
                if (offset <= 2) return offset
                if (offset <= 6) return (offset - 1).coerceIn(0, text.length)
                if (offset <= 10) return (offset - 2).coerceIn(0, text.length)
                return (offset - 3).coerceIn(0, text.length)
            }
        }

        return TransformedText(AnnotatedString(out), offsetMapping)
    }
}
