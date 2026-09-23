package com.pedro.ChamaKids.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pedro.ChamaKids.data.AttendanceEntity
import com.pedro.ChamaKids.data.AttendanceRecordEntity
import com.pedro.ChamaKids.data.AttendanceRepository
import com.pedro.ChamaKids.data.DatabaseProvider
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

class AttendanceViewModel : ViewModel() {

    private val database = DatabaseProvider.getDatabase()
    private val repository = AttendanceRepository(database.attendanceDao())

    private val _frequencias = MutableStateFlow<Map<String, Float?>>(emptyMap())
    val frequencias: StateFlow<Map<String, Float?>> = _frequencias.asStateFlow()

    val chamadas = repository.chamadas.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    val acoes = database.actionLogDao().observarAcoes().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    fun salvarChamada(
        nome: String?,
        presencas: Map<String, Boolean>,
        criadoPor: String?,
        dataHora: Long = Clock.System.now().toEpochMilliseconds(),
        onSucesso: () -> Unit = {}
    ) {
        viewModelScope.launch {
            val chamadaId = com.pedro.ChamaKids.IdGenerator.generate()
            val chamada = AttendanceEntity(
                serverId = chamadaId,
                nome = nome,
                dataHora = dataHora,
                criadoPor = criadoPor
            )
            val registros = presencas.map { (memberId, presente) ->
                AttendanceRecordEntity(
                    serverId = com.pedro.ChamaKids.IdGenerator.generate(),
                    attendanceId = chamada.serverId,
                    memberId = memberId,
                    presente = presente
                )
            }
            repository.salvarChamada(chamada, registros)
            val totalPresentes = presencas.count { it.value }
            val nomeChamada = if (!nome.isNullOrBlank()) "'$nome'" else "Sem título"
            com.pedro.ChamaKids.data.ActionLogManager.registrarAcao(
                tipoAcao = "Chamada Realizada",
                descricao = "Realizou a chamada $nomeChamada com $totalPresentes presente(s)",
                usuarioNome = criadoPor
            )
            onSucesso()
        }
    }

    suspend fun buscarRegistrosDaChamada(chamadaId: String) = repository.buscarRegistrosDaChamada(chamadaId)
    
    suspend fun buscarChamadaPorId(serverId: String): AttendanceEntity? {
        return database.attendanceDao().buscarPorServerId(serverId)
    }

    fun carregarFrequencias(membrosIds: List<String>) {
        viewModelScope.launch {
            val resultado = mutableMapOf<String, Float?>()
            membrosIds.forEach { memberId ->
                val presencas = repository.contarPresencasDoMembro(memberId)
                val total = repository.contarChamadasDoMembro(memberId)
                resultado[memberId] = if (total > 0) presencas.toFloat() / total else null
            }
            _frequencias.value = resultado
        }
    }

    suspend fun buscarHistorico(memberId: String) = repository.buscarHistoricoDoMembro(memberId)

    suspend fun calcularFrequencia(memberId: String): Float? {
        val presencas = repository.contarPresencasDoMembro(memberId)
        val total = repository.contarChamadasDoMembro(memberId)
        return if (total > 0) presencas.toFloat() / total else null
    }

    fun excluirChamadas(ids: List<String>) {
        viewModelScope.launch {
            repository.excluirChamadas(ids)
        }
    }

    fun excluirAcoes(ids: List<String>) {
        viewModelScope.launch {
            database.actionLogDao().excluirAcoes(ids)
        }
    }

    suspend fun buscarMembroMaisPresente(inicio: Long, fim: Long) = repository.buscarMembroMaisPresente(inicio, fim)
    suspend fun buscarDiaMaiorAssiduidade(inicio: Long, fim: Long) = repository.buscarDiaMaiorAssiduidade(inicio, fim)
}
