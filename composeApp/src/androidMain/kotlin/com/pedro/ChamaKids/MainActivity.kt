package com.pedro.ChamaKids

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.pedro.ChamaKids.data.DatabaseProvider
import com.pedro.ChamaKids.data.FirebaseSyncManager
import com.pedro.ChamaKids.data.getDatabaseBuilder
import com.pedro.ChamaKids.data.getRoomDatabase
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.initialize

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Inicializa o Firebase no Android
        Firebase.initialize(this)
        
        // Inicializa o banco de dados se ainda não foi inicializado
        val builder = getDatabaseBuilder(applicationContext)
        val database = getRoomDatabase(builder)
        DatabaseProvider.initialize(database)
        
        // Inicia a sincronização bidirecional com a nuvem
        FirebaseSyncManager.startSync(database)
        
        PdfGenerator.setContext(this)
        DeviceIdentifier.setContext(this)
        ApkInstaller.setContext(this)

        setContent {
            App()
        }
    }
}
