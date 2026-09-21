package com.pedro.ChamaKids.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "attendance_records",

    primaryKeys = [
        "attendanceId",
        "memberId"
    ],

    foreignKeys = [
        ForeignKey(
            entity = AttendanceEntity::class,
            parentColumns = ["serverId"],
            childColumns = ["attendanceId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = MemberEntity::class,
            parentColumns = ["serverId"],
            childColumns = ["memberId"],
            onDelete = ForeignKey.NO_ACTION
        )
    ],

    indices = [
        Index("attendanceId"),
        Index("memberId")
    ]
)
data class AttendanceRecordEntity(
    val attendanceId: String, // serverId da chamada
    val memberId: String, // serverId do membro
    val presente: Boolean,
    val serverId: String,
    val lastUpdated: Long = 0
)
