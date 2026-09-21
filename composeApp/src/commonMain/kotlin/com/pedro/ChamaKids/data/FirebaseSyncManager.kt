package com.pedro.ChamaKids.data

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.firestore
import kotlinx.datetime.Clock
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first

object FirebaseSyncManager {
    private val firestore by lazy { Firebase.firestore }
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    suspend fun syncMember(member: MemberEntity) {
        try {
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
            firestore.collection("members").document(member.serverId).set(data)
        } catch (_: Exception) { }
    }

    suspend fun syncAttendance(attendance: AttendanceEntity, records: List<AttendanceRecordEntity>) {
        try {
            firestore.collection("attendances").document(attendance.serverId).set(mapOf(
                "nome" to attendance.nome,
                "dataHora" to attendance.dataHora,
                "criadoPor" to attendance.criadoPor,
                "lastUpdated" to Clock.System.now().toEpochMilliseconds()
            ))
            
            val recordsCollection = firestore.collection("attendances").document(attendance.serverId).collection("records")
            records.forEach { record ->
                recordsCollection.document(record.serverId).set(mapOf(
                    "memberId" to record.memberId,
                    "presente" to record.presente,
                    "lastUpdated" to Clock.System.now().toEpochMilliseconds()
                ))
            }
        } catch (_: Exception) { }
    }

    suspend fun syncStar(star: StarRecordEntity) {
        try {
            firestore.collection("stars").document(star.serverId).set(mapOf(
                "memberId" to star.memberId,
                "dataHora" to star.dataHora,
                "comentario" to star.comentario,
                "criadoPor" to star.criadoPor,
                "lastUpdated" to Clock.System.now().toEpochMilliseconds()
            ))
        } catch (_: Exception) { }
    }

    suspend fun syncUser(user: UserEntity) {
        try {
            firestore.collection("users").document(user.serverId).set(mapOf(
                "nome" to user.nome,
                "fraseSecreta" to user.fraseSecreta,
                "lastUpdated" to user.lastUpdated
            ))
        } catch (_: Exception) { }
    }

    fun startSync(database: ChamaKidsDatabase) {
        scope.launch {
            try {
                // Membros
                firestore.collection("members").snapshots().collect { snapshot ->
                    snapshot.documents.forEach { doc ->
                        val data = doc.data<Map<String, Any?>>()
                        val member = MemberEntity(
                            serverId = doc.id,
                            nome = data["nome"] as? String ?: "",
                            cpf = data["cpf"] as? String ?: "",
                            rg = data["rg"] as? String ?: "",
                            dataNascimento = data["dataNascimento"] as? String,
                            endereco = data["endereco"] as? String ?: "",
                            celularMembro = data["celularMembro"] as? String ?: "",
                            telefone = data["telefone"] as? String ?: "",
                            nomePai = data["nomePai"] as? String ?: "",
                            celularPai = data["celularPai"] as? String ?: "",
                            nomeMae = data["nomeMae"] as? String ?: "",
                            celularMae = data["celularMae"] as? String ?: "",
                            fotoUri = data["fotoUri"] as? String,
                            ativo = data["ativo"] as? Boolean ?: true,
                            criadoPor = data["criadoPor"] as? String,
                            ultimaAlteracaoPor = data["ultimaAlteracaoPor"] as? String,
                            lastUpdated = (data["lastUpdated"] as? Number)?.toLong() ?: 0
                        )
                        database.memberDao().inserir(member)
                    }
                }
            } catch (_: Exception) { }
        }

        scope.launch {
            try {
                // Chamadas
                firestore.collection("attendances").snapshots().collect { snapshot ->
                    snapshot.documents.forEach { doc ->
                        val data = doc.data<Map<String, Any?>>()
                        val attendance = AttendanceEntity(
                            serverId = doc.id,
                            nome = data["nome"] as? String,
                            dataHora = (data["dataHora"] as? Number)?.toLong() ?: 0,
                            criadoPor = data["criadoPor"] as? String,
                            lastUpdated = (data["lastUpdated"] as? Number)?.toLong() ?: 0
                        )
                        database.attendanceDao().inserirChamada(attendance)
                        
                        // Sincronizar registros (Usando um fetch simples para evitar listeners aninhados infinitos)
                        val recordSnapshot = firestore.collection("attendances").document(doc.id).collection("records").get()
                        val records = recordSnapshot.documents.map { rDoc ->
                            val rData = rDoc.data<Map<String, Any?>>()
                            AttendanceRecordEntity(
                                serverId = rDoc.id,
                                attendanceId = doc.id,
                                memberId = rData["memberId"] as? String ?: "",
                                presente = rData["presente"] as? Boolean ?: false,
                                lastUpdated = (rData["lastUpdated"] as? Number)?.toLong() ?: 0
                            )
                        }
                        database.attendanceDao().inserirRegistros(records)
                    }
                }
            } catch (_: Exception) { }
        }

        scope.launch {
            try {
                // Estrelas
                firestore.collection("stars").snapshots().collect { snapshot ->
                    snapshot.documents.forEach { doc ->
                        val data = doc.data<Map<String, Any?>>()
                        val star = StarRecordEntity(
                            serverId = doc.id,
                            memberId = data["memberId"] as? String ?: "",
                            dataHora = (data["dataHora"] as? Number)?.toLong() ?: 0,
                            comentario = data["comentario"] as? String,
                            criadoPor = data["criadoPor"] as? String,
                            lastUpdated = (data["lastUpdated"] as? Number)?.toLong() ?: 0
                        )
                        database.starDao().inserirEstrela(star)
                    }
                }
            } catch (_: Exception) { }
        }
    }
}
