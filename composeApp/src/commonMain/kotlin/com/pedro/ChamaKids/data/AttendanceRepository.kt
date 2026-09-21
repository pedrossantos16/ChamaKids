package com.pedro.ChamaKids.data

import kotlinx.coroutines.flow.Flow

class AttendanceRepository(
    private val attendanceDao: AttendanceDao
) {
    val chamadas: Flow<List<AttendanceEntity>> = attendanceDao.observarChamadas()

    suspend fun salvarChamada(chamada: AttendanceEntity, registros: List<AttendanceRecordEntity>) {
        attendanceDao.inserirChamada(chamada)
        attendanceDao.inserirRegistros(registros)
        FirebaseSyncManager.syncAttendance(chamada, registros)
    }

    suspend fun buscarRegistrosDaChamada(attendanceId: String): List<AttendanceRecordEntity> {
        return attendanceDao.buscarRegistrosDaChamada(attendanceId)
    }

    suspend fun contarChamadasDoMembro(memberId: String): Int {
        return attendanceDao.contarChamadasDoMembro(memberId)
    }

    suspend fun contarPresencasDoMembro(memberId: String): Int {
        return attendanceDao.contarPresencasDoMembro(memberId)
    }

    suspend fun excluirChamadas(ids: List<String>) {
        attendanceDao.excluirChamadas(ids)
        // Opcional: deletar no Firestore também
    }

    suspend fun buscarMembroMaisPresente(inicio: Long, fim: Long) =
        attendanceDao.membroMaisPresenteNoPeriodo(inicio, fim)

    suspend fun buscarDiaMaiorAssiduidade(inicio: Long, fim: Long) =
        attendanceDao.diaMaiorAssiduidadeNoPeriodo(inicio, fim)

    suspend fun buscarHistoricoDoMembro(memberId: String) =
        attendanceDao.buscarHistoricoDoMembro(memberId)
}
