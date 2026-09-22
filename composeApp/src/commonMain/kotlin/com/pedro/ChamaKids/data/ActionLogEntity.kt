package com.pedro.ChamaKids.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "action_logs")
data class ActionLogEntity(
    @PrimaryKey
    val serverId: String,
    val usuarioNome: String,
    val tipoAcao: String,
    val descricao: String,
    val dataHora: Long,
    val lastUpdated: Long = dataHora
)
