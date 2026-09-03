package com.pedro.ChamaKids.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface StarDao {
    @Insert
    suspend fun inserirEstrela(estrela: StarRecordEntity)

    @Query("SELECT COUNT(*) FROM star_records WHERE memberId = :memberId")
    suspend fun contarEstrelasDoMembro(memberId: Int): Int

    /*
     * Query para o Ranking:
     * Retorna os membros ordenados por Estrelas (DESC) e depois por Presenças (DESC).
     * Nota: A lógica de presença aqui conta o total de registros 'presente = 1'.
     */
    @Query("""
        SELECT m.id, m.nome, m.fotoUri, 
        (SELECT COUNT(*) FROM star_records s WHERE s.memberId = m.id) as totalEstrelas,
        (SELECT COUNT(*) FROM attendance_records r WHERE r.memberId = m.id AND r.presente = 1) as totalPresencas
        FROM members m
        WHERE m.ativo = 1
        ORDER BY totalEstrelas DESC, totalPresencas DESC
    """)
    suspend fun buscarRanking(): List<MemberWithRanking>

    @Query("SELECT dataHora FROM star_records WHERE memberId = :memberId")
    suspend fun buscarHistoricoEstrelas(memberId: Int): List<Long>

    @Query("""
        SELECT m.id, m.nome, m.fotoUri, COUNT(s.id) as count, s.comentario
        FROM members m
        JOIN star_records s ON m.id = s.memberId
        WHERE s.dataHora >= :inicio AND s.dataHora <= :fim
        GROUP BY m.id
        ORDER BY count DESC LIMIT 1
    """)
    suspend fun membroMaisEstrelasNoPeriodo(inicio: Long, fim: Long): MemberWithStarStats?

    @Query("""
        SELECT dataHora as timestamp, COUNT(id) as count
        FROM star_records
        WHERE dataHora >= :inicio AND dataHora <= :fim
        GROUP BY CAST(dataHora / 86400000 AS INTEGER)
        ORDER BY count DESC LIMIT 1
    """)
    suspend fun diaMaisEstrelasNoPeriodo(inicio: Long, fim: Long): PeriodStat?
}

data class MemberWithStarStats(
    val id: Int,
    val nome: String,
    val fotoUri: String?,
    val count: Int,
    val comentario: String?
)

data class MemberWithRanking(
    val id: Int,
    val nome: String,
    val fotoUri: String?,
    val totalEstrelas: Int,
    val totalPresencas: Int
)
