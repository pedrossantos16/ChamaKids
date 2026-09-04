package com.pedro.ChamaKids.data

import androidx.room.withTransaction
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Clock

class AttendanceRepository(
    private val database: ChamaKidsDatabase
) {

    private val attendanceDao = database.attendanceDao()

    val chamadas: Flow<List<AttendanceEntity>> = attendanceDao.observarChamadas()

    suspend fun salvarChamada(
        nome: String?,
        presencas: Map<Int, Boolean>
    ) {
        database.withTransaction {
            val chamadaId = attendanceDao.inserirChamada(
                AttendanceEntity(
                    nome = nome,
                    dataHora = Clock.System.now().toEpochMilliseconds()
                )
            ).toInt()

            val registros = presencas.map { (memberId, presente) ->
                AttendanceRecordEntity(
                    attendanceId = chamadaId,
                    memberId = memberId,
                    presente = presente
                )
            }

            attendanceDao.inserirRegistros(registros)
        }
    }

    suspend fun buscarRegistros(chamadaId: Int) = attendanceDao.buscarRegistrosDaChamada(chamadaId)
    suspend fun buscarChamadaPorId(id: Int) = attendanceDao.buscarPorId(id)

    suspend fun calcularFrequencia(memberId: Int): Float? {
        val total = attendanceDao.contarChamadasDoMembro(memberId)
        if (total == 0) return null
        val presentes = attendanceDao.contarPresencasDoMembro(memberId)
        return (presentes.toFloat() / total.toFloat()) * 100f
    }

    suspend fun buscarHistorico(memberId: Int) = attendanceDao.buscarHistoricoDoMembro(memberId)
    suspend fun excluirChamadas(ids: List<Int>) = attendanceDao.excluirChamadas(ids)

    suspend fun buscarMembroMaisPresente(inicio: Long, fim: Long) =
        attendanceDao.membroMaisPresenteNoPeriodo(inicio, fim)

    suspend fun buscarDiaMaiorAssiduidade(inicio: Long, fim: Long) =
        attendanceDao.diaMaiorAssiduidadeNoPeriodo(inicio, fim)
}
