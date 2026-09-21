package com.pedro.ChamaKids.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.pedro.ChamaKids.IdGenerator

@Entity(
    tableName = "attendances"
)
data class AttendanceEntity(

    @PrimaryKey
    val serverId: String = IdGenerator.generate(),

    val nome: String? = null,

    val dataHora: Long,

    // Auditoria
    val criadoPor: String? = null,
    val lastUpdated: Long = 0
)
