package com.pedro.ChamaKids.data

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor

@Database(
    entities = [
        MemberEntity::class,
        AttendanceEntity::class,
        AttendanceRecordEntity::class,
        StarRecordEntity::class
    ],
    version = 3,
    exportSchema = false
)
@ConstructedBy(ChamaKidsDatabaseConstructor::class)
abstract class ChamaKidsDatabase : RoomDatabase() {
    abstract fun memberDao(): MemberDao
    abstract fun attendanceDao(): AttendanceDao
    abstract fun starDao(): StarDao
}

@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object ChamaKidsDatabaseConstructor : RoomDatabaseConstructor<ChamaKidsDatabase>

fun getRoomDatabase(
    builder: RoomDatabase.Builder<ChamaKidsDatabase>
): ChamaKidsDatabase {
    return builder
        .fallbackToDestructiveMigration(true)
        .build()
}
