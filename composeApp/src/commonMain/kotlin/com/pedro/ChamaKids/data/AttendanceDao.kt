package com.pedro.ChamaKids.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction

import kotlinx.coroutines.flow.Flow


@Dao
interface AttendanceDao {

    /**
     * Cria uma nova chamada.
     *
     * Room retorna Long mesmo que a PK seja Int.
     */
    @Insert
    suspend fun inserirChamada(
        chamada: AttendanceEntity
    ): Long


    /**
     * Insere todas as presenças/faltas
     * de uma chamada.
     */
    @Insert
    suspend fun inserirRegistros(
        registros: List<AttendanceRecordEntity>
    )


    /**
     * Retorna todas as chamadas,
     * da mais recente para a mais antiga.
     */
    @Query(
        """
        SELECT *
        FROM attendances
        ORDER BY dataHora DESC
        """
    )
    fun observarChamadas():
            Flow<List<AttendanceEntity>>


    /**
     * Retorna os registros de uma chamada específica.
     */
    @Query(
        """
        SELECT *
        FROM attendance_records
        WHERE attendanceId = :attendanceId
        """
    )
    suspend fun buscarRegistrosDaChamada(
        attendanceId: Int
    ): List<AttendanceRecordEntity>


    /**
     * Total de chamadas registradas para o membro.
     */
    @Query(
        """
        SELECT COUNT(*)
        FROM attendance_records
        WHERE memberId = :memberId
        """
    )
    suspend fun contarChamadasDoMembro(
        memberId: Int
    ): Int


    /**
     * Quantas dessas chamadas foram presença.
     */
    @Query(
        """
        SELECT COUNT(*)
        FROM attendance_records
        WHERE memberId = :memberId
        AND presente = 1
        """
    )
    suspend fun contarPresencasDoMembro(
        memberId: Int
    ): Int

    @Query("SELECT * FROM attendances WHERE id = :id")
    suspend fun buscarPorId(id: Int): AttendanceEntity?

    @Query("DELETE FROM attendances WHERE id IN (:ids)")
    suspend fun excluirChamadas(ids: List<Int>)

    @Query("""
        SELECT m.id, m.nome, m.fotoUri, COUNT(r.attendanceId) as count
        FROM members m
        JOIN attendance_records r ON m.id = r.memberId
        JOIN attendances a ON r.attendanceId = a.id
        WHERE r.presente = 1 AND a.dataHora >= :inicio AND a.dataHora <= :fim
        GROUP BY m.id
        ORDER BY count DESC LIMIT 1
    """)
    suspend fun membroMaisPresenteNoPeriodo(inicio: Long, fim: Long): MemberWithStats?

    @Query("""
        SELECT a.dataHora as timestamp, COUNT(r.memberId) as count
        FROM attendances a
        JOIN attendance_records r ON a.id = r.attendanceId
        WHERE r.presente = 1 AND a.dataHora >= :inicio AND a.dataHora <= :fim
        GROUP BY a.id
        ORDER BY count DESC LIMIT 1
    """)
    suspend fun diaMaiorAssiduidadeNoPeriodo(inicio: Long, fim: Long): PeriodStat?

    /**
     * Retorna a data (timestamp) e se o membro estava presente
     * em todas as chamadas que ele participou.
     */
    @Query(
        """
        SELECT a.dataHora, r.presente
        FROM attendances a
        JOIN attendance_records r ON a.id = r.attendanceId
        WHERE r.memberId = :memberId
        ORDER BY a.dataHora ASC
        """
    )
    suspend fun buscarHistoricoDoMembro(
        memberId: Int
    ): List<MemberAttendanceInfo>
}

data class MemberWithStats(
    val id: Int,
    val nome: String,
    val fotoUri: String?,
    val count: Int
)

data class PeriodStat(
    val timestamp: Long,
    val count: Int
)

data class MemberAttendanceInfo(
    val dataHora: Long,
    val presente: Boolean
)
