package com.pedro.ChamaKids.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.pedro.ChamaKids.IdGenerator

@Entity(
    tableName = "members"
)
data class MemberEntity(

    @PrimaryKey
    val serverId: String,

    val nome: String,

    val cpf: String = "",

    val rg: String = "",

    val dataNascimento: String? = null,

    val endereco: String = "",

    val celularMembro: String = "",

    val telefone: String = "",

    val nomePai: String = "",

    val celularPai: String = "",

    val nomeMae: String = "",

    val celularMae: String = "",

    val fotoUri: String? = null,

    val ativo: Boolean = true,

    // Auditoria
    val criadoPor: String? = null,
    val ultimaAlteracaoPor: String? = null,
    val lastUpdated: Long = 0
)
