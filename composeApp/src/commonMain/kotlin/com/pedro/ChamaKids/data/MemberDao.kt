package com.pedro.ChamaKids.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface MemberDao {

    @Query("SELECT * FROM members WHERE ativo = 1 ORDER BY nome ASC")
    fun observarMembrosAtivos(): Flow<List<MemberEntity>>

    @Query("SELECT * FROM members ORDER BY nome ASC")
    fun observarTodosMembros(): Flow<List<MemberEntity>>

    @Query("SELECT * FROM members WHERE serverId = :serverId LIMIT 1")
    suspend fun buscarPorServerId(serverId: String): MemberEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserir(membro: MemberEntity)

    @Update
    suspend fun atualizar(membro: MemberEntity)

    @Query("UPDATE members SET ativo = 0 WHERE serverId = :serverId")
    suspend fun inativar(serverId: String)

    @Query("UPDATE members SET ativo = 1 WHERE serverId = :serverId")
    suspend fun reativar(serverId: String)
    
    @Query("SELECT * FROM members")
    suspend fun buscarTodos(): List<MemberEntity>
}
