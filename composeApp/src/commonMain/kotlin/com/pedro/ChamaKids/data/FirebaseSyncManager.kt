package com.pedro.ChamaKids.data

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.firestore
import kotlinx.coroutines.flow.first
import kotlinx.datetime.Clock

object FirebaseSyncManager {
    private val firestore = Firebase.firestore
    
    suspend fun syncMember(member: MemberEntity) {
        try {
            val collection = firestore.collection("members")
            val data = mapOf(
                "nome" to member.nome,
                "cpf" to member.cpf,
                "rg" to member.rg,
                "dataNascimento" to member.dataNascimento,
                "endereco" to member.endereco,
                "celularMembro" to member.celularMembro,
                "telefone" to member.telefone,
                "nomePai" to member.nomePai,
                "celularPai" to member.celularPai,
                "nomeMae" to member.nomeMae,
                "celularMae" to member.celularMae,
                "fotoUri" to member.fotoUri,
                "ativo" to member.ativo,
                "criadoPor" to member.criadoPor,
                "ultimaAlteracaoPor" to member.ultimaAlteracaoPor,
                "lastUpdated" to Clock.System.now().toEpochMilliseconds()
            )
            
            if (member.serverId == null) {
                val doc = collection.add(data)
                // O ideal seria atualizar o Room com o serverId aqui
            } else {
                collection.document(member.serverId).set(data)
            }
        } catch (e: Exception) {
            // Log error
        }
    }

    suspend fun syncAttendance(attendance: AttendanceEntity, records: List<AttendanceRecordEntity>) {
        try {
            val attendanceDoc = firestore.collection("attendances").add(mapOf(
                "nome" to attendance.nome,
                "dataHora" to attendance.dataHora,
                "criadoPor" to attendance.criadoPor,
                "lastUpdated" to Clock.System.now().toEpochMilliseconds()
            ))
            
            val recordsCollection = attendanceDoc.collection("records")
            records.forEach { record ->
                recordsCollection.add(mapOf(
                    "memberId" to record.memberId,
                    "presente" to record.presente,
                    "lastUpdated" to Clock.System.now().toEpochMilliseconds()
                ))
            }
        } catch (e: Exception) {
            // Log error
        }
    }
}
