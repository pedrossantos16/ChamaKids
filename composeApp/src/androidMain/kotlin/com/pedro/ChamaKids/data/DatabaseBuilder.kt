package com.pedro.ChamaKids.data

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase

fun getDatabaseBuilder(context: Context): RoomDatabase.Builder<ChamaKidsDatabase> {
    val dbFile = context.getDatabasePath("chamakids_database.db")
    return Room.databaseBuilder<ChamaKidsDatabase>(
        context = context,
        name = dbFile.absolutePath
    )
}
