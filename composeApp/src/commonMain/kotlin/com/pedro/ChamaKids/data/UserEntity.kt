package com.pedro.ChamaKids.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val serverId: String? = null,
    val nome: String,
    val fraseSecreta: String,
    val lastUpdated: Long = 0
)
