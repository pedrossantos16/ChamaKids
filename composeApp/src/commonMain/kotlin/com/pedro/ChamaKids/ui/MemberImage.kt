package com.pedro.ChamaKids.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun MemberImage(fotoUri: String?, modifier: Modifier, placeholderText: String)
