package com.pedro.ChamaKids.data

import kotlinx.coroutines.flow.Flow

class MemberRepository(
    private val memberDao: MemberDao,
    private val starDao: StarDao
) {
    val membrosAtivos: Flow<List<MemberEntity>> = memberDao.observarMembrosAtivos()
    val todosMembros: Flow<List<MemberEntity>> = memberDao.observarTodosMembros()

    suspend fun adicionar(membro: MemberEntity) {
        val serverId = com.pedro.ChamaKids.IdGenerator.generate()
        val membroComId = membro.copy(serverId = serverId)
        memberDao.inserir(membroComId)
        FirebaseSyncManager.syncMember(membroComId)
    }

    suspend fun atualizar(membro: MemberEntity) {
        memberDao.atualizar(membro)
        FirebaseSyncManager.syncMember(membro)
    }

    suspend fun buscarPorId(serverId: String): MemberEntity? {
        return memberDao.buscarPorServerId(serverId)
    }

    suspend fun inativar(serverId: String) {
        memberDao.inativar(serverId)
        buscarPorId(serverId)?.let { FirebaseSyncManager.syncMember(it) }
    }

    suspend fun reativar(serverId: String) {
        memberDao.reativar(serverId)
        buscarPorId(serverId)?.let { FirebaseSyncManager.syncMember(it) }
    }

    suspend fun darEstrela(estrela: StarRecordEntity) {
        starDao.inserirEstrela(estrela)
        FirebaseSyncManager.syncStar(estrela)
    }

    suspend fun buscarRanking(): List<MemberWithRanking> {
        return starDao.buscarRanking()
    }

    suspend fun buscarHistoricoEstrelas(memberId: String): List<Long> {
        return starDao.buscarHistoricoEstrelas(memberId)
    }

    suspend fun buscarMembroMaisEstrelas(inicio: Long, fim: Long) =
        starDao.membroMaisEstrelasNoPeriodo(inicio, fim)

    suspend fun buscarDiaMaisEstrelas(inicio: Long, fim: Long) =
        starDao.diaMaisEstrelasNoPeriodo(inicio, fim)
}
