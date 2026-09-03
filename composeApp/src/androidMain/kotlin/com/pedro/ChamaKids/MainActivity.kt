package com.pedro.ChamaKids

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.pedro.ChamaKids.data.DatabaseProvider
import com.pedro.ChamaKids.data.getDatabaseBuilder
import com.pedro.ChamaKids.data.getRoomDatabase

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Inicializa o banco de dados se ainda não foi inicializado
        val builder = getDatabaseBuilder(applicationContext)
        val database = getRoomDatabase(builder)
        DatabaseProvider.initialize(database)
        PdfGenerator.setContext(this)

        setContent {
            App()
        }
    }
}
