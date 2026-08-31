package com.pedro.ChamaKids.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding

import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import android.graphics.BitmapFactory
import android.net.Uri

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape

import androidx.compose.runtime.remember

import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext

import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.ButtonDefaults
import com.pedro.ChamaKids.ui.theme.ChamaKidsAction

/*
 * Tela de membros cadastrados.
 *
 * Agora os dados vêm diretamente do Room,
 * através do MemberViewModel.
 */
@Composable
fun MembersScreen(
    onVoltar: () -> Unit,
    onAdicionarMembro: () -> Unit,
    onAbrirMembro: (Int) -> Unit,
    viewModel: MemberViewModel
) {

    val context = LocalContext.current

    /*
     * Observa a lista de membros ativos.
     *
     * Quando alguém é adicionado ao banco,
     * essa lista atualiza automaticamente.
     */
    val membros by
    viewModel.membros.collectAsState()

    ChamaKidsScreen(
        titulo = "MEMBROS",
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
            modifier =
                Modifier.height(24.dp)
        )


        // =====================================================
        // CASO NÃO EXISTA NENHUM MEMBRO
        // =====================================================

        if (membros.isEmpty()) {

            Text(
                text = "Nenhum membro cadastrado.",
                fontSize = 16.sp,
                color = Color.Gray
            )

        } else {


            // =================================================
            // LISTA REAL DO BANCO
            // =================================================

            membros.forEach { membro ->

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
                        .padding(
                            bottom = 12.dp
                        )
                        .clickable {

                            onAbrirMembro(
                                membro.id
                            )
                        },

                    shape =
                        RoundedCornerShape(
                            12.dp
                        ),

                    colors =
                        CardDefaults.cardColors(
                            containerColor =
                                Color(0xFFF1F1F5)
                        )
                ) {


                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),

                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {

                        // =====================================
                        // FOTO DO MEMBRO
                        // =====================================

                        Box(
                            modifier = Modifier
                                .size(52.dp)
                                .clip(CircleShape)
                                .background(
                                    Color(0xFFD9D9D9)
                                ),

                            contentAlignment = Alignment.Center
                        ) {

                            if (bitmapFoto != null) {

                                Image(
                                    bitmap = bitmapFoto.asImageBitmap(),
                                    contentDescription =
                                        "Foto de ${membro.nome}",

                                    modifier =
                                        Modifier.fillMaxSize(),

                                    contentScale =
                                        ContentScale.Crop
                                )

                            } else {

                                Text(
                                    text =
                                        membro.nome
                                            .firstOrNull()
                                            ?.uppercase()
                                            ?: "?",

                                    fontWeight =
                                        FontWeight.Bold,

                                    color =
                                        Color.Gray
                                )
                            }
                        }

                        Spacer(
                            modifier = Modifier.width(14.dp)
                        )

                        // =====================================
                        // NOME
                        // =====================================

                        Column(
                            modifier =
                                Modifier.weight(1f)
                        ) {

                            Text(
                                text = membro.nome,
                                fontSize = 18.sp,
                                fontWeight =
                                    FontWeight.SemiBold
                            )

                            Text(
                                text = "Abrir informações",
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        }


                        // =====================================
                        // SETA
                        // =====================================

                        Text(
                            text = ">",
                            fontSize = 24.sp
                        )
                    }
                }
            }
        }


        /*
         * Empurra o botão para baixo.
         */
        Spacer(
            modifier =
                Modifier.weight(1f)
        )


        // =====================================================
        // ADICIONAR MEMBRO
        // =====================================================

        Button(
            onClick =
                onAdicionarMembro,

            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),

            colors = ButtonDefaults.buttonColors(
                containerColor = ChamaKidsAction,
                contentColor = Color.Black
            ),

            border = BorderStroke(
                width = 1.5.dp,
                color = Color.Black
            )
        ) {

            Text(
                text = "ADICIONAR MEMBRO",
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
}