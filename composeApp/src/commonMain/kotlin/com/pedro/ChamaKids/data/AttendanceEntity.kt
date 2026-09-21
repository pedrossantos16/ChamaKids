package com.pedro.ChamaKids.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "attendances"
)
data class AttendanceEntity(

    @PrimaryKey
    val serverId: String,

    val nome: String? = null,

    val dataHora: Long,

    // Auditoria
    val criadoPor: String? = null,
    val lastUpdated: Long = 0
)
