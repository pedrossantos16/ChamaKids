package com.pedro.ChamaKids.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface SecurityDao {
    @Query("SELECT * FROM security_state WHERE deviceId = :deviceId")
    suspend fun buscarEstado(deviceId: String): SecurityStateEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun salvarEstado(estado: SecurityStateEntity)
}
