package com.pedro.ChamaKids

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class UpdateInfo(
    val versionCode: Int,
    val apkUrl: String,
    val releaseNotes: String
)

object UpdateChecker {
    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
    }

    // URL onde ficará o JSON com as informações da versão
    private const val UPDATE_JSON_URL = "https://raw.githubusercontent.com/usuario/projeto/main/update.json"

    suspend fun checkUpdate(currentVersionCode: Int): UpdateInfo? {
        return try {
            val info: UpdateInfo = client.get(UPDATE_JSON_URL).body()
            if (info.versionCode > currentVersionCode) {
                info
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
}
