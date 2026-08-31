package com.pedro.ChamaKids.ui

import androidx.compose.foundation.Canvas

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding

import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

import androidx.compose.ui.text.font.FontWeight

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.widget.Toast
import androidx.compose.foundation.layout.size
import androidx.compose.ui.platform.LocalContext

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.material3.Icon
import androidx.compose.foundation.shape.RoundedCornerShape

import android.graphics.BitmapFactory
import android.net.Uri

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import com.pedro.ChamaKids.ui.theme.ChamaKidsBlue
import com.pedro.ChamaKids.ui.theme.ChamaKidsHeaderGray

import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.ButtonDefaults
import com.pedro.ChamaKids.ui.theme.ChamaKidsAction
import com.pedro.ChamaKids.ui.theme.ChamaKidsCard

/*
 * MembroUi
 *
 * Modelo TEMPORÁRIO usado apenas para construir
 * e testar a interface.
 *
 * Ainda não representa dados salvos no banco.
 *
 * Futuramente ele será substituído/abastecido
 * pelos membros salvos no Room.
 */
data class MembroUi(

    val id: Int,

    val nome: String,

    /*
     * Representa a situação histórica
     * da frequência do membro.
     */
    val statusFrequencia: StatusFrequencia,

    /*
     * Representa somente a presença
     * na chamada ATUAL.
     */
    val presente: Boolean
)


/*
 * Classificação da frequência histórica.
 *
 * Posteriormente será calculada automaticamente
 * usando o histórico das chamadas.
 */
enum class StatusFrequencia {

    BOM,

    REGULAR,

    RUIM
}


/*
 * AttendanceScreen
 *
 * Tela usada para realizar uma chamada.
 */

@Composable
fun AttendanceScreen(
    onSalvar: () -> Unit,
    onVoltar: () -> Unit,
    memberViewModel: MemberViewModel,
    attendanceViewModel: AttendanceViewModel
) {

    val membrosBanco by
    memberViewModel.membros.collectAsState()

    val frequencias by
    attendanceViewModel
        .frequencias
        .collectAsState()

    var presencas by remember {
        mutableStateOf<Map<Int, Boolean>>(
            emptyMap()
        )
    }

    LaunchedEffect(membrosBanco) {

        presencas =
            membrosBanco.associate { membro ->
                membro.id to true
            }

        attendanceViewModel
            .carregarFrequencias(
                membrosBanco.map {
                    it.id
                }
            )
    }

    val context = LocalContext.current

    ChamaKidsScreen(
        titulo = "CHAMADA",
        onVoltar = onVoltar
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    start = 20.dp,
                    end = 20.dp,
                    bottom = 36.dp
                )
        ) {

            Spacer(
                modifier = Modifier.height(24.dp)
            )

            // =====================================================
            // LISTA DE MEMBROS
            // =====================================================

            membrosBanco.forEach { membro ->

                val presente =
                    presencas[membro.id] ?: true

                val frequencia =
                    frequencias[membro.id]

                val bitmapFoto = remember(membro.fotoUri) {

                    if (membro.fotoUri.isNullOrBlank()) {

                        null

                    } else {

                        try {

                            context.contentResolver
                                .openInputStream(
                                    Uri.parse(membro.fotoUri)
                                )
                                ?.use { inputStream ->

                                    BitmapFactory.decodeStream(
                                        inputStream
                                    )
                                }

                        } catch (e: Exception) {

                            null
                        }
                    }
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(92.dp),

                    shape = RoundedCornerShape(12.dp)
                ) {

                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(
                                horizontal = 14.dp,
                                vertical = 10.dp
                            ),

                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        // FOTO
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(Color.LightGray),

                            contentAlignment = Alignment.Center
                        ) {

                            if (bitmapFoto != null) {

                                Image(
                                    bitmap = bitmapFoto.asImageBitmap(),
                                    contentDescription = "Foto de ${membro.nome}",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )

                            } else {

                                Text(
                                    text = membro.nome
                                        .firstOrNull()
                                        ?.uppercase()
                                        ?: "?",
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Gray
                                )
                            }
                        }

                        Spacer(
                            modifier = Modifier.width(12.dp)
                        )

                        // NOME + FREQUÊNCIA
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {

                            Text(
                                text = membro.nome,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Medium,
                                maxLines = 2
                            )

                            Spacer(
                                modifier = Modifier.height(5.dp)
                            )

                            Canvas(
                                modifier = Modifier.size(14.dp)
                            ) {

                                val cor =
                                    when {
                                        frequencia == null ->
                                            Color.LightGray

                                        frequencia >= 75f ->
                                            Color(0xFF39FF14)

                                        frequencia >= 50f ->
                                            Color(0xFFFFD600)

                                        else ->
                                            Color(0xFFFF1744)
                                    }

                                drawCircle(
                                    color = cor
                                )
                            }
                        }

                        Spacer(
                            modifier = Modifier.width(10.dp)
                        )

                        // PRESENTE / FALTOU
                        Switch(
                            checked = presente,

                            onCheckedChange = { novoEstado ->
                                presencas =
                                    presencas
                                        .toMutableMap()
                                        .apply {
                                            this[membro.id] =
                                                novoEstado
                                        }
                            }
                        )
                    }
                }

                Spacer(
                    modifier = Modifier.height(15.dp)
                )
            }

            /*
         * Empurra o botão SALVAR para
         * a parte inferior da tela.
         */
            Spacer(
                modifier =
                    Modifier.weight(1f)
            )


            // =====================================================
            // SALVAR CHAMADA
            // =====================================================

            Button(

                /*
             * 1. criaremos uma Chamada;
             * 2. salvaremos uma Presenca para cada membro;
             * 3. retornaremos à Home.
             */
                onClick = {

                    if (membrosBanco.isNotEmpty()) {

                        attendanceViewModel.salvarChamada(
                            presencas = presencas,
                            onSucesso = {

                                Toast.makeText(
                                    context,
                                    "Chamada salva com sucesso!",
                                    Toast.LENGTH_SHORT
                                ).show()

                                onSalvar()
                            }
                        )
                    }
                },

                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),

                colors =
                    ButtonDefaults.buttonColors(
                        containerColor = ChamaKidsAction,
                        contentColor = Color.Black
                    ),

                border = BorderStroke(
                    width = 1.5.dp,
                    color = Color.Black
                )

            ) {

                Text(
                    text = "SALVAR CHAMADA"
                )
            }
        }
    }
}
