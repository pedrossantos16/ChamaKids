package com.pedro.ChamaKids.data

import androidx.room.Entity
import androidx.room.PrimaryKey


/**
 * Representa uma chamada realizada.
 *
 * Cada vez que o responsável tocar em
 * "SALVAR CHAMADA", será criado um registro aqui.
 */
@Entity(
    tableName = "attendances"
)
data class AttendanceEntity(

    @PrimaryKey(
        autoGenerate = true
    )
    val id: Int = 0,

    /*
     * Data e hora da chamada.
     *
     * Vamos salvar em milissegundos porque isso
     * facilita ordenação, comparação e conversão.
     */
    val dataHora: Long
)