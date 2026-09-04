package com.pedro.ChamaKids.ui

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

class RgVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val trimmed = text.text.filter { it.isDigit() }.take(9)
        var out = ""
        for (i in trimmed.indices) {
            out += trimmed[i]
            if (i == 1 || i == 4) out += "."
            if (i == 7) out += "-"
        }

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                if (offset <= 2) return offset
                if (offset <= 5) return offset + 1
                if (offset <= 8) return offset + 2
                return offset + 3
            }

            override fun transformedToOriginal(offset: Int): Int {
                if (offset <= 2) return offset
                if (offset <= 6) return offset - 1
                if (offset <= 10) return offset - 2
                return (offset - 3).coerceIn(0, trimmed.length)
            }
        }

        return TransformedText(AnnotatedString(out), offsetMapping)
    }
}
