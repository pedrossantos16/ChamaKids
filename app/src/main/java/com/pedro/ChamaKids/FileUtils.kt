package com.pedro.ChamaKids

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

/**
 * Utilitário para salvar e tratar imagens permanentemente na memória interna do app.
 */
object FileUtils {

    /**
     * Salva a foto, redimensiona para economizar espaço e corrige a rotação.
     */
    fun salvarFotoInterna(context: Context, uri: Uri): String? {
        return try {
            val pasta = File(context.filesDir, "fotos_membros")
            if (!pasta.exists()) pasta.mkdirs()

            val nomeArquivo = "foto_${UUID.randomUUID()}.jpg"
            val arquivoDestino = File(pasta, nomeArquivo)

            // 1. Carrega o Bitmap original
            context.contentResolver.openInputStream(uri)?.use { input ->
                val bitmapOriginal = BitmapFactory.decodeStream(input) ?: return null

                // 2. Corrige a rotação baseada nos metadados (EXIF)
                val bitmapCorrigido = corrigirRotacao(context, uri, bitmapOriginal)

                // 3. Redimensiona para um tamanho máximo (ex: 500px)
                val bitmapTratado = redimensionar(bitmapCorrigido, 500)

                // 4. Salva com compressão JPEG (80% de qualidade é o ideal entre peso e visual)
                FileOutputStream(arquivoDestino).use { output ->
                    bitmapTratado.compress(Bitmap.CompressFormat.JPEG, 80, output)
                }

                // Limpa memória
                if (bitmapOriginal != bitmapTratado) bitmapOriginal.recycle()
                if (bitmapCorrigido != bitmapTratado) bitmapCorrigido.recycle()
                
                return arquivoDestino.absolutePath
            }
            null
        } catch (_: Exception) {
            null
        }
    }

    /**
     * Apaga um arquivo físico do celular se ele existir.
     */
    fun excluirArquivo(caminho: String?) {
        if (caminho.isNullOrBlank()) return
        try {
            val arquivo = File(caminho)
            if (arquivo.exists()) {
                arquivo.delete()
            }
        } catch (_: Exception) { }
    }

    private fun redimensionar(bitmap: Bitmap, maxTam: Int): Bitmap {
        val largura = bitmap.width
        val altura = bitmap.height

        if (largura <= maxTam && altura <= maxTam) return bitmap

        val proporcao = largura.toFloat() / altura.toFloat()
        val novaLargura: Int
        val novaAltura: Int

        if (proporcao > 1) {
            novaLargura = maxTam
            novaAltura = (maxTam / proporcao).toInt()
        } else {
            novaAltura = maxTam
            novaLargura = (maxTam * proporcao).toInt()
        }

        return Bitmap.createScaledBitmap(bitmap, novaLargura, novaAltura, true)
    }

    private fun corrigirRotacao(context: Context, uri: Uri, bitmap: Bitmap): Bitmap {
        return try {
            val input = context.contentResolver.openInputStream(uri) ?: return bitmap
            val exif = ExifInterface(input)
            val orientacao = exif.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL
            )
            input.close()

            val angulo = when (orientacao) {
                ExifInterface.ORIENTATION_ROTATE_90 -> 90f
                ExifInterface.ORIENTATION_ROTATE_180 -> 180f
                ExifInterface.ORIENTATION_ROTATE_270 -> 270f
                else -> 0f
            }

            if (angulo == 0f) return bitmap

            val matrix = Matrix()
            matrix.postRotate(angulo)
            Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        } catch (_: Exception) {
            bitmap
        }
    }
}
