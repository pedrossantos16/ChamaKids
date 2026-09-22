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
            com.pedro.ChamaKids.data.ActionLogManager.registrarAcao(
                tipoAcao = "Membro Adicionado",
                descricao = "Cadastrou o membro '${membro.nome}'",
                usuarioNome = membro.criadoPor
            )
            onSucesso()
        }
    }

    fun atualizarMembro(membro: MemberEntity) {
        viewModelScope.launch {
            repository.atualizar(membro)
            com.pedro.ChamaKids.data.ActionLogManager.registrarAcao(
                tipoAcao = "Membro Editado",
                descricao = "Atualizou os dados do membro '${membro.nome}'",
                usuarioNome = membro.ultimaAlteracaoPor
            )
        }
    }

    fun inativarMembro(serverId: String, executadoPor: String? = null) {
        viewModelScope.launch {
            val m = repository.buscarPorId(serverId)
            repository.inativar(serverId)
            val autor = executadoPor ?: m?.ultimaAlteracaoPor ?: m?.criadoPor
            com.pedro.ChamaKids.data.ActionLogManager.registrarAcao(
                tipoAcao = "Membro Inativado",
                descricao = "Inativou o membro '${m?.nome ?: "Desconhecido"}'",
                usuarioNome = autor
            )
        }
    }

    fun reativarMembro(serverId: String) {
        viewModelScope.launch {
            repository.reativar(serverId)
        }
    }

    suspend fun buscarMembroPorId(serverId: String): MemberEntity? {
        return repository.buscarPorId(serverId)
    }

    fun darEstrela(memberId: String, comentario: String?, criadoPor: String?, onSucesso: () -> Unit) {
        viewModelScope.launch {
            val m = repository.buscarPorId(memberId)
            val star = StarRecordEntity(
                serverId = com.pedro.ChamaKids.IdGenerator.generate(),
                memberId = memberId,
                dataHora = Clock.System.now().toEpochMilliseconds(),
                comentario = comentario,
                criadoPor = criadoPor
            )
            repository.darEstrela(star)
            val infoComentario = if (!comentario.isNullOrBlank()) " ($comentario)" else ""
            com.pedro.ChamaKids.data.ActionLogManager.registrarAcao(
                tipoAcao = "Estrela Atribuída",
                descricao = "Atribuiu estrela para '${m?.nome ?: "Membro"}'$infoComentario",
                usuarioNome = criadoPor
            )
            onSucesso()
        }
    }

    suspend fun buscarRanking(): List<MemberWithRanking> {
        return repository.buscarRanking()
    }

    suspend fun buscarHistoricoEstrelas(memberId: String): List<Long> {
        return repository.buscarHistoricoEstrelas(memberId)
    }

    suspend fun buscarMembroMaisEstrelas(inicio: Long, fim: Long) =
        repository.buscarMembroMaisEstrelas(inicio, fim)

    suspend fun buscarDiaMaisEstrelas(inicio: Long, fim: Long) =
        repository.buscarDiaMaisEstrelas(inicio, fim)
}
