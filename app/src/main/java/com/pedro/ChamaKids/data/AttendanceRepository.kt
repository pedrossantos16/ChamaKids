package com.pedro.ChamaKids.data

import androidx.room.withTransaction
import kotlinx.coroutines.flow.Flow

class AttendanceRepository(
    private val database: ChamaKidsDatabase
) {

    private val attendanceDao =
        database.attendanceDao()

    val chamadas:
            Flow<List<AttendanceEntity>> =
        attendanceDao.observarChamadas()


    /**
     * Salva uma chamada completa:
     *
     * 1. cria a chamada;
     * 2. obtém o ID gerado;
     * 3. salva presença/falta de todos os membros.
     *
     * A transação garante que tudo seja salvo junto.
     */
    suspend fun salvarChamada(
        nome: String?,
        presencas: Map<Int, Boolean>
    ) {

        database.withTransaction {

            val chamadaId =
                attendanceDao
                    .inserirChamada(
                        AttendanceEntity(
                            nome = nome,
                            dataHora =
                                System.currentTimeMillis()
                        )
                    )
                    .toInt()


            val registros =
                presencas.map {
                        (memberId, presente) ->

                    AttendanceRecordEntity(
                        attendanceId =
                            chamadaId,
                        memberId =
                            memberId,
                        presente =
                            presente
                    )
                }


            attendanceDao
                .inserirRegistros(
                    registros
                )
        }
    }


    suspend fun buscarRegistros(
        chamadaId: Int
    ): List<AttendanceRecordEntity> {

        return attendanceDao
            .buscarRegistrosDaChamada(
                chamadaId
            )
    }

    suspend fun buscarChamadaPorId(id: Int): AttendanceEntity? {
        return attendanceDao.buscarPorId(id)
    }


    /**
     * Retorna a frequência em percentual.
     *
     * Exemplo:
     *
     * 8 presenças / 10 chamadas = 80%
     *
     * Caso ainda não exista chamada,
     * retorna null.
     */
    suspend fun calcularFrequencia(
        memberId: Int
    ): Float? {

        val total =
            attendanceDao
                .contarChamadasDoMembro(
                    memberId
                )

        if (total == 0) {
            return null
        }

        val presentes =
            attendanceDao
                .contarPresencasDoMembro(
                    memberId
                )

        return (
                presentes.toFloat() /
                        total.toFloat()
                ) * 100f
    }

    suspend fun buscarHistorico(
        memberId: Int
    ): List<MemberAttendanceInfo> {
        return attendanceDao
            .buscarHistoricoDoMembro(
                memberId
            )
    }

    suspend fun excluirChamadas(ids: List<Int>) {
        attendanceDao.excluirChamadas(ids)
    }

    suspend fun buscarMembroMaisPresente(inicio: Long, fim: Long) =
        attendanceDao.membroMaisPresenteNoPeriodo(inicio, fim)

    suspend fun buscarDiaMaiorAssiduidade(inicio: Long, fim: Long) =
        attendanceDao.diaMaiorAssiduidadeNoPeriodo(inicio, fim)
}
