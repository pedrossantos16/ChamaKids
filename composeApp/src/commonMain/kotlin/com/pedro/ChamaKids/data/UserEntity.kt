package com.pedro.ChamaKids.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "users"
)
data class UserEntity(
    @PrimaryKey
    val serverId: String,
    val nome: String,
    val fraseSecreta: String,
    val lastUpdated: Long = 0
)
