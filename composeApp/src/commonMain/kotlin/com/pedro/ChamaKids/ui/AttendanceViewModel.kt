package com.pedro.ChamaKids.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pedro.ChamaKids.data.AttendanceRepository
import com.pedro.ChamaKids.data.DatabaseProvider
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

class AttendanceViewModel : ViewModel() {

    private val database = DatabaseProvider.getDatabase()
    private val repository = AttendanceRepository(database)

    private val _frequencias = MutableStateFlow<Map<Int, Float?>>(emptyMap())
    val frequencias: StateFlow<Map<Int, Float?>> = _frequencias.asStateFlow()

    val chamadas = repository.chamadas.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    fun salvarChamada(nome: String?, presencas: Map<Int, Boolean>, onSucesso: () -> Unit = {}) {
        viewModelScope.launch {
            repository.salvarChamada(nome, presencas)
            onSucesso()
        }
    }

    suspend fun buscarRegistrosDaChamada(chamadaId: Int) = repository.buscarRegistros(chamadaId)
    suspend fun buscarChamadaPorId(id: Int) = repository.buscarChamadaPorId(id)

    fun carregarFrequencias(membrosIds: List<Int>) {
        viewModelScope.launch {
            val resultado = mutableMapOf<Int, Float?>()
            membrosIds.forEach { memberId ->
                resultado[memberId] = repository.calcularFrequencia(memberId)
            }
            _frequencias.value = resultado
        }
    }

    suspend fun buscarHistorico(memberId: Int) = repository.buscarHistorico(memberId)
    suspend fun calcularFrequencia(memberId: Int) = repository.calcularFrequencia(memberId)

    fun excluirChamadas(ids: List<Int>) {
        viewModelScope.launch {
            repository.excluirChamadas(ids)
        }
    }

    suspend fun buscarMembroMaisPresente(inicio: Long, fim: Long) = repository.buscarMembroMaisPresente(inicio, fim)
    suspend fun buscarDiaMaiorAssiduidade(inicio: Long, fim: Long) = repository.buscarDiaMaiorAssiduidade(inicio, fim)
}
