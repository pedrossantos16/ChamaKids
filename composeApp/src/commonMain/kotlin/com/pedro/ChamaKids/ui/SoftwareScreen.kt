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

    val totalMembros by viewModel.totalMembros.collectAsState()
    val totalChamadas by viewModel.totalChamadas.collectAsState()
    val totalEstrelas by viewModel.totalEstrelas.collectAsState()
    val totalAcoes by viewModel.totalAcoes.collectAsState()
    val totalUsuarios by viewModel.totalUsuarios.collectAsState()

    var showResetConfirm by remember { mutableStateOf(false) }
    var userToDelete by remember { mutableStateOf<UserEntity?>(null) }

    var moduloParaLimpar by remember { mutableStateOf<Pair<String, String>?>(null) } // Pair(key, nomeModulo)
    var mostrarDialogNovoMembro by remember { mutableStateOf(false) }
    var nomeNovoMembro by remember { mutableStateOf("") }
    var mostrarDialogNovoUsuario by remember { mutableStateOf(false) }
    var nomeNovoUsuario by remember { mutableStateOf("") }
    var fraseNovoUsuario by remember { mutableStateOf("") }

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

            // CENTRAL DE MANIPULAÇÃO DE MÓDULOS / ABAS
            Text("CENTRAL DE CONTROLE DE MÓDULOS (ABAS)", color = Color.Gray, fontSize = 14.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 12.dp))

            CardModuloSoftware(
                icone = "👥",
                titulo = "Massa de Membros",
                contagemTexto = "$totalMembros membro(s) no sistema",
                onLimpar = { moduloParaLimpar = "membros" to "Membros" },
                onAdicionar = { mostrarDialogNovoMembro = true }
            )

            CardModuloSoftware(
                icone = "📋",
                titulo = "Chamadas de Presença",
                contagemTexto = "$totalChamadas chamada(s) registrada(s)",
                onLimpar = { moduloParaLimpar = "chamadas" to "Chamadas" }
            )

            CardModuloSoftware(
                icone = "⭐",
                titulo = "Estrelas e Ranking",
                contagemTexto = "$totalEstrelas estrela(s) atribuída(s)",
                onLimpar = { moduloParaLimpar = "estrelas" to "Estrelas" }
            )

            CardModuloSoftware(
                icone = "📜",
                titulo = "Histórico de Ações",
                contagemTexto = "$totalAcoes ação(ões) salvas no log",
                onLimpar = { moduloParaLimpar = "acoes" to "Histórico de Ações" }
            )

            CardModuloSoftware(
                icone = "👤",
                titulo = "Operadores / Usuários",
                contagemTexto = "$totalUsuarios usuário(s) no sistema",
                onLimpar = { moduloParaLimpar = "usuarios" to "Usuários/Operadores" },
                onAdicionar = { mostrarDialogNovoUsuario = true }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // GESTÃO DE OPERADORES INDIVIDUAIS
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

            // RESET DE FÁBRICA COMPLETO
            Button(
                onClick = { showResetConfirm = true },
                modifier = Modifier.fillMaxWidth().height(60.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF330000), contentColor = Color.Red),
                border = BorderStroke(2.dp, Color.Red),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("RESTAURAR PADRÃO DE FÁBRICA (GLOBAL)", fontWeight = FontWeight.ExtraBold)
            }
            Text(
                "Atenção: Esta ação apagará TODOS os dados de TODAS as abas da nuvem permanentemente.",
                color = Color.Gray, fontSize = 11.sp, textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 60.dp)
            )
        }
    }

    // DIÁLOGO DE CONFIRMAÇÃO DE LIMPEZA DE MÓDULO ESPECÍFICO
    if (moduloParaLimpar != null) {
        val (key, nomeModulo) = moduloParaLimpar!!
        AlertDialog(
            onDismissRequest = { moduloParaLimpar = null },
            title = { Text("LIMPAR MÓDULO $nomeModulo") },
            text = { Text("Tem certeza que deseja apagar TODOS os dados da aba '$nomeModulo'? Os demais dados de outras abas serão preservados.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.limparModulo(key, nomeModulo)
                        moduloParaLimpar = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) { Text("LIMPAR MÓDULO", fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = { moduloParaLimpar = null }) { Text("CANCELAR") }
            }
        )
    }

    // DIÁLOGO RÁPIDO PARA ADICIONAR MEMBRO VIA SOFTWARE
    if (mostrarDialogNovoMembro) {
        AlertDialog(
            onDismissRequest = { mostrarDialogNovoMembro = false },
            title = { Text("Cadastrar Membro (Central)") },
            text = {
                OutlinedTextField(
                    value = nomeNovoMembro,
                    onValueChange = { nomeNovoMembro = it },
                    label = { Text("Nome completo do membro") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (nomeNovoMembro.isNotBlank()) {
                            viewModel.cadastrarMembroDireto(nomeNovoMembro)
                            nomeNovoMembro = ""
                            mostrarDialogNovoMembro = false
                        }
                    }
                ) { Text("ADICIONAR") }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogNovoMembro = false }) { Text("CANCELAR") }
            }
        )
    }

    // DIÁLOGO RÁPIDO PARA ADICIONAR OPERADOR VIA SOFTWARE
    if (mostrarDialogNovoUsuario) {
        AlertDialog(
            onDismissRequest = { mostrarDialogNovoUsuario = false },
            title = { Text("Cadastrar Operador (Central)") },
            text = {
                Column {
                    OutlinedTextField(
                        value = nomeNovoUsuario,
                        onValueChange = { nomeNovoUsuario = it },
                        label = { Text("Nome do usuário/operador") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = fraseNovoUsuario,
                        onValueChange = { fraseNovoUsuario = it },
                        label = { Text("Palavra/Frase Chave") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (nomeNovoUsuario.isNotBlank() && fraseNovoUsuario.isNotBlank()) {
                            viewModel.cadastrarUsuario(nomeNovoUsuario, fraseNovoUsuario)
                            nomeNovoUsuario = ""
                            fraseNovoUsuario = ""
                            mostrarDialogNovoUsuario = false
                        }
                    }
                ) { Text("ADICIONAR") }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogNovoUsuario = false }) { Text("CANCELAR") }
            }
        )
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
            title = { Text("CONFIRMAÇÃO CRÍTICA GLOBAL") },
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

@Composable
private fun CardModuloSoftware(
    icone: String,
    titulo: String,
    contagemTexto: String,
    onLimpar: () -> Unit,
    onAdicionar: (() -> Unit)? = null
) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
        border = BorderStroke(1.dp, Color(0xFF333333))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(icone, fontSize = 22.sp, modifier = Modifier.padding(end = 10.dp))
                    Column {
                        Text(titulo, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text(contagemTexto, color = Color(0xFF00FF41), fontSize = 12.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                if (onAdicionar != null) {
                    Button(
                        onClick = onAdicionar,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0288D1)),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        Text("➕ ADICIONAR", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }

                Button(
                    onClick = onLimpar,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8B0000)),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    Text("🗑️ LIMPAR MÓDULO", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}
