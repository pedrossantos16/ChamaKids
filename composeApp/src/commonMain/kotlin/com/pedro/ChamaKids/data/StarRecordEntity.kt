package com.pedro.ChamaKids.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Index
import com.pedro.ChamaKids.IdGenerator

@Entity(
    tableName = "star_records",
    foreignKeys = [
        ForeignKey(
            entity = MemberEntity::class,
            parentColumns = ["serverId"],
            childColumns = ["memberId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("memberId")]
)
data class StarRecordEntity(
    @PrimaryKey
    val serverId: String = IdGenerator.generate(),
    val memberId: String, // serverId do membro
    val dataHora: Long,
    val comentario: String?,
    
    // Auditoria
    val criadoPor: String? = null,
    val lastUpdated: Long = 0
)
