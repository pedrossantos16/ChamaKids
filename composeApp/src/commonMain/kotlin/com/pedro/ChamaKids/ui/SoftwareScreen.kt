package com.pedro.ChamaKids.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pedro.ChamaKids.AppVersion
import com.pedro.ChamaKids.data.UserEntity

@Composable
fun SoftwareScreen(
    onVoltar: () -> Unit,
    viewModel: UserViewModel
) {
    val isFrozen by viewModel.isFrozen.collectAsState()
    val usuarios by viewModel.usuarios.collectAsState()
    var showResetConfirm by remember { mutableStateOf(false) }
    var userToDelete by remember { mutableStateOf<UserEntity?>(null) }

    ChamaKidsScreen(
        titulo = "SOFTWARE",
        onVoltar = onVoltar
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF121212)) // Fundo escuro tecnológico
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            // VERSÃO DO APP
            Card(
                modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
                border = BorderStroke(1.dp, Color(0xFF333333))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("SISTEMA OPERACIONAL", color = Color(0xFF00FF41), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Versão: ${AppVersion.VERSION_NAME} (Code ${AppVersion.VERSION_CODE})", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
                    Text("Status: Online / Sincronizado", color = Color.Gray, fontSize = 12.sp)
                }
            }

            // CONTROLE GLOBAL
            Text("CONTROLE DE ACESSO", color = Color.Gray, fontSize = 14.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 12.dp))
            
            Card(
                modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Congelar Aplicativo", color = Color.White, fontWeight = FontWeight.Bold)
                        Text("Impede o uso por outros operadores", color = Color.Gray, fontSize = 12.sp)
                    }
                    Switch(
                        checked = isFrozen,
                        onCheckedChange = { viewModel.toggleFreezeApp() },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF00FF41))
                    )
                }
            }

            // GESTÃO DE USUÁRIOS
            Text("OPERADORES CADASTRADOS", color = Color.Gray, fontSize = 14.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 12.dp))
            usuarios.forEach { user ->
                if (user.nome != "ADMINISTRADOR") {
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
                        border = BorderStroke(1.dp, if (user.bloqueado) Color.Red else Color(0xFF333333))
                    ) {
                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(user.nome, color = Color.White, fontWeight = FontWeight.Bold)
                                Text(if (user.bloqueado) "BLOQUEADO" else "ATIVO", color = if (user.bloqueado) Color.Red else Color(0xFF00FF41), fontSize = 11.sp)
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(
                                    onClick = { viewModel.toggleUserBlock(user) },
                                    colors = ButtonDefaults.buttonColors(containerColor = if (user.bloqueado) Color.DarkGray else Color(0xFFD32F2F)),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(if (user.bloqueado) "DESBLOQUEAR" else "BLOQUEAR", fontSize = 10.sp)
                                }
                                Button(
                                    onClick = { userToDelete = user },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8B0000)),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text("EXCLUIR", fontSize = 10.sp, color = Color.White)
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // RESET DE FÁBRICA
            Button(
                onClick = { showResetConfirm = true },
                modifier = Modifier.fillMaxWidth().height(60.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF330000), contentColor = Color.Red),
                border = BorderStroke(2.dp, Color.Red),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("RESTAURAR PADRÃO DE FÁBRICA", fontWeight = FontWeight.ExtraBold)
            }
            Text(
                "Atenção: Esta ação apagará todos os dados da nuvem permanentemente.",
                color = Color.Gray, fontSize = 11.sp, textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 60.dp)
            )
        }
    }

    if (userToDelete != null) {
        AlertDialog(
            onDismissRequest = { userToDelete = null },
            title = { Text("CONFIRMAR EXCLUSÃO") },
            text = { Text("Deseja mesmo excluir o usuário '${userToDelete?.nome}'? Esta ação não pode ser desfeita.") },
            confirmButton = {
                Button(
                    onClick = {
                        userToDelete?.let { viewModel.excluirUsuario(it) }
                        userToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) { Text("EXCLUIR") }
            },
            dismissButton = {
                TextButton(onClick = { userToDelete = null }) { Text("CANCELAR") }
            }
        )
    }

    if (showResetConfirm) {
        AlertDialog(
            onDismissRequest = { showResetConfirm = false },
            title = { Text("CONFIRMAÇÃO CRÍTICA") },
            text = { Text("Deseja mesmo apagar todos os membros, chamadas e usuários do banco de dados? Esta ação não pode ser desfeita.") },
            confirmButton = {
                Button(
                    onClick = { 
                        viewModel.factoryReset()
                        showResetConfirm = false
                        onVoltar()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) { Text("APAGAR TUDO") }
            },
            dismissButton = {
                TextButton(onClick = { showResetConfirm = false }) { Text("CANCELAR") }
            }
        )
    }
}
