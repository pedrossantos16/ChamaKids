package com.pedro.ChamaKids.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users")
    fun observartodosUsuarios(): Flow<List<UserEntity>>

    @Insert
    suspend fun inserir(usuario: UserEntity): Long

    @Query("SELECT * FROM users WHERE serverId = :serverId LIMIT 1")
    suspend fun buscarPorServerId(serverId: String): UserEntity?

    @Query("UPDATE users SET serverId = :serverId WHERE id = :id")
    suspend fun atualizarServerId(id: Int, serverId: String)

    @Query("DELETE FROM users WHERE serverId = :serverId")
    suspend fun excluirPorServerId(serverId: String)

    @Query("DELETE FROM users WHERE id = :id")
    suspend fun excluir(id: Int)
    
    @Query("SELECT * FROM users WHERE id = :id")
    suspend fun buscarPorId(id: Int): UserEntity?

    @Query("SELECT * FROM users")
    suspend fun todosUsuarios(): List<UserEntity>
}
