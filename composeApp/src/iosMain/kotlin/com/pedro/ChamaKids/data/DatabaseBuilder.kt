package com.pedro.ChamaKids.data

import androidx.room.Room
import androidx.room.RoomDatabase
import platform.Foundation.NSHomeDirectory

fun getDatabaseBuilder(): RoomDatabase.Builder<ChamaKidsDatabase> {
    val dbFilePath = NSHomeDirectory() + "/chamakids_database.db"
    return Room.databaseBuilder<ChamaKidsDatabase>(
        name = dbFilePath,
        factory = { ChamaKidsDatabase::class.instantiateImpl() }
    )
}
