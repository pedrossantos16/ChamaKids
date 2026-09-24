package com.pedro.ChamaKids.data

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.firestore
import kotlinx.datetime.Clock
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.serialization.Serializable

@Serializable
private data class UserDoc(
    val nome: String = "",
    val fraseSecreta: String = "",
    val lastUpdated: Long = 0,
    val bloqueado: Boolean = false
)

@Serializable
private data class MemberDoc(
    val nome: String = "",
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
    val criadoPor: String? = null,
    val ultimaAlteracaoPor: String? = null,
    val lastUpdated: Long = 0
)

@Serializable
private data class AttendanceDoc(
    val nome: String? = null,
    val dataHora: Long = 0,
    val criadoPor: String? = null,
    val lastUpdated: Long = 0
)

@Serializable
private data class RecordDoc(
    val memberId: String = "",
    val presente: Boolean = false,
    val lastUpdated: Long = 0
)

@Serializable
private data class StarDoc(
    val memberId: String = "",
    val dataHora: Long = 0,
    val comentario: String? = null,
    val criadoPor: String? = null,
    val lastUpdated: Long = 0
)

@Serializable
private data class AppConfigDoc(
    val isFrozen: Boolean = false
)

@Serializable
private data class ActionLogDoc(
    val usuarioNome: String = "",
    val tipoAcao: String = "",
    val descricao: String = "",
    val dataHora: Long = 0,
    val lastUpdated: Long = 0
)

object FirebaseSyncManager {
    private val firestore by lazy { Firebase.firestore }
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    
    private val _syncing = MutableStateFlow(false)
    val syncing: StateFlow<Boolean> = _syncing.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _isFrozen = MutableStateFlow(false)
    val isFrozen: StateFlow<Boolean> = _isFrozen.asStateFlow()

    private fun toLong(value: Any?): Long {
        return when (value) {
            is Number -> value.toLong()
            is String -> value.toLongOrNull() ?: 0L
            else -> 0L
        }
    }

    suspend fun syncMember(member: MemberEntity) {
        try {
            val doc = MemberDoc(
                nome = member.nome,
                cpf = member.cpf,
                rg = member.rg,
                dataNascimento = member.dataNascimento,
                endereco = member.endereco,
                celularMembro = member.celularMembro,
                telefone = member.telefone,
                nomePai = member.nomePai,
                celularPai = member.celularPai,
                nomeMae = member.nomeMae,
                celularMae = member.celularMae,
                fotoUri = member.fotoUri,
                ativo = member.ativo,
                criadoPor = member.criadoPor,
                ultimaAlteracaoPor = member.ultimaAlteracaoPor,
                lastUpdated = Clock.System.now().toEpochMilliseconds()
            )
            firestore.collection("members").document(member.serverId).set(MemberDoc.serializer(), doc)
        } catch (e: Exception) {
            _errorMessage.value = "Erro membro: ${e.message}"
        }
    }

    suspend fun syncUser(user: UserEntity) {
        try {
            val doc = UserDoc(user.nome, user.fraseSecreta, user.lastUpdated, user.bloqueado)
            firestore.collection("users").document(user.serverId).set(UserDoc.serializer(), doc)
        } catch (e: Exception) {
            _errorMessage.value = "Erro usuário: ${e.message}"
        }
    }

    suspend fun syncAttendance(attendance: AttendanceEntity, records: List<AttendanceRecordEntity>) {
        try {
            val aDoc = AttendanceDoc(attendance.nome, attendance.dataHora, attendance.criadoPor, Clock.System.now().toEpochMilliseconds())
            firestore.collection("attendances").document(attendance.serverId).set(AttendanceDoc.serializer(), aDoc)
            
            val recordsCollection = firestore.collection("attendances").document(attendance.serverId).collection("records")
            records.forEach { record ->
                val rDoc = RecordDoc(record.memberId, record.presente, Clock.System.now().toEpochMilliseconds())
                recordsCollection.document(record.serverId).set(RecordDoc.serializer(), rDoc)
            }
        } catch (e: Exception) {
            _errorMessage.value = "Erro chamada: ${e.message}"
        }
    }

    suspend fun syncStar(star: StarRecordEntity) {
        try {
            val sDoc = StarDoc(star.memberId, star.dataHora, star.comentario, star.criadoPor, Clock.System.now().toEpochMilliseconds())
            firestore.collection("stars").document(star.serverId).set(StarDoc.serializer(), sDoc)
        } catch (e: Exception) {
            _errorMessage.value = "Erro estrela: ${e.message}"
        }
    }

    suspend fun syncActionLog(log: ActionLogEntity) {
        try {
            val doc = ActionLogDoc(log.usuarioNome, log.tipoAcao, log.descricao, log.dataHora, log.lastUpdated)
            firestore.collection("action_logs").document(log.serverId).set(ActionLogDoc.serializer(), doc)
        } catch (e: Exception) {
            _errorMessage.value = "Erro log ação: ${e.message}"
        }
    }

    suspend fun setFrozen(frozen: Boolean) {
        try {
            firestore.collection("app_config").document("global").set(AppConfigDoc.serializer(), AppConfigDoc(frozen))
        } catch (e: Exception) {
            _errorMessage.value = "Erro congelar: ${e.message}"
        }
    }

    fun startSync(database: ChamaKidsDatabase) {
        _errorMessage.value = null
        
        // Monitor de Configuração Global (Congelamento)
        scope.launch {
            try {
                firestore.collection("app_config").document("global").snapshots().collect { doc ->
                    if (doc.exists) {
                        val config = doc.data(AppConfigDoc.serializer())
                        _isFrozen.value = config.isFrozen
                    }
                }
            } catch (_: Exception) { }
        }

        // Monitor de Usuários
        scope.launch {
            try {
                firestore.collection("users").snapshots().collect { snapshot ->
                    val remoteIds = snapshot.documents.map { it.id }.toSet()
                    snapshot.documents.forEach { doc ->
                        try {
                            val data = doc.data(UserDoc.serializer())
                            val user = UserEntity(
                                serverId = doc.id,
                                nome = data.nome,
                                fraseSecreta = data.fraseSecreta,
                                lastUpdated = data.lastUpdated,
                                bloqueado = data.bloqueado
                            )
                            database.userDao().inserir(user)
                        } catch (_: Exception) {}
                    }
                    database.userDao().todosUsuarios().forEach { local ->
                        if (local.serverId !in remoteIds) {
                            database.userDao().excluirPorServerId(local.serverId)
                        }
                    }
                }
            } catch (e: Exception) { _errorMessage.value = "Sinc usuários: ${e.message}" }
        }

        // Monitor de Membros
        scope.launch {
            try {
                firestore.collection("members").snapshots().collect { snapshot ->
                    _syncing.value = true
                    val remoteIds = snapshot.documents.map { it.id }.toSet()
                    snapshot.documents.forEach { doc ->
                        try {
                            val data = doc.data(MemberDoc.serializer())
                            val member = MemberEntity(
                                serverId = doc.id,
                                nome = data.nome,
                                cpf = data.cpf,
                                rg = data.rg,
                                dataNascimento = data.dataNascimento,
                                endereco = data.endereco,
                                celularMembro = data.celularMembro,
                                telefone = data.telefone,
                                nomePai = data.nomePai,
                                celularPai = data.celularPai,
                                nomeMae = data.nomeMae,
                                celularMae = data.celularMae,
                                fotoUri = data.fotoUri,
                                ativo = data.ativo,
                                criadoPor = data.criadoPor,
                                ultimaAlteracaoPor = data.ultimaAlteracaoPor,
                                lastUpdated = data.lastUpdated
                            )
                            database.memberDao().inserir(member)
                        } catch (_: Exception) {}
                    }
                    database.memberDao().buscarTodos().forEach { local ->
                        if (local.serverId !in remoteIds) {
                            database.memberDao().excluirPorServerId(local.serverId)
                        }
                    }
                    _syncing.value = false
                }
            } catch (e: Exception) { _errorMessage.value = "Sinc membros: ${e.message}" }
        }

        // Monitor de Chamadas (Com records reativos)
        scope.launch {
            try {
                firestore.collection("attendances").snapshots().collect { snapshot ->
                    val remoteIds = snapshot.documents.map { it.id }.toSet()
                    snapshot.documents.forEach { doc ->
                        try {
                            val data = doc.data(AttendanceDoc.serializer())
                            val attendance = AttendanceEntity(
                                serverId = doc.id,
                                nome = data.nome,
                                dataHora = data.dataHora,
                                criadoPor = data.criadoPor,
                                lastUpdated = data.lastUpdated
                            )
                            database.attendanceDao().inserirChamada(attendance)
                            
                            // Inicia um listener para os registros desta chamada específica
                            scope.launch {
                                firestore.collection("attendances").document(doc.id).collection("records").snapshots().collect { rSnapshot ->
                                    val entities = rSnapshot.documents.map { rDoc ->
                                        val rData = rDoc.data(RecordDoc.serializer())
                                        AttendanceRecordEntity(
                                            serverId = rDoc.id,
                                            attendanceId = doc.id,
                                            memberId = rData.memberId,
                                            presente = rData.presente,
                                            lastUpdated = rData.lastUpdated
                                        )
                                    }
                                    database.attendanceDao().inserirRegistros(entities)
                                }
                            }
                        } catch (_: Exception) {}
                    }
                    database.attendanceDao().buscarTodasChamadas().forEach { local ->
                        if (local.serverId !in remoteIds) {
                            database.attendanceDao().excluirChamadas(listOf(local.serverId))
                        }
                    }
                }
            } catch (e: Exception) { _errorMessage.value = "Sinc chamadas: ${e.message}" }
        }

        // Monitor de Estrelas
        scope.launch {
            try {
                firestore.collection("stars").snapshots().collect { snapshot ->
                    val remoteIds = snapshot.documents.map { it.id }.toSet()
                    snapshot.documents.forEach { doc ->
                        try {
                            val data = doc.data(StarDoc.serializer())
                            val star = StarRecordEntity(
                                serverId = doc.id,
                                memberId = data.memberId,
                                dataHora = data.dataHora,
                                comentario = data.comentario,
                                criadoPor = data.criadoPor,
                                lastUpdated = data.lastUpdated
                            )
                            database.starDao().inserirEstrela(star)
                        } catch (_: Exception) {}
                    }
                    database.starDao().buscarTodasEstrelas().forEach { local ->
                        if (local.serverId !in remoteIds) {
                            database.starDao().excluirPorServerId(local.serverId)
                        }
                    }
                }
            } catch (e: Exception) { _errorMessage.value = "Sinc estrelas: ${e.message}" }
        }

        // Monitor de Log de Ações
        scope.launch {
            try {
                firestore.collection("action_logs").snapshots().collect { snapshot ->
                    val remoteIds = snapshot.documents.map { it.id }.toSet()
                    snapshot.documents.forEach { doc ->
                        try {
                            val data = doc.data(ActionLogDoc.serializer())
                            val log = ActionLogEntity(
                                serverId = doc.id,
                                usuarioNome = data.usuarioNome,
                                tipoAcao = data.tipoAcao,
                                descricao = data.descricao,
                                dataHora = data.dataHora,
                                lastUpdated = data.lastUpdated
                            )
                            database.actionLogDao().inserir(log)
                        } catch (_: Exception) {}
                    }
                    database.actionLogDao().buscarTodasAcoes().forEach { local ->
                        if (local.serverId !in remoteIds) {
                            database.actionLogDao().excluirPorServerId(local.serverId)
                        }
                    }
                }
            } catch (e: Exception) { _errorMessage.value = "Sinc ações: ${e.message}" }
        }
    }
    
    suspend fun deleteUser(serverId: String) {
        try {
            firestore.collection("users").document(serverId).delete()
        } catch (e: Exception) {
            _errorMessage.value = "Erro ao excluir usuário: ${e.message}"
        }
    }

    suspend fun deleteStar(serverId: String) {
        try {
            firestore.collection("stars").document(serverId).delete()
        } catch (e: Exception) {
            _errorMessage.value = "Erro ao excluir estrela: ${e.message}"
        }
    }

    suspend fun deleteAttendance(serverId: String) {
        try {
            val recSnapshot = firestore.collection("attendances").document(serverId).collection("records").get()
            recSnapshot.documents.forEach { rDoc ->
                firestore.collection("attendances").document(serverId).collection("records").document(rDoc.id).delete()
            }
            firestore.collection("attendances").document(serverId).delete()
        } catch (e: Exception) {
            _errorMessage.value = "Erro ao excluir chamada na nuvem: ${e.message}"
        }
    }

    suspend fun deleteActionLog(serverId: String) {
        try {
            firestore.collection("action_logs").document(serverId).delete()
        } catch (e: Exception) {
            _errorMessage.value = "Erro ao excluir ação na nuvem: ${e.message}"
        }
    }

    suspend fun clearModuleMembers(database: ChamaKidsDatabase) {
        database.memberDao().limparTodos()
        clearFirestoreCollection("members")
    }

    suspend fun clearModuleAttendances(database: ChamaKidsDatabase) {
        database.attendanceDao().limparTodasChamadas()
        database.attendanceDao().limparTodosRegistros()
        clearFirestoreCollection("attendances")
    }

    suspend fun clearModuleStars(database: ChamaKidsDatabase) {
        database.starDao().limparTodasEstrelas()
        clearFirestoreCollection("stars")
    }

    suspend fun clearModuleActionLogs(database: ChamaKidsDatabase) {
        database.actionLogDao().limparTudo()
        clearFirestoreCollection("action_logs")
    }

    suspend fun clearModuleUsers(database: ChamaKidsDatabase) {
        database.userDao().limparTodos()
        clearFirestoreCollection("users")
    }

    private suspend fun clearFirestoreCollection(collectionName: String) {
        try {
            val snapshot = firestore.collection(collectionName).get()
            snapshot.documents.forEach { doc ->
                if (collectionName == "attendances") {
                    try {
                        val recSnapshot = firestore.collection("attendances").document(doc.id).collection("records").get()
                        recSnapshot.documents.forEach { rDoc ->
                            firestore.collection("attendances").document(doc.id).collection("records").document(rDoc.id).delete()
                        }
                    } catch (_: Exception) {}
                }
                firestore.collection(collectionName).document(doc.id).delete()
            }
        } catch (_: Exception) {}
    }

    suspend fun factoryReset(database: ChamaKidsDatabase) {
        // 1. Limpa banco de dados local (Room) IMEDIATAMENTE
        try {
            database.userDao().limparTodos()
            database.memberDao().limparTodos()
            database.attendanceDao().limparTodasChamadas()
            database.attendanceDao().limparTodosRegistros()
            database.starDao().limparTodasEstrelas()
            database.securityDao().limparTudo()
            database.actionLogDao().limparTudo()
        } catch (_: Exception) {}

        // 2. Descongela aplicativo se estivesse congelado
        try {
            setFrozen(false)
        } catch (_: Exception) {}

        // 3. Limpa Firestore Cloud em segundo plano
        val collections = listOf("members", "attendances", "stars", "users", "action_logs")
        collections.forEach { coll ->
            clearFirestoreCollection(coll)
        }
    }
}
