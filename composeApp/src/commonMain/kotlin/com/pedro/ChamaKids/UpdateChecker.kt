package com.pedro.ChamaKids

import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import io.ktor.utils.io.*
import io.ktor.utils.io.core.*
import io.ktor.http.*

@Serializable
data class UpdateInfo(
    val versionCode: Int,
    val versionName: String,
    val apkUrl: String,
    val releaseNotes: String
)

object UpdateChecker {
    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
        install(HttpTimeout) {
            requestTimeoutMillis = 60000
        }
        followRedirects = true
    }

    private const val UPDATE_JSON_URL = "https://raw.githubusercontent.com/pedrossantos16/ChamaKids/master/update.json"

    suspend fun checkUpdate(currentVersionCode: Int): UpdateInfo? {
        val responseString: String = client.get(UPDATE_JSON_URL).bodyAsText()
        val info = Json { ignoreUnknownKeys = true }.decodeFromString<UpdateInfo>(responseString)
        return if (info.versionCode > currentVersionCode) {
            info
        } else {
            null
        }
    }

    suspend fun downloadApk(url: String, onProgress: (Float) -> Unit): ByteArray? {
        return try {
            val response = client.get(url)
            val contentLength = response.headers[HttpHeaders.ContentLength]?.toLong() ?: -1L
            val channel: ByteReadChannel = response.bodyAsChannel()
            val buffer = ByteArray(1024 * 8)
            val output = BytePacketBuilder()
            var totalRead = 0L

            while (!channel.isClosedForRead) {
                val read = channel.readAvailable(buffer)
                if (read <= 0) break
                output.writeFully(buffer, 0, read)
                totalRead += read
                if (contentLength > 0) {
                    onProgress(totalRead.toFloat() / contentLength)
                }
            }
            output.build().readBytes()
        } catch (e: Exception) {
            null
        }
    }
}
