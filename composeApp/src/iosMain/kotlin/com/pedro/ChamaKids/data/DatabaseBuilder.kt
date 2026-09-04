package com.pedro.ChamaKids.data

import androidx.room.Room
import androidx.room.RoomDatabase
import platform.Foundation.NSHomeDirectory

fun getDatabaseBuilder(): RoomDatabase.Builder<ChamaKidsDatabase> {
    val dbFilePath = NSHomeDirectory() + "/chamakids_database.db"
    return Room.databaseBuilder<ChamaKidsDatabase>(
        name = dbFilePath,
        factory = { ChamaKidsDatabaseConstructor.initialize() }
    )
}

// O Room Multiplatform gera uma implementação interna para cada plataforma
@Suppress("UNCHECKED_CAST")
fun <T : RoomDatabase> androidx.room.RoomDatabaseConstructor<T>.initialize(): T {
    return ChamaKidsDatabase_Impl() as T
}

// Declaração externa para o compilador encontrar a classe gerada pelo KSP
@Suppress("NO_ACTUAL_FOR_EXPECT")
external class ChamaKidsDatabase_Impl : ChamaKidsDatabase
