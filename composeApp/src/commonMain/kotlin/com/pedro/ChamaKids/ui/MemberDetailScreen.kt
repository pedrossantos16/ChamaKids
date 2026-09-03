package com.pedro.ChamaKids.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pedro.ChamaKids.FileUtils
import com.pedro.ChamaKids.data.MemberEntity
import com.pedro.ChamaKids.ui.theme.ChamaKidsAction
import com.pedro.ChamaKids.ui.theme.ChamaKidsCard

@Composable
fun MemberDetailScreen(
    membroId: Int,
    viewModel: MemberViewModel,
    onVoltar: () -> Unit
) {
    fun formatarDataBR(data: String?): String {
        if (data.isNullOrBlank()) return "Não informada"
        return if (data.contains("-")) {
            try {
                val partes = data.split("-")
                "${partes[2]}/${partes[1]}/${partes[0]}"
            } catch (e: Exception) { data }
        } else data
    }

    var membroOriginal by remember { mutableStateOf<MemberEntity?>(null) }
    var editando by remember { mutableStateOf(false) }
    var nome by remember { mutableStateOf("") }
    var cpf by remember { mutableStateOf("") }
    var rg by remember { mutableStateOf("") }
    var dataNascimento by remember { mutableStateOf("") }
    var endereco by remember { mutableStateOf("") }
    var celularMembro by remember { mutableStateOf("") }
    var telefone by remember { mutableStateOf("") }
    var nomePai by remember { mutableStateOf("") }
    var celularPai by remember { mutableStateOf("") }
    var nomeMae by remember { mutableStateOf("") }
    var celularMae by remember { mutableStateOf("") }
    var fotoUri by remember { mutableStateOf<String?>(null) }

    /*
    // TODO: Implementar seletor de foto KMP
    // No Android era usado rememberLauncherForActivityResult
    */

    LaunchedEffect(membroId) {
        val membro = viewModel.buscarMembroPorId(membroId)
        if (membro != null) {
            membroOriginal = membro
            nome = membro.nome
            cpf = membro.cpf
            rg = membro.rg
            dataNascimento = formatarDataBR(membro.dataNascimento)
            endereco = membro.endereco
            celularMembro = membro.celularMembro
            telefone = membro.telefone
            nomePai = membro.nomePai
            celularPai = membro.celularPai
            nomeMae = membro.nomeMae
            celularMae = membro.celularMae
            fotoUri = membro.fotoUri
        }
    }

    if (membroOriginal == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = "Carregando...", modifier = Modifier.padding(20.dp))
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
                .padding(horizontal = 20.dp)
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {
                if (editando) {
                    Box(modifier = Modifier.fillMaxWidth().height(200.dp).padding(top = 20.dp), contentAlignment = Alignment.Center) {
                        Card(
                            modifier = Modifier.size(180.dp).clickable { 
                                // TODO: Chamar seletor de foto KMP
                            },
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                MemberImage(
                                    fotoUri = fotoUri,
                                    modifier = Modifier.fillMaxSize(),
                                    placeholderText = "+ FOTO"
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    CampoFicha(valor = nome, titulo = "Nome completo", habilitado = true) { nome = it }
                    Spacer(modifier = Modifier.height(12.dp))
                    CampoFicha(
                        valor = cpf,
                        titulo = "CPF",
                        habilitado = true,
                        keyboardType = KeyboardType.Number,
                        visualTransformation = CpfVisualTransformation()
                    ) { valor -> cpf = valor.filter { it.isDigit() }.take(11) }
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(modifier = Modifier.fillMaxWidth()) {
                        CampoFicha(
                            valor = rg,
                            titulo = "RG",
                            habilitado = true,
                            modifier = Modifier.weight(1f),
                            keyboardType = KeyboardType.Number,
                            visualTransformation = RgVisualTransformation()
                        ) { valor -> rg = valor.filter { it.isDigit() }.take(9) }
                        Spacer(modifier = Modifier.width(10.dp))
                        CampoFicha(valor = dataNascimento, titulo = "Nascimento", habilitado = true, modifier = Modifier.weight(1f)) { dataNascimento = it }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    CampoFicha(valor = endereco, titulo = "Endereço completo", habilitado = true) { endereco = it }
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(modifier = Modifier.fillMaxWidth()) {
                        CampoFicha(
                            valor = celularMembro,
                            titulo = "Cel. da criança",
                            habilitado = true,
                            modifier = Modifier.weight(1f),
                            keyboardType = KeyboardType.Number,
                            visualTransformation = CellphoneVisualTransformation()
                        ) { valor -> celularMembro = valor.filter { it.isDigit() }.take(11) }
                        Spacer(modifier = Modifier.width(10.dp))
                        CampoFicha(
                            valor = telefone,
                            titulo = "Telefone",
                            habilitado = true,
                            modifier = Modifier.weight(1f),
                            keyboardType = KeyboardType.Number,
                            visualTransformation = PhoneVisualTransformation()
                        ) { valor -> telefone = valor.filter { it.isDigit() }.take(10) }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(modifier = Modifier.fillMaxWidth()) {
                        CampoFicha(valor = nomePai, titulo = "Nome do pai", habilitado = true, modifier = Modifier.weight(1f)) { nomePai = it }
                        Spacer(modifier = Modifier.width(10.dp))
                        CampoFicha(
                            valor = celularPai,
                            titulo = "Cel. do pai",
                            habilitado = true,
                            modifier = Modifier.weight(1f),
                            keyboardType = KeyboardType.Number,
                            visualTransformation = CellphoneVisualTransformation()
                        ) { valor -> celularPai = valor.filter { it.isDigit() }.take(11) }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(modifier = Modifier.fillMaxWidth()) {
                        CampoFicha(valor = nomeMae, titulo = "Nome da mãe", habilitado = true, modifier = Modifier.weight(1f)) { nomeMae = it }
                        Spacer(modifier = Modifier.width(10.dp))
                        CampoFicha(
                            valor = celularMae,
                            titulo = "Cel. da mãe",
                            habilitado = true,
                            modifier = Modifier.weight(1f),
                            keyboardType = KeyboardType.Number,
                            visualTransformation = CellphoneVisualTransformation()
                        ) { valor -> celularMae = valor.filter { it.isDigit() }.take(11) }
                    }
                } else {
                    // MODO VISUALIZAÇÃO
                    Box(modifier = Modifier.size(180.dp).align(Alignment.CenterHorizontally).padding(top = 20.dp).clip(CircleShape).background(Color(0xFFD9D9D9)), contentAlignment = Alignment.Center) {
                        MemberImage(
                            fotoUri = fotoUri,
                            modifier = Modifier.fillMaxSize(),
                            placeholderText = nome.firstOrNull()?.uppercase() ?: "?"
                        )
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                    Text(text = nome, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.Black, modifier = Modifier.align(Alignment.CenterHorizontally))
                    Spacer(modifier = Modifier.height(30.dp))
                    InfoCard(label = "Data de Nascimento", value = dataNascimento)
                    Spacer(modifier = Modifier.height(12.dp))
                    InfoCard(label = "CPF", value = cpf.ifBlank { "---" })
                    Spacer(modifier = Modifier.height(12.dp))
                    InfoCard(label = "RG", value = if(rg.length == 9) "${rg.take(2)}.${rg.substring(2,5)}.${rg.substring(5,8)}-${rg.takeLast(1)}" else rg.ifBlank { "---" })
                    Spacer(modifier = Modifier.height(12.dp))
                    InfoCard(label = "Endereço", value = endereco.ifBlank { "---" })
                    Spacer(modifier = Modifier.height(12.dp))
                    val membroTelInfo = if (celularMembro.isNotBlank()) {
                        val cel = if(celularMembro.length == 11) "(${celularMembro.take(2)}) ${celularMembro.substring(2,7)}-${celularMembro.takeLast(4)}" else celularMembro
                        if (telefone.isNotBlank()) {
                            val fixo = if(telefone.length == 10) "(${telefone.take(2)}) ${telefone.substring(2,6)}-${telefone.takeLast(4)}" else telefone
                            "$cel ($fixo)"
                        } else cel
                    } else if (telefone.isNotBlank()) {
                        if(telefone.length == 10) "(${telefone.take(2)}) ${telefone.substring(2,6)}-${telefone.takeLast(4)}" else telefone
                    } else "---"
                    InfoCard(label = "Número do membro (tel)", value = membroTelInfo)
                    Spacer(modifier = Modifier.height(12.dp))
                    val paiInfo = if (nomePai.isNotBlank()) {
                        if (celularPai.isNotBlank()) {
                            val cel = if(celularPai.length == 11) "(${celularPai.take(2)}) ${celularPai.substring(2,7)}-${celularPai.takeLast(4)}" else celularPai
                            "$nomePai ($cel)"
                        } else nomePai
                    } else "---"
                    InfoCard(label = "Nome do pai", value = paiInfo)
                    Spacer(modifier = Modifier.height(12.dp))
                    val maeInfo = if (nomeMae.isNotBlank()) {
                        if (celularMae.isNotBlank()) {
                            val cel = if(celularMae.length == 11) "(${celularMae.take(2)}) ${celularMae.substring(2,7)}-${celularMae.takeLast(4)}" else celularMae
                            "$nomeMae ($cel)"
                        } else nomeMae
                    } else "---"
                    InfoCard(label = "Nome da mãe", value = maeInfo)
                }
                Spacer(modifier = Modifier.height(30.dp))
            }

            // BOTÕES FIXOS
            if (!editando) {
                Button(onClick = { editando = true }, modifier = Modifier.fillMaxWidth().padding(top = 10.dp).height(54.dp), colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Black), border = BorderStroke(1.5.dp, Color.Black)) { Text("EDITAR") }
                Spacer(modifier = Modifier.height(12.dp))
                Button(onClick = { viewModel.inativarMembro(membroId); onVoltar() }, modifier = Modifier.fillMaxWidth().padding(bottom = 45.dp).height(54.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F), contentColor = Color.White), border = BorderStroke(1.5.dp, Color.Black)) { Text("EXCLUIR") }
            } else {
                Button(onClick = {
                    val dataIso = if (dataNascimento.contains("/")) {
                        try {
                            val partes = dataNascimento.split("/")
                            "${partes[2]}-${partes[1]}-${partes[0]}"
                        } catch (e: Exception) { dataNascimento }
                    } else dataNascimento

                    val atualizado = membroOriginal!!.copy(nome = nome.trim(), cpf = cpf, rg = rg, dataNascimento = dataIso.ifBlank { null }, endereco = endereco, celularMembro = celularMembro, telefone = telefone, nomePai = nomePai, celularPai = celularPai, nomeMae = nomeMae, celularMae = celularMae, fotoUri = fotoUri)
                    
                    // Se a foto mudou, apaga a física antiga
                    if (membroOriginal?.fotoUri != null && membroOriginal?.fotoUri != fotoUri) {
                        FileUtils.excluirArquivo(membroOriginal?.fotoUri)
                    }

                    viewModel.atualizarMembro(atualizado)
                    membroOriginal = atualizado
                    editando = false
                }, modifier = Modifier.fillMaxWidth().padding(top = 10.dp).height(54.dp), colors = ButtonDefaults.buttonColors(containerColor = ChamaKidsAction, contentColor = Color.Black), border = BorderStroke(1.5.dp, Color.Black)) { Text("SALVAR") }
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedButton(onClick = {
                    val o = membroOriginal!!

                    // Se escolheu uma foto nova mas desistiu, apaga a foto nova física
                    if (!fotoUri.isNullOrBlank() && fotoUri != o.fotoUri) {
                        FileUtils.excluirArquivo(fotoUri)
                    }

                    nome = o.nome; cpf = o.cpf; rg = o.rg; dataNascimento = formatarDataBR(o.dataNascimento)
                    endereco = o.endereco; celularMembro = o.celularMembro; telefone = o.telefone
                    nomePai = o.nomePai; celularPai = o.celularPai; nomeMae = o.nomeMae; celularMae = o.celularMae
                    fotoUri = o.fotoUri; editando = false
                }, modifier = Modifier.fillMaxWidth().padding(bottom = 45.dp).height(54.dp), colors = ButtonDefaults.buttonColors(containerColor = ChamaKidsCard, contentColor = Color.Black), border = BorderStroke(1.5.dp, Color.Black)) { Text("DESCARTAR") }
            }
        }
    }
}

@Composable
private fun InfoCard(label: String, value: String) {
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F1F5))) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = label, fontSize = 12.sp, color = Color.Gray)
            Text(text = value, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color.Black)
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
    visualTransformation: VisualTransformation = VisualTransformation.None,
    onChange: (String) -> Unit
) {
    OutlinedTextField(
        value = valor,
        onValueChange = onChange,
        enabled = habilitado,
        label = { Text(titulo) },
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        visualTransformation = visualTransformation,
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            disabledContainerColor = Color(0xFFE2E2E2),
            disabledTextColor = Color(0xFF666666),
            disabledBorderColor = Color(0xFFAAAAAA)
        ),
        modifier = modifier.fillMaxWidth()
    )
}
