package com.pedro.ChamaKids

import androidx.compose.ui.window.ComposeUIViewController
import com.pedro.ChamaKids.data.DatabaseProvider
import com.pedro.ChamaKids.data.getDatabaseBuilder
import com.pedro.ChamaKids.data.getRoomDatabase

fun MainViewController() = ComposeUIViewController(
    configure = {
        // Inicializa o banco de dados no iOS
        val builder = getDatabaseBuilder()
        val database = getRoomDatabase(builder)
        DatabaseProvider.initialize(database)
    }
) {
    App()
}
