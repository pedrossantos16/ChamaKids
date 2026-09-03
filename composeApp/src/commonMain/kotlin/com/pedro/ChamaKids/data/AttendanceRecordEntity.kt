package com.pedro.ChamaKids.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index


/**
 * Representa a presença de UM membro
 * em UMA chamada.
 *
 * Exemplo:
 *
 * chamada 10
 * Pedro
 * presente = true
 */
@Entity(
    tableName = "attendance_records",

    primaryKeys = [
        "attendanceId",
        "memberId"
    ],

    foreignKeys = [

        ForeignKey(
            entity = AttendanceEntity::class,
            parentColumns = ["id"],
            childColumns = ["attendanceId"],
            onDelete = ForeignKey.CASCADE
        ),

        ForeignKey(
            entity = MemberEntity::class,
            parentColumns = ["id"],
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

    /*
     * ID da chamada.
     */
    val attendanceId: Int,

    /*
     * ID do membro.
     */
    val memberId: Int,

    /*
     * true  = presente
     * false = faltou
     */
    val presente: Boolean
)