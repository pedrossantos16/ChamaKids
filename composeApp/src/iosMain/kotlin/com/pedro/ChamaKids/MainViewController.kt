package com.pedro.ChamaKids

import androidx.compose.ui.window.ComposeUIViewController
import com.pedro.ChamaKids.data.DatabaseProvider
import com.pedro.ChamaKids.data.getDatabaseBuilder
import com.pedro.ChamaKids.data.getRoomDatabase

import com.pedro.ChamaKids.data.FirebaseSyncManager
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.initialize

fun MainViewController() = ComposeUIViewController(
    configure = {
        // Inicializa o Firebase no iOS
        Firebase.initialize()
        
        // Inicializa o banco de dados no iOS
        val builder = getDatabaseBuilder()
        val database = getRoomDatabase(builder)
        DatabaseProvider.initialize(database)
        
        // Inicia a sincronização bidirecional com a nuvem
        FirebaseSyncManager.startSync(database)
    }
) {
    App()
}
