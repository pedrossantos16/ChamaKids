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
    var userToEdit by remember { mutableStateOf<UserEntity?>(null) }
    
    val usuarios by viewModel.usuarios.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()

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
                    val isOwnUser = currentUser != null && (user.serverId == currentUser?.serverId || user.nome == currentUser?.nome)
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
                            if (isOwnUser) {
                                Button(
                                    onClick = { userToEdit = user },
                                    colors = ButtonDefaults.buttonColors(containerColor = ChamaKidsAction),
                                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 4.dp),
                                    shape = RoundedCornerShape(6.dp),
                                    border = BorderStroke(1.dp, Color.Black)
                                ) {
                                    Text("EDITAR", fontSize = 11.sp, color = Color.Black, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }

    if (userToEdit != null) {
        var editNome by remember(userToEdit) { mutableStateOf(userToEdit?.nome ?: "") }
        var editFrase by remember(userToEdit) { mutableStateOf(userToEdit?.fraseSecreta ?: "") }

        AlertDialog(
            onDismissRequest = { userToEdit = null },
            title = { Text("EDITAR USUÁRIO", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    OutlinedTextField(
                        value = editNome,
                        onValueChange = { editNome = it },
                        label = { Text("Nome do usuário") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = editFrase,
                        onValueChange = { editFrase = it },
                        label = { Text("Palavra/Frase Chave") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (editNome.isNotBlank() && editFrase.isNotBlank()) {
                            userToEdit?.let { u ->
                                viewModel.editarUsuario(u, editNome, editFrase)
                            }
                            userToEdit = null
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ChamaKidsAction, contentColor = Color.Black)
                ) {
                    Text("SALVAR", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { userToEdit = null }) {
                    Text("CANCELAR")
                }
            }
        )
    }
}
