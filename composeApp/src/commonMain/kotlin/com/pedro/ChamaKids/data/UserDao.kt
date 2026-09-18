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
    suspend fun inserir(usuario: UserEntity)

    @Query("DELETE FROM users WHERE id = :id")
    suspend fun excluir(id: Int)
    
    @Query("SELECT * FROM users WHERE id = :id")
    suspend fun buscarPorId(id: Int): UserEntity?
}
