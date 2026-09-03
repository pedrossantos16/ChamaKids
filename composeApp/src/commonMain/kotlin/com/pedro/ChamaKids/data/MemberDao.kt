package com.pedro.ChamaKids.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

import kotlinx.coroutines.flow.Flow


@Dao
interface MemberDao {

    /*
     * Retorna apenas membros ativos.
     *
     * Essa será a lista usada normalmente
     * na tela de membros e nas chamadas.
     */
    @Query(
        """
        SELECT *
        FROM members
        WHERE ativo = 1
        ORDER BY nome ASC
        """
    )
    fun observarMembrosAtivos():
            Flow<List<MemberEntity>>


    /*
     * Retorna todos os membros,
     * inclusive os que foram inativados.
     */
    @Query(
        """
        SELECT *
        FROM members
        ORDER BY nome ASC
        """
    )
    fun observarTodosMembros():
            Flow<List<MemberEntity>>


    /*
     * Busca uma ficha específica.
     *
     * Seu MemberEntity usa Int como ID,
     * então aqui também deve ser Int.
     */
    @Query(
        """
        SELECT *
        FROM members
        WHERE id = :id
        LIMIT 1
        """
    )
    suspend fun buscarPorId(
        id: Int
    ): MemberEntity?


    /*
     * Cadastra um novo membro.
     *
     * O Room retorna Long para o ID gerado,
     * mesmo que a PK da Entity seja Int.
     */
    @Insert(
        onConflict = OnConflictStrategy.REPLACE
    )
    suspend fun inserir(
        membro: MemberEntity
    ): Long


    /*
     * Atualiza uma ficha já existente.
     */
    @Update
    suspend fun atualizar(
        membro: MemberEntity
    )


    /*
     * "Remove" o membro sem apagar
     * seus dados e histórico.
     */
    @Query(
        """
        UPDATE members
        SET ativo = 0
        WHERE id = :id
        """
    )
    suspend fun inativar(
        id: Int
    )


    /*
     * Reativa um membro anteriormente inativado.
     */
    @Query(
        """
        UPDATE members
        SET ativo = 1
        WHERE id = :id
        """
    )
    suspend fun reativar(
        id: Int
    )
}