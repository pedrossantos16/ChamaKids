package com.pedro.ChamaKids.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface StarDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserirEstrela(estrela: StarRecordEntity)

    @Query("SELECT * FROM star_records WHERE serverId = :serverId LIMIT 1")
    suspend fun buscarPorServerId(serverId: String): StarRecordEntity?

    @Query("DELETE FROM star_records WHERE serverId = :serverId")
    suspend fun excluirPorServerId(serverId: String)

    @Query("SELECT * FROM star_records")
    suspend fun buscarTodasEstrelas(): List<StarRecordEntity>

    @Query("SELECT COUNT(*) FROM star_records WHERE memberId = :memberId")
    suspend fun contarEstrelasDoMembro(memberId: String): Int

    @Query("""
        SELECT m.serverId as id, m.nome, m.fotoUri, 
        (SELECT COUNT(*) FROM star_records s WHERE s.memberId = m.serverId) as totalEstrelas,
        (SELECT COUNT(*) FROM attendance_records r WHERE r.memberId = m.serverId AND r.presente = 1) as totalPresencas
        FROM members m
        WHERE m.ativo = 1
        ORDER BY totalEstrelas DESC, totalPresencas DESC
    """)
    suspend fun buscarRanking(): List<MemberWithRanking>

    @Query("SELECT dataHora FROM star_records WHERE memberId = :memberId")
    suspend fun buscarHistoricoEstrelas(memberId: String): List<Long>

    @Query("SELECT * FROM star_records WHERE memberId = :memberId ORDER BY dataHora DESC")
    suspend fun buscarEstrelasDoMembro(memberId: String): List<StarRecordEntity>

    @Query("""
        SELECT m.serverId as id, m.nome, m.fotoUri, COUNT(s.serverId) as count, s.comentario
        FROM members m
        JOIN star_records s ON m.serverId = s.memberId
        WHERE s.dataHora >= :inicio AND s.dataHora <= :fim
        GROUP BY m.serverId
        ORDER BY count DESC LIMIT 1
    """)
    suspend fun membroMaisEstrelasNoPeriodo(inicio: Long, fim: Long): MemberWithStarStats?

    @Query("""
        SELECT dataHora as timestamp, COUNT(serverId) as count
        FROM star_records
        WHERE dataHora >= :inicio AND dataHora <= :fim
        GROUP BY CAST(dataHora / 86400000 AS INTEGER)
        ORDER BY count DESC LIMIT 1
    """)
    suspend fun diaMaisEstrelasNoPeriodo(inicio: Long, fim: Long): PeriodStat?

    @Query("DELETE FROM star_records")
    suspend fun limparTodasEstrelas()
}

data class MemberWithStarStats(
    val id: String,
    val nome: String,
    val fotoUri: String?,
    val count: Int,
    val comentario: String?
)

data class MemberWithRanking(
    val id: String,
    val nome: String,
    val fotoUri: String?,
    val totalEstrelas: Int,
    val totalPresencas: Int
)
