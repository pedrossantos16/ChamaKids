package com.pedro.ChamaKids.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AttendanceDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserirChamada(chamada: AttendanceEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserirRegistros(registros: List<AttendanceRecordEntity>)

    @Query("SELECT * FROM attendances ORDER BY dataHora DESC")
    fun observarChamadas(): Flow<List<AttendanceEntity>>

    @Query("SELECT * FROM attendance_records WHERE attendanceId = :attendanceId")
    suspend fun buscarRegistrosDaChamada(attendanceId: String): List<AttendanceRecordEntity>

    @Query("SELECT COUNT(*) FROM attendance_records WHERE memberId = :memberId")
    suspend fun contarChamadasDoMembro(memberId: String): Int

    @Query("SELECT COUNT(*) FROM attendance_records WHERE memberId = :memberId AND presente = 1")
    suspend fun contarPresencasDoMembro(memberId: String): Int

    @Query("SELECT * FROM attendances WHERE serverId = :serverId LIMIT 1")
    suspend fun buscarPorServerId(serverId: String): AttendanceEntity?

    @Query("DELETE FROM attendances WHERE serverId IN (:ids)")
    suspend fun excluirChamadas(ids: List<String>)

    @Query("""
        SELECT m.serverId as id, m.nome, m.fotoUri, COUNT(r.attendanceId) as count
        FROM members m
        JOIN attendance_records r ON m.serverId = r.memberId
        JOIN attendances a ON r.attendanceId = a.serverId
        WHERE r.presente = 1 AND a.dataHora >= :inicio AND a.dataHora <= :fim
        GROUP BY m.serverId
        ORDER BY count DESC LIMIT 1
    """)
    suspend fun membroMaisPresenteNoPeriodo(inicio: Long, fim: Long): MemberWithStats?

    @Query("""
        SELECT a.dataHora as timestamp, COUNT(r.memberId) as count
        FROM attendances a
        JOIN attendance_records r ON a.serverId = r.attendanceId
        WHERE r.presente = 1 AND a.dataHora >= :inicio AND a.dataHora <= :fim
        GROUP BY a.serverId
        ORDER BY count DESC LIMIT 1
    """)
    suspend fun diaMaiorAssiduidadeNoPeriodo(inicio: Long, fim: Long): PeriodStat?

    @Query("""
        SELECT a.dataHora, r.presente
        FROM attendances a
        JOIN attendance_records r ON a.serverId = r.attendanceId
        WHERE r.memberId = :memberId
        ORDER BY a.dataHora ASC
    """)
    suspend fun buscarHistoricoDoMembro(memberId: String): List<MemberAttendanceInfo>
    
    @Query("SELECT * FROM attendances")
    suspend fun buscarTodasChamadas(): List<AttendanceEntity>
    
    @Query("SELECT * FROM attendance_records")
    suspend fun buscarTodosRegistros(): List<AttendanceRecordEntity>

    @Query("DELETE FROM attendances")
    suspend fun limparTodasChamadas()

    @Query("DELETE FROM attendance_records")
    suspend fun limparTodosRegistros()
}

data class MemberWithStats(
    val id: String,
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
