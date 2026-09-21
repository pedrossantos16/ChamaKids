package com.pedro.ChamaKids.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.pedro.ChamaKids.IdGenerator

@Entity(
    tableName = "users"
)
data class UserEntity(
    @PrimaryKey
    val serverId: String = IdGenerator.generate(),
    val nome: String,
    val fraseSecreta: String,
    val lastUpdated: Long = 0
)
