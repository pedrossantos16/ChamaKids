package com.pedro.ChamaKids.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pedro.ChamaKids.data.UserEntity
import com.pedro.ChamaKids.ui.theme.ChamaKidsAction
import com.pedro.ChamaKids.ui.theme.ChamaKidsCard

@Composable
fun UsersScreen(
    onVoltar: () -> Unit,
    viewModel: UserViewModel
) {
    var nome by remember { mutableStateOf("") }
    var fraseSecreta by remember { mutableStateOf("") }
    var userToDelete by remember { mutableStateOf<UserEntity?>(null) }
    
    val usuarios by viewModel.usuarios.collectAsState()

    ChamaKidsScreen(
        titulo = "USUÁRIOS",
        onVoltar = onVoltar
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(modifier = Modifier.height(24.dp))
                
                Text(
                    text = "CADASTRAR NOVO",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                CampoFicha(valor = nome, titulo = "Nome do usuário", habilitado = true) { nome = it }
                Spacer(modifier = Modifier.height(12.dp))
                CampoFicha(valor = fraseSecreta, titulo = "Palavra/Frase Chave", habilitado = true) { fraseSecreta = it }
                
                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        if (nome.isNotBlank() && fraseSecreta.isNotBlank()) {
                            viewModel.cadastrarUsuario(nome, fraseSecreta)
                            nome = ""
                            fraseSecreta = ""
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(54.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ChamaKidsAction, contentColor = Color.Black),
                    border = BorderStroke(1.5.dp, Color.Black)
                ) {
                    Text(text = "ADICIONAR USUÁRIO", fontWeight = FontWeight.SemiBold)
                }

                Spacer(modifier = Modifier.height(40.dp))
                HorizontalDivider(color = Color.Black, thickness = 1.dp)
                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "USUÁRIOS CADASTRADOS",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                usuarios.forEach { user ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = ChamaKidsCard),
                        border = BorderStroke(1.dp, Color.Black)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = user.nome, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Text(
                                    text = if (user.bloqueado) "Status: BLOQUEADO" else "Frase: ****",
                                    fontSize = 14.sp,
                                    color = if (user.bloqueado) Color.Red else Color.Gray
                                )
                            }
                            if (user.nome != "ADMINISTRADOR") {
                                Button(
                                    onClick = { userToDelete = user },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text("EXCLUIR", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }

    if (userToDelete != null) {
        AlertDialog(
            onDismissRequest = { userToDelete = null },
            title = { Text("EXCLUIR USUÁRIO") },
            text = { Text("Tem certeza que deseja excluir o usuário '${userToDelete?.nome}'?") },
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
}
