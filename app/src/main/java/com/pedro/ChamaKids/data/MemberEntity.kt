package com.pedro.ChamaKids.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "members"
)
data class MemberEntity(

    @PrimaryKey(
        autoGenerate = true
    )
    val id: Int = 0,

    val nome: String,

    val cpf: String,

    val rg: String,

    /*
     * Vamos salvar a data como String no formato ISO:
     * yyyy-MM-dd
     *
     * Isso simplifica o banco e evita TypeConverter
     * nesta primeira versão.
     */
    val dataNascimento: String?,

    val endereco: String,

    val celularMembro: String,

    val telefone: String,

    val nomePai: String,

    val celularPai: String,

    val nomeMae: String,

    val celularMae: String,

    /*
     * URI da foto escolhida no aparelho.
     *
     * Pode ser null caso o membro não tenha foto.
     */
    val fotoUri: String?,

    /*
     * Em vez de apagar definitivamente um membro,
     * futuramente podemos apenas torná-lo inativo.
     *
     * Isso preserva o histórico de chamadas.
     */
    val ativo: Boolean = true
)