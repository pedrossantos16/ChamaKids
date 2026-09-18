package com.pedro.ChamaKids.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "security_state")
data class SecurityStateEntity(
    @PrimaryKey
    val deviceId: String,
    val falhasConsecutivas: Int = 0,
    val ultimoBloqueioTimestamp: Long = 0
)
