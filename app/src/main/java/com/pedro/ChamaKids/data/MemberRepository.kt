package com.pedro.ChamaKids.data

import kotlinx.coroutines.flow.Flow


/**
 * Repository responsável por centralizar
 * o acesso aos dados dos membros.
 *
 * A interface nunca acessa o DAO diretamente.
 * Ela conversa com o Repository.
 */
class MemberRepository(
    private val memberDao: MemberDao
) {

    /**
     * Lista de membros ativos.
     *
     * Como é Flow, qualquer alteração no banco
     * será refletida automaticamente na interface.
     */
    val membrosAtivos:
            Flow<List<MemberEntity>> =
        memberDao.observarMembrosAtivos()


    /**
     * Lista completa, incluindo membros inativos.
     *
     * Será útil futuramente para administração
     * e histórico.
     */
    val todosMembros:
            Flow<List<MemberEntity>> =
        memberDao.observarTodosMembros()


    /**
     * Insere um novo membro.
     */
    suspend fun adicionar(
        membro: MemberEntity
    ): Long {

        return memberDao.inserir(
            membro
        )
    }


    /**
     * Atualiza um cadastro existente.
     */
    suspend fun atualizar(
        membro: MemberEntity
    ) {

        memberDao.atualizar(
            membro
        )
    }


    /**
     * Busca um membro específico.
     */
    suspend fun buscarPorId(
        id: Int
    ): MemberEntity? {

        return memberDao.buscarPorId(
            id
        )
    }


    /**
     * Remove o membro das listas atuais
     * sem apagar o histórico.
     */
    suspend fun inativar(
        id: Int
    ) {

        memberDao.inativar(
            id
        )
    }


    /**
     * Permite recuperar um membro inativo.
     */
    suspend fun reativar(
        id: Int
    ) {

        memberDao.reativar(
            id
        )
    }
}