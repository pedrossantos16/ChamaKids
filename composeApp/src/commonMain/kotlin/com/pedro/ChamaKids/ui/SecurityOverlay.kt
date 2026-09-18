package com.pedro.ChamaKids.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pedro.ChamaKids.DeviceIdentifier
import com.pedro.ChamaKids.data.UserEntity
import kotlinx.coroutines.delay

@Composable
fun SecurityOverlay(
    viewModel: UserViewModel
) {
    val usuarios by viewModel.usuarios.collectAsState()
    val isBlocked by viewModel.isBlocked.collectAsState()
    val remainingTime by viewModel.remainingTime.collectAsState()
    val securityState by viewModel.securityState.collectAsState()

    var selectedUser by remember { mutableStateOf<UserEntity?>(null) }
    var randomizedOptions by remember { mutableStateOf<List<String>>(emptyList()) }
    var temporaryError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(temporaryError) {
        if (temporaryError != null) {
            delay(3000)
            temporaryError = null
        }
    }

    // Efeito de escurecimento (Filme)
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.7f))
            .clickable(enabled = false) {}, // Impede clique no fundo
        contentAlignment = Alignment.Center
    ) {
        if (isBlocked) {
            JanelaSeguranca(
                titulo = "",
                tituloCustom = {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = "APLICATIVO\nBLOQUEADO",
                            fontSize = 30.sp,
                            lineHeight = 34.sp,
                            fontWeight = FontWeight.ExtraBold,
                            fontStyle = FontStyle.Italic,
                            color = Color.Black,
                            textAlign = TextAlign.Center,
                            style = TextStyle(drawStyle = Stroke(width = 6f))
                        )
                        Text(
                            text = "APLICATIVO\nBLOQUEADO",
                            fontSize = 30.sp,
                            lineHeight = 34.sp,
                            fontWeight = FontWeight.ExtraBold,
                            fontStyle = FontStyle.Italic,
                            color = Color.Red,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Por motivos de segurança, o aplicativo foi bloqueado temporariamente.",
                        fontSize = 16.sp,
                        color = Color.White,
                        modifier = Modifier.padding(16.dp),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "Tente novamente em:",
                        fontSize = 14.sp,
                        color = Color.LightGray
                    )
                    Text(
                        text = remainingTime,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        } else if (selectedUser == null) {
            JanelaSeguranca(titulo = "Selecione seu usuário") {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    usuarios.forEach { user ->
                        CardUsuario(nome = user.nome) {
                            selectedUser = user
                            randomizedOptions = viewModel.gerarOpcoesAleatorias(user.fraseSecreta)
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        } else {
            JanelaSeguranca(titulo = "QUAL É A SENHA?") {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    randomizedOptions.forEach { opcao ->
                        CardUsuario(nome = opcao) {
                            if (opcao == selectedUser?.fraseSecreta) {
                                viewModel.resetFalhas()
                                viewModel.autenticar(selectedUser!!)
                            } else {
                                val falhas = (securityState?.falhasConsecutivas ?: 0) + 1
                                viewModel.registrarFalha()
                                temporaryError = "SENHA INCORRETA!\nCaso erre ${3 - falhas} vezes o aplicativo será bloqueado."
                                
                                if (falhas >= 3) {
                                    DeviceIdentifier.closeApp()
                                } else {
                                    selectedUser = null
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "Cancelar",
                        color = Color.LightGray,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .padding(bottom = 16.dp)
                            .clickable { selectedUser = null }
                    )
                }
            }
        }

        // Mensagem de Erro Temporária
        if (temporaryError != null) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 40.dp)
                    .fillMaxWidth(0.8f)
                    .background(Color.Red.copy(alpha = 0.9f), RoundedCornerShape(12.dp))
                    .border(2.dp, Color.Black, RoundedCornerShape(12.dp))
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = temporaryError!!,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    fontSize = 15.sp
                )
            }
        }
    }
}

@Composable
fun JanelaSeguranca(
    titulo: String,
    tituloCustom: (@Composable () -> Unit)? = null,
    conteudo: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth(0.85f)
            .fillMaxHeight(0.7f)
            .background(Color(0xFF457B88), RoundedCornerShape(24.dp))
            .border(4.dp, Color.Black, RoundedCornerShape(24.dp))
            .padding(16.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(bottom = 24.dp)
            ) {
                if (tituloCustom != null) {
                    tituloCustom()
                } else {
                    Text(
                        text = titulo,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold,
                        fontStyle = FontStyle.Italic,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                }
            }
            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                conteudo()
            }
        }
    }
}

@Composable
fun CardUsuario(nome: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .height(60.dp)
            .border(2.dp, Color.Black, RoundedCornerShape(30.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(30.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                text = nome,
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                fontStyle = FontStyle.Italic,
                color = Color.Black,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 12.dp)
            )
        }
    }
}
