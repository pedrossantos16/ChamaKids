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
}