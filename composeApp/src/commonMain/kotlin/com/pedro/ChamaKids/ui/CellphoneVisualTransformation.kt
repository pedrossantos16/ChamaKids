package com.pedro.ChamaKids.ui

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

class CellphoneVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val trimmed = text.text.filter { it.isDigit() }.take(11)
        var out = ""
        for (i in trimmed.indices) {
            if (i == 0) out += "("
            out += trimmed[i]
            if (i == 1) out += ") "
            if (i == 6) out += "-"
        }

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                if (offset <= 0) return 0
                if (offset <= 1) return offset + 1 // Depois do (
                if (offset <= 2) return offset + 3 // Depois do ) 
                if (offset <= 7) return offset + 4 // Depois do -
                if (offset <= 11) return offset + 5
                return out.length
            }

            override fun transformedToOriginal(offset: Int): Int {
                if (offset <= 1) return 0
                if (offset <= 4) return (offset - 1).coerceAtLeast(0)
                if (offset <= 9) return (offset - 3).coerceAtLeast(0)
                return (offset - 4).coerceIn(0, trimmed.length)
            }
        }

        return TransformedText(AnnotatedString(out), offsetMapping)
    }
}
