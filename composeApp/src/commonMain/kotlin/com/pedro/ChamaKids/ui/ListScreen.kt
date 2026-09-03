package com.pedro.ChamaKids.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ListScreen(
    onVoltar: () -> Unit,
    onAbrirMembro: (Int) -> Unit,
    viewModel: MemberViewModel
) {
    val membros by viewModel.membros.collectAsState()

    ChamaKidsScreen(
        titulo = "LISTA",
        onVoltar = onVoltar
    ) {
        if (membros.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Nenhum membro cadastrado.",
                    fontSize = 16.sp,
                    color = Color.Gray
                )
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(16.dp),
                verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(16.dp)
            ) {
                items(membros) { membro ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clickable { onAbrirMembro(membro.id) }
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1f)
                                .clip(CircleShape)
                                .background(Color(0xFFD9D9D9)),
                            contentAlignment = Alignment.Center
                        ) {
                            MemberImage(
                                fotoUri = membro.fotoUri,
                                modifier = Modifier.fillMaxSize(),
                                placeholderText = membro.nome.firstOrNull()?.uppercase() ?: "?"
                            )
                        }

                        Text(
                            text = membro.nome.split(" ").firstOrNull() ?: "",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.Black,
                            modifier = Modifier.padding(top = 4.dp),
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}
