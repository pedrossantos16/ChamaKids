package com.pedro.ChamaKids.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pedro.ChamaKids.data.DatabaseProvider
import com.pedro.ChamaKids.data.MemberEntity
import com.pedro.ChamaKids.data.MemberRepository
import com.pedro.ChamaKids.data.MemberWithRanking
import com.pedro.ChamaKids.data.StarRecordEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

class MemberViewModel : ViewModel() {

    private val database = DatabaseProvider.getDatabase()

    private val repository =
        MemberRepository(
            database.memberDao(),
            database.starDao()
        )

    val membros =
        repository
            .membrosAtivos
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList()
            )

    fun adicionarMembro(membro: MemberEntity, onSucesso: () -> Unit = {}) {
        viewModelScope.launch {
            repository.adicionar(membro)
            onSucesso()
        }
    }

    fun atualizarMembro(membro: MemberEntity) {
        viewModelScope.launch {
            repository.atualizar(membro)
        }
    }

    fun inativarMembro(id: Int) {
        viewModelScope.launch {
            repository.inativar(id)
        }
    }

    fun reativarMembro(id: Int) {
        viewModelScope.launch {
            repository.reativar(id)
        }
    }

    suspend fun buscarMembroPorId(id: Int): MemberEntity? {
        return repository.buscarPorId(id)
    }

    fun darEstrela(memberId: Int, comentario: String?, onSucesso: () -> Unit) {
        viewModelScope.launch {
            repository.darEstrela(
                StarRecordEntity(
                    memberId = memberId,
                    dataHora = Clock.System.now().toEpochMilliseconds(),
                    comentario = comentario
                )
            )
            onSucesso()
        }
    }

    suspend fun buscarRanking(): List<MemberWithRanking> {
        return repository.buscarRanking()
    }

    suspend fun buscarHistoricoEstrelas(memberId: Int): List<Long> {
        return repository.buscarHistoricoEstrelas(memberId)
    }

    suspend fun buscarMembroMaisEstrelas(inicio: Long, fim: Long) =
        repository.buscarMembroMaisEstrelas(inicio, fim)

    suspend fun buscarDiaMaisEstrelas(inicio: Long, fim: Long) =
        repository.buscarDiaMaisEstrelas(inicio, fim)
}
