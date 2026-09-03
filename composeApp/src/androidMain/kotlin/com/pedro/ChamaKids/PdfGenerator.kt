package com.pedro.ChamaKids

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.core.content.FileProvider
import com.pedro.ChamaKids.data.MemberWithStarStats
import com.pedro.ChamaKids.data.MemberWithStats
import com.pedro.ChamaKids.data.PeriodStat
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

actual object PdfGenerator {
    private var context: Context? = null

    fun setContext(ctx: Context) {
        context = ctx
    }

    actual fun gerarEPrompt(
        mesAno: String,
        membroMaisPresente: MemberWithStats?,
        melhorComportamento: MemberWithStarStats?,
        diaMaiorAssiduidade: PeriodStat?,
        diaMaisEstrelas: PeriodStat?
    ) {
        val ctx = context ?: return
        val file = gerarRelatorioComoArquivo(ctx, mesAno, membroMaisPresente, melhorComportamento, diaMaiorAssiduidade, diaMaisEstrelas)
        if (file != null) {
            salvarPdfDefinitivo(ctx, file, mesAno)
        }
    }

    private fun gerarRelatorioComoArquivo(
        context: Context,
        mesAno: String,
        membroMaisPresente: MemberWithStats?,
        melhorComportamento: MemberWithStarStats?,
        diaMaiorAssiduidade: PeriodStat?,
        diaMaisEstrelas: PeriodStat?
    ): File? {
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas: Canvas = page.canvas
        val paint = Paint()
        val titlePaint = Paint()

        titlePaint.textAlign = Paint.Align.CENTER
        titlePaint.textSize = 24f
        titlePaint.isFakeBoldText = true
        canvas.drawText("RELATÓRIO MENSAL - CHAMAKIDS", 297f, 50f, titlePaint)
        titlePaint.textSize = 18f
        titlePaint.isFakeBoldText = false
        canvas.drawText(mesAno, 297f, 80f, titlePaint)
        paint.color = Color.BLACK
        paint.strokeWidth = 2f
        canvas.drawLine(50f, 100f, 545f, 100f, paint)

        var yPos = 140f
        val step = 80f
        drawSeccao(canvas, "CRIANÇA MAIS PRESENTE", yPos)
        yPos += 30f
        val txtPresente = membroMaisPresente?.let { "${it.nome} - ${it.count} presenças" } ?: "Sem dados"
        canvas.drawText(txtPresente, 70f, yPos, paintNormal())
        yPos += step
        drawSeccao(canvas, "MELHOR COMPORTAMENTO", yPos)
        yPos += 30f
        val txtComportamento = melhorComportamento?.let { "${it.nome} - ${it.count} estrelas" } ?: "Sem dados"
        canvas.drawText(txtComportamento, 70f, yPos, paintNormal())
        if (melhorComportamento?.comentario != null) {
            yPos += 20f
            canvas.drawText("\"${melhorComportamento.comentario}\"", 80f, yPos, paintItalic())
        }
        yPos += step
        drawSeccao(canvas, "DIA COM MAIS CRIANÇAS", yPos)
        yPos += 30f
        val txtDiaAssiduidade = diaMaiorAssiduidade?.let { 
            "${SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(it.timestamp)} - ${it.count} crianças" 
        } ?: "Sem dados"
        canvas.drawText(txtDiaAssiduidade, 70f, yPos, paintNormal())
        yPos += step
        drawSeccao(canvas, "DIA COM MAIS ESTRELAS", yPos)
        yPos += 30f
        val txtDiaEstrelas = diaMaisEstrelas?.let { 
            "${SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(it.timestamp)} - ${it.count} estrelas" 
        } ?: "Sem dados"
        canvas.drawText(txtDiaEstrelas, 70f, yPos, paintNormal())

        pdfDocument.finishPage(page)
        return try {
            val file = File(context.cacheDir, "previa_relatorio.pdf")
            pdfDocument.writeTo(FileOutputStream(file))
            file
        } catch (_: Exception) {
            null
        } finally {
            pdfDocument.close()
        }
    }

    private fun salvarPdfDefinitivo(context: Context, arquivoCache: File, mesAno: String) {
        val nomeArquivo = "Relatorio_ChamaKids_${mesAno.replace(" ", "_")}.pdf"
        try {
            val uriFinal: Uri?
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, nomeArquivo)
                    put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                }
                uriFinal = context.contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
                if (uriFinal != null) {
                    context.contentResolver.openOutputStream(uriFinal)?.use { outputStream ->
                        arquivoCache.inputStream().use { it.copyTo(outputStream) }
                    }
                }
            } else {
                val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                val file = File(downloadsDir, nomeArquivo)
                arquivoCache.inputStream().use { input ->
                    FileOutputStream(file).use { output -> input.copyTo(output) }
                }
                uriFinal = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
            }

            if (uriFinal != null) {
                Toast.makeText(context, "Relatório salvo em Downloads!", Toast.LENGTH_SHORT).show()
                abrirPdf(context, uriFinal)
            }
        } catch (_: Exception) {
            Toast.makeText(context, "Erro ao salvar", Toast.LENGTH_SHORT).show()
        }
    }

    private fun abrirPdf(context: Context, uri: Uri) {
        try {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "application/pdf")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (_: Exception) { }
    }

    private fun drawSeccao(canvas: Canvas, titulo: String, y: Float) {
        val p = Paint().apply { color = Color.LTGRAY }
        canvas.drawRect(50f, y - 20f, 545f, y + 5f, p)
        val pt = Paint().apply { textSize = 14f; isFakeBoldText = true }
        canvas.drawText(titulo, 60f, y, pt)
    }

    private fun paintNormal() = Paint().apply { textSize = 14f; color = Color.BLACK }
    private fun paintItalic() = Paint().apply { textSize = 12f; color = Color.DKGRAY; textSkewX = -0.25f }
}
