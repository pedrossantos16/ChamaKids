package com.pedro.ChamaKids.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun GuideScreen(onVoltar: () -> Unit) {
    var expandidoMembros by remember { mutableStateOf(false) }
    var expandidoLista by remember { mutableStateOf(false) }
    var expandidoClassificar by remember { mutableStateOf(false) }
    var expandidoRanking by remember { mutableStateOf(false) }
    var expandidoRelatorio by remember { mutableStateOf(false) }
    var expandidoHistorico by remember { mutableStateOf(false) }
    var expandidoChamada by remember { mutableStateOf(false) }

    ChamaKidsScreen(titulo = "GUIA", onVoltar = onVoltar) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            SecaoGuia("CHAMADA", expandidoChamada, { expandidoChamada = !expandidoChamada }, 
                "Registrar quem veio na aula.", 
                "Selecione 'PRESENTE' ou 'FALTOU' para cada criança e salve com um nome personalizado.")
            
            Spacer(modifier = Modifier.height(16.dp))

            SecaoGuia("MEMBROS", expandidoMembros, { expandidoMembros = !expandidoMembros }, 
                "Gerenciar o cadastro das crianças.", 
                "Aqui você pode adicionar novos alunos, editar informações existentes ou excluir cadastros.")

            Spacer(modifier = Modifier.height(16.dp))

            SecaoGuia("LISTA", expandidoLista, { expandidoLista = !expandidoLista }, 
                "Visualização rápida e estatísticas.", 
                "Uma galeria de fotos que mostra o desempenho detalhado de cada criança (Presenças e Estrelas).")

            Spacer(modifier = Modifier.height(16.dp))

            SecaoGuia("CLASSIFICAR", expandidoClassificar, { expandidoClassificar = !expandidoClassificar }, 
                "Premiar o bom comportamento.", 
                "Dê estrelas para as crianças que se destacaram. Isso influenciará o Ranking.")

            Spacer(modifier = Modifier.height(16.dp))

            SecaoGuia("RANKING", expandidoRanking, { expandidoRanking = !expandidoRanking }, 
                "Ver os destaques do grupo.", 
                "Um pódio dinâmico baseado na quantidade de estrelas e na frequência de cada um.")

            Spacer(modifier = Modifier.height(16.dp))

            SecaoGuia("RELATÓRIO", expandidoRelatorio, { expandidoRelatorio = !expandidoRelatorio }, 
                "Análise mensal completa.", 
                "Veja recordes do mês e gere documentos PDF profissionais para baixar.")

            Spacer(modifier = Modifier.height(16.dp))

            SecaoGuia("HISTÓRICO", expandidoHistorico, { expandidoHistorico = !expandidoHistorico }, 
                "Consultar chamadas passadas.", 
                "Veja os detalhes de cada dia, quem ganhou estrela e exclua registros antigos se necessário.")
            
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
private fun SecaoGuia(titulo: String, expandido: Boolean, onToggle: () -> Unit, objetivo: String, instrucoes: String) {
    SecaoExpansivel(titulo = titulo, expandido = expandido, onToggle = onToggle) {
        Column {
            Text(text = "Objetivo:", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.DarkGray)
            Text(text = objetivo, fontSize = 15.sp, modifier = Modifier.padding(bottom = 12.dp))
            
            Text(text = "Como usar:", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.DarkGray)
            Text(text = instrucoes, fontSize = 15.sp)
        }
    }
}
