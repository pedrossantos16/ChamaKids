package com.pedro.ChamaKids.data

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.firestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.first
import kotlinx.datetime.Clock

object FirebaseSyncManager {
    private val firestore = Firebase.firestore
    private val scope = CoroutineScope(Dispatchers.IO)
    private var database: ChamaKidsDatabase? = null

    fun startSync(db: ChamaKidsDatabase) {
        database = db
        
        // Listener para Membros
        scope.launch {
            try {
                firestore.collection("members").snapshots.collect { snapshot ->
                    snapshot.documents.forEach { doc ->
                        val serverId = doc.id
                        val remoteLastUpdated = try { doc.get<Long>("lastUpdated") } catch(e: Exception) { 0L }
                        
                        // Busca local por ID da nuvem
                        var localMember = db.memberDao().buscarPorServerId(serverId)
                        
                        // Se não achou por ID da nuvem, tenta por Nome e CPF (evita duplicidade de membros novos)
                        if (localMember == null) {
                            val nome = doc.get<String>("nome")
                            val cpf = doc.get<String>("cpf")
                            localMember = db.memberDao().buscarPorNomeECpf(nome, cpf)
                        }
                        
                        if (localMember == null || remoteLastUpdated > localMember.lastUpdated) {
                            val member = MemberEntity(
                                id = localMember?.id ?: 0,
                                nome = doc.get("nome"),
                                cpf = doc.get("cpf"),
                                rg = doc.get("rg"),
                                dataNascimento = doc.get("dataNascimento"),
                                endereco = doc.get("endereco"),
                                celularMembro = doc.get("celularMembro"),
                                telefone = doc.get("telefone"),
                                nomePai = doc.get("nomePai"),
                                celularPai = doc.get("celularPai"),
                                nomeMae = doc.get("nomeMae"),
                                celularMae = doc.get("celularMae"),
                                fotoUri = doc.get("fotoUri"),
                                ativo = doc.get("ativo"),
                                serverId = serverId,
                                lastUpdated = remoteLastUpdated
                            )
                            db.memberDao().inserir(member)
                        }
                    }
                }
            } catch (e: Exception) { }
        }

        // Listener para Chamadas
        scope.launch {
            try {
                firestore.collection("attendances").snapshots.collect { snapshot ->
                    snapshot.documents.forEach { doc ->
                        val serverId = doc.id
                        val remoteLastUpdated = try { doc.get<Long>("lastUpdated") } catch(e: Exception) { 0L }
                        
                        val localAttendance = db.attendanceDao().buscarPorServerId(serverId)
                        
                        if (localAttendance == null) {
                            val attendance = AttendanceEntity(
                                nome = doc.get("nome"),
                                dataHora = doc.get("dataHora"),
                                serverId = serverId,
                                lastUpdated = remoteLastUpdated
                            )
                            val newId = db.attendanceDao().inserirChamada(attendance).toInt()
                            
                            val recordsSnapshot = doc.reference.collection("records").get()
                            val localRecords = recordsSnapshot.documents.map { recDoc ->
                                val remoteMemberServerId = recDoc.get<String?>("memberServerId")
                                val localMember = remoteMemberServerId?.let { db.memberDao().buscarPorServerId(it) }
                                AttendanceRecordEntity(
                                    attendanceId = newId,
                                    memberId = localMember?.id ?: 0,
                                    presente = recDoc.get("presente")
                                )
                            }
                            db.attendanceDao().inserirRegistros(localRecords)
                        }
                    }
                }
            } catch (e: Exception) { }
        }

        // Upload inicial
        scope.launch {
            try {
                val members = db.memberDao().observarTodosMembros().first()
                members.forEach { member ->
                    if (member.serverId == null) {
                        syncMember(member)
                    }
                }
            } catch (e: Exception) { }
        }
    }
    
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
                "lastUpdated" to Clock.System.now().toEpochMilliseconds()
            )
            
            if (member.serverId == null) {
                val doc = collection.add(data)
                database?.memberDao()?.atualizarServerId(member.id, doc.id)
            } else {
                collection.document(member.serverId).set(data)
            }
        } catch (e: Exception) { }
    }

    suspend fun syncAttendance(attendance: AttendanceEntity, records: List<AttendanceRecordEntity>) {
        try {
            val collection = firestore.collection("attendances")
            val now = Clock.System.now().toEpochMilliseconds()
            
            val data = mapOf(
                "nome" to attendance.nome,
                "dataHora" to attendance.dataHora,
                "lastUpdated" to now
            )

            if (attendance.serverId == null) {
                val docRef = collection.add(data)
                val recordsCollection = docRef.collection("records")
                records.forEach { record ->
                    val member = database?.memberDao()?.buscarPorId(record.memberId)
                    recordsCollection.add(mapOf(
                        "memberServerId" to member?.serverId,
                        "presente" to record.presente,
                        "lastUpdated" to now
                    ))
                }
                database?.attendanceDao()?.atualizarServerId(attendance.id, docRef.id)
            } else {
                collection.document(attendance.serverId).set(data)
            }
        } catch (e: Exception) { }
    }
}
