package com.pedro.ChamaKids.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.material3.*

import androidx.compose.runtime.*

import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp

import com.pedro.ChamaKids.data.MemberEntity
import com.pedro.ChamaKids.ui.theme.ChamaKidsBlue
import com.pedro.ChamaKids.ui.theme.ChamaKidsHeaderGray

import android.content.Intent
import android.net.Uri

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.VisualTransformation

import android.graphics.BitmapFactory
import androidx.compose.foundation.BorderStroke

import androidx.compose.foundation.Image
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import com.pedro.ChamaKids.ui.theme.ChamaKidsAction
import com.pedro.ChamaKids.ui.theme.ChamaKidsCard


@Composable
fun MemberDetailScreen(
    membroId: Int,
    viewModel: MemberViewModel,
    onVoltar: () -> Unit
) {
    val context = LocalContext.current

    var membroOriginal by remember {
        mutableStateOf<MemberEntity?>(null)
    }

    var editando by remember {
        mutableStateOf(false)
    }

    var nome by remember {
        mutableStateOf("")
    }

    var cpf by remember {
        mutableStateOf("")
    }

    var rg by remember {
        mutableStateOf("")
    }

    var dataNascimento by remember {
        mutableStateOf("")
    }

    var endereco by remember {
        mutableStateOf("")
    }

    var celularMembro by remember {
        mutableStateOf("")
    }

    var telefone by remember {
        mutableStateOf("")
    }

    var nomePai by remember {
        mutableStateOf("")
    }

    var celularPai by remember {
        mutableStateOf("")
    }

    var nomeMae by remember {
        mutableStateOf("")
    }

    var celularMae by remember {
        mutableStateOf("")
    }

    var fotoUri by remember {
        mutableStateOf<String?>(null)
    }

    val seletorFoto =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.OpenDocument()
        ) { uri ->

            if (uri != null) {

                try {
                    context.contentResolver
                        .takePersistableUriPermission(
                            uri,
                            Intent.FLAG_GRANT_READ_URI_PERMISSION
                        )
                } catch (_: SecurityException) {
                }

                fotoUri = uri.toString()
            }
        }


    // =====================================================
    // CARREGA O MEMBRO
    // =====================================================

    LaunchedEffect(membroId) {

        val membro =
            viewModel.buscarMembroPorId(
                membroId
            )

        if (membro != null) {

            membroOriginal = membro

            nome = membro.nome
            cpf = membro.cpf
            rg = membro.rg
            dataNascimento =
                membro.dataNascimento ?: ""

            endereco = membro.endereco
            celularMembro =
                membro.celularMembro
            telefone = membro.telefone

            nomePai = membro.nomePai
            celularPai = membro.celularPai

            nomeMae = membro.nomeMae
            celularMae = membro.celularMae

            fotoUri = membro.fotoUri
        }
    }


    if (membroOriginal == null) {

        Box(
            modifier = Modifier.fillMaxSize()
        ) {

            Text(
                text = "Carregando...",
                modifier = Modifier.padding(20.dp)
            )
        }

        return
    }


    ChamaKidsScreen(
        titulo = "INFORMAÇÕES",
        onVoltar = onVoltar,
        mostrarVoltar = !editando
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(
                    rememberScrollState()
                )
                .padding(20.dp)
        ) {

            // =================================================
            // CONTEÚDO ROLÁVEL
            // =================================================

            val bitmapFoto = remember(fotoUri) {

                if (fotoUri.isNullOrBlank()) {

                    null

                } else {

                    try {

                        context.contentResolver
                            .openInputStream(
                                Uri.parse(fotoUri)
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

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {

                    Card(
                        modifier = Modifier
                            .size(180.dp)
                            .clickable(
                                enabled = editando
                            ) {
                                seletorFoto.launch(
                                    arrayOf("image/*")
                                )
                            },

                        shape = RoundedCornerShape(16.dp),

                        colors = CardDefaults.cardColors(
                            containerColor =
                                if (editando) {
                                    Color.White
                                } else {
                                    Color(0xFFE2E2E2)
                                }
                        )
                    ) {

                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {

                            if (bitmapFoto != null) {

                                Image(
                                    bitmap = bitmapFoto.asImageBitmap(),
                                    contentDescription = "Foto do membro",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )

                                if (editando) {

                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(
                                                Color.Black.copy(
                                                    alpha = 0.35f
                                                )
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {

                                        Text(
                                            text = "ALTERAR FOTO",
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                            } else {

                                Text(
                                    text =
                                        if (editando) {
                                            "+ FOTO"
                                        } else {
                                            "SEM FOTO"
                                        }
                                )
                            }
                        }
                    }
                }

                Spacer(
                    modifier = Modifier.height(20.dp)
                )


                CampoFicha(
                    valor = nome,
                    titulo = "Nome completo",
                    habilitado = editando
                ) {
                    nome = it
                }


                Spacer(
                    modifier = Modifier.height(12.dp)
                )


                CampoFicha(
                    valor = cpf,
                    titulo = "CPF",
                    habilitado = editando,
                    keyboardType = KeyboardType.Number,

                    visualTransformation =
                        if (editando) {
                            CpfVisualTransformation()
                        } else {
                            VisualTransformation.None
                        }
                ) { valor ->

                    cpf = valor
                        .filter { it.isDigit() }
                        .take(11)
                }


                Spacer(
                    modifier = Modifier.height(12.dp)
                )


                Row(
                    modifier =
                        Modifier.fillMaxWidth()
                ) {

                    CampoFicha(
                        valor = rg,
                        titulo = "RG",
                        habilitado = editando,
                        modifier =
                            Modifier.weight(1f)
                    ) {
                        rg = it
                    }

                    Spacer(
                        modifier =
                            Modifier.width(10.dp)
                    )

                    CampoFicha(
                        valor = dataNascimento,
                        titulo = "Nascimento",
                        habilitado = editando,
                        modifier =
                            Modifier.weight(1f)
                    ) {
                        dataNascimento = it
                    }
                }


                Spacer(
                    modifier = Modifier.height(12.dp)
                )


                CampoFicha(
                    valor = endereco,
                    titulo = "Endereço completo",
                    habilitado = editando
                ) {
                    endereco = it
                }


                Spacer(
                    modifier = Modifier.height(12.dp)
                )


                Row(
                    modifier =
                        Modifier.fillMaxWidth()
                ) {

                    CampoFicha(
                        valor = celularMembro,
                        titulo = "Cel. da criança",
                        habilitado = editando,
                        modifier =
                            Modifier.weight(1f)
                    ) {
                        celularMembro = it
                    }

                    Spacer(
                        modifier =
                            Modifier.width(10.dp)
                    )

                    CampoFicha(
                        valor = telefone,
                        titulo = "Telefone",
                        habilitado = editando,
                        modifier =
                            Modifier.weight(1f)
                    ) {
                        telefone = it
                    }
                }


                Spacer(
                    modifier = Modifier.height(12.dp)
                )


                Row(
                    modifier =
                        Modifier.fillMaxWidth()
                ) {

                    CampoFicha(
                        valor = nomePai,
                        titulo = "Nome do pai",
                        habilitado = editando,
                        modifier =
                            Modifier.weight(1f)
                    ) {
                        nomePai = it
                    }

                    Spacer(
                        modifier =
                            Modifier.width(10.dp)
                    )

                    CampoFicha(
                        valor = celularPai,
                        titulo = "Cel. do pai",
                        habilitado = editando,
                        modifier =
                            Modifier.weight(1f)
                    ) {
                        celularPai = it
                    }
                }


                Spacer(
                    modifier = Modifier.height(12.dp)
                )


                Row(
                    modifier =
                        Modifier.fillMaxWidth()
                ) {

                    CampoFicha(
                        valor = nomeMae,
                        titulo = "Nome da mãe",
                        habilitado = editando,
                        modifier =
                            Modifier.weight(1f)
                    ) {
                        nomeMae = it
                    }

                    Spacer(
                        modifier =
                            Modifier.width(10.dp)
                    )

                    CampoFicha(
                        valor = celularMae,
                        titulo = "Cel. da mãe",
                        habilitado = editando,
                        modifier =
                            Modifier.weight(1f)
                    ) {
                        celularMae = it
                    }
                }


                Spacer(
                    modifier = Modifier.height(30.dp)
                )


                // =================================================
                // BOTÕES
                // =================================================

                if (!editando) {

                    Button(
                        onClick = {
                            editando = true
                        },

                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),

                        colors =
                            ButtonDefaults
                                .buttonColors(
                                    containerColor =
                                        Color.White,
                                    contentColor =
                                        Color.Black
                                ),

                        border = BorderStroke(
                            width = 1.5.dp,
                            color = Color.Black
                        )
                    ) {

                        Text("EDITAR")
                    }


                    Spacer(
                        modifier =
                            Modifier.height(12.dp)
                    )


                    Button(
                        onClick = {

                            viewModel.inativarMembro(
                                membroId
                            )

                            onVoltar()
                        },

                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),

                        colors =
                            ButtonDefaults
                                .buttonColors(
                                    containerColor =
                                        Color(0xFFD32F2F),
                                    contentColor =
                                        Color.Black
                                ),

                        border = BorderStroke(
                            width = 1.5.dp,
                            color = Color.Black
                    )


                    ) {

                        Text("EXCLUIR")
                    }

                } else {


                    Button(
                        onClick = {

                            val atualizado =
                                membroOriginal!!.copy(
                                    nome =
                                        nome.trim(),
                                    cpf = cpf,
                                    rg = rg,
                                    dataNascimento =
                                        dataNascimento
                                            .ifBlank {
                                                null
                                            },
                                    endereco =
                                        endereco,
                                    celularMembro =
                                        celularMembro,
                                    telefone =
                                        telefone,
                                    nomePai =
                                        nomePai,
                                    celularPai =
                                        celularPai,
                                    nomeMae =
                                        nomeMae,
                                    celularMae =
                                        celularMae,
                                    fotoUri =
                                        fotoUri
                                )

                            viewModel
                                .atualizarMembro(
                                    atualizado
                                )

                            membroOriginal =
                                atualizado

                            editando =
                                false
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

                        Text("SALVAR")
                    }


                    Spacer(
                        modifier =
                            Modifier.height(12.dp)
                    )


                    OutlinedButton(
                        onClick = {

                            val original =
                                membroOriginal!!

                            nome =
                                original.nome

                            cpf =
                                original.cpf

                            rg =
                                original.rg

                            dataNascimento =
                                original
                                    .dataNascimento
                                    ?: ""

                            endereco =
                                original.endereco

                            celularMembro =
                                original
                                    .celularMembro

                            telefone =
                                original.telefone

                            nomePai =
                                original.nomePai

                            celularPai =
                                original.celularPai

                            nomeMae =
                                original.nomeMae

                            celularMae =
                                original.celularMae

                            fotoUri =
                                original.fotoUri

                            editando =
                                false
                        },

                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),

                        colors =
                            ButtonDefaults.buttonColors(
                                containerColor = ChamaKidsCard,
                                contentColor = Color.Black
                            ),

                        border = BorderStroke(
                            width = 1.5.dp,
                            color = Color.Black
                        )
                    ) {

                        Text("DESCARTAR")
                    }
                }
            }
        }
    }

@Composable
fun CampoFicha(
    valor: String,
    titulo: String,
    habilitado: Boolean,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
    visualTransformation: VisualTransformation =
        VisualTransformation.None,
    onChange: (String) -> Unit
) {

    OutlinedTextField(
        value = valor,

        onValueChange =
            onChange,

        enabled =
            habilitado,

        label = {
            Text(titulo)
        },

        keyboardOptions =
            KeyboardOptions(
                keyboardType =
                    keyboardType
            ),

        visualTransformation =
            visualTransformation,

        colors =
            OutlinedTextFieldDefaults
                .colors(

                    focusedContainerColor =
                        Color.White,

                    unfocusedContainerColor =
                        Color.White,

                    disabledContainerColor =
                        Color(
                            0xFFE2E2E2
                        ),

                    disabledTextColor =
                        Color(
                            0xFF666666
                        ),

                    disabledBorderColor =
                        Color(
                            0xFFAAAAAA
                        )
                ),

        modifier =
            modifier.fillMaxWidth()
    )
}