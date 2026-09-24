package com.pedro.ChamaKids.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users")
    fun observartodosUsuarios(): Flow<List<UserEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserir(usuario: UserEntity)

    @Query("SELECT * FROM users WHERE serverId = :serverId LIMIT 1")
    suspend fun buscarPorServerId(serverId: String): UserEntity?

    @Query("DELETE FROM users WHERE serverId = :serverId")
    suspend fun excluirPorServerId(serverId: String)
    
    @Query("SELECT * FROM users")
    suspend fun todosUsuarios(): List<UserEntity>

    @Query("DELETE FROM users")
    suspend fun limparTodos()

    @Query("SELECT COUNT(*) FROM users")
    fun observarContagem(): Flow<Int>
}
