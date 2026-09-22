package com.pedro.ChamaKids.data

import com.pedro.ChamaKids.IdGenerator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

object ActionLogManager {
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    fun registrarAcao(tipoAcao: String, descricao: String, usuarioNome: String?) {
        scope.launch {
            try {
                val db = DatabaseProvider.getDatabase()
                val timestamp = Clock.System.now().toEpochMilliseconds()
                val nomeLimpo = usuarioNome?.trim().takeUnless { it.isNullOrBlank() } ?: "Sistema / Geral"
                val log = ActionLogEntity(
                    serverId = IdGenerator.generate(),
                    usuarioNome = nomeLimpo,
                    tipoAcao = tipoAcao,
                    descricao = descricao,
                    dataHora = timestamp,
                    lastUpdated = timestamp
                )
                db.actionLogDao().inserir(log)
                FirebaseSyncManager.syncActionLog(log)
            } catch (_: Exception) {}
        }
    }
}
