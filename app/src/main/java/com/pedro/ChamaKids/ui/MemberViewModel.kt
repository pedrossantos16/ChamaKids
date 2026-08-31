package com.pedro.ChamaKids.ui

import android.app.Application

import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope

import com.pedro.ChamaKids.data.ChamaKidsDatabase
import com.pedro.ChamaKids.data.MemberEntity
import com.pedro.ChamaKids.data.MemberRepository

import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


/**
 * ViewModel responsável pelos membros.
 *
 * Faz a ligação entre:
 *
 * Interface
 *      ↓
 * ViewModel
 *      ↓
 * Repository
 *      ↓
 * Room
 */
class MemberViewModel(
    application: Application
) : AndroidViewModel(application) {


    // =====================================================
    // BANCO E REPOSITORY
    // =====================================================

    /*
     * Obtém a instância única do banco Room.
     */
    private val database =
        ChamaKidsDatabase.getDatabase(
            application
        )


    /*
     * Repository usado pelo ViewModel.
     */
    private val repository =
        MemberRepository(
            database.memberDao()
        )


    // =====================================================
    // LISTA DE MEMBROS
    // =====================================================

    /*
     * Lista observável de membros ativos.
     *
     * Quando um membro for cadastrado,
     * alterado ou inativado, essa lista será
     * atualizada automaticamente.
     */
    val membros =
        repository
            .membrosAtivos
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(
                    5_000
                ),
                initialValue = emptyList()
            )


    // =====================================================
    // ADICIONAR MEMBRO
    // =====================================================

    /**
     * Salva um novo membro no banco.
     *
     * onSucesso será executado somente depois
     * que o Room terminar de inserir o cadastro.
     */
    fun adicionarMembro(
        membro: MemberEntity,
        onSucesso: () -> Unit = {}
    ) {

        viewModelScope.launch {

            repository.adicionar(
                membro
            )

            onSucesso()
        }
    }


    // =====================================================
    // ATUALIZAR MEMBRO
    // =====================================================

    /**
     * Atualiza uma ficha existente.
     */
    fun atualizarMembro(
        membro: MemberEntity
    ) {

        viewModelScope.launch {

            repository.atualizar(
                membro
            )
        }
    }


    // =====================================================
    // INATIVAR MEMBRO
    // =====================================================

    /**
     * Remove o membro das listas normais
     * sem apagar seu histórico.
     */
    fun inativarMembro(
        id: Int
    ) {

        viewModelScope.launch {

            repository.inativar(
                id
            )
        }
    }


    // =====================================================
    // REATIVAR MEMBRO
    // =====================================================

    /**
     * Permite recuperar um cadastro inativado.
     */
    fun reativarMembro(
        id: Int
    ) {

        viewModelScope.launch {

            repository.reativar(
                id
            )
        }
    }

    suspend fun buscarMembroPorId(
        id: Int
    ): MemberEntity? {

        return repository.buscarPorId(id)
    }
}