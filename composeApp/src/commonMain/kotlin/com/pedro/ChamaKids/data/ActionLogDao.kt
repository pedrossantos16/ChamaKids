package com.pedro.ChamaKids.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ActionLogDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserir(log: ActionLogEntity)

    @Query("SELECT * FROM action_logs ORDER BY dataHora DESC")
    fun observarAcoes(): Flow<List<ActionLogEntity>>

    @Query("SELECT * FROM action_logs ORDER BY dataHora DESC")
    suspend fun buscarTodasAcoes(): List<ActionLogEntity>

    @Query("DELETE FROM action_logs WHERE serverId = :serverId")
    suspend fun excluirPorServerId(serverId: String)

    @Query("DELETE FROM action_logs WHERE serverId IN (:ids)")
    suspend fun excluirAcoes(ids: List<String>)

    @Query("DELETE FROM action_logs")
    suspend fun limparTudo()

    @Query("SELECT COUNT(*) FROM action_logs")
    fun observarContagem(): Flow<Int>
}
