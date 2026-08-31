package com.pedro.ChamaKids.ui

import android.app.Application

import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope

import com.pedro.ChamaKids.data.AttendanceRepository
import com.pedro.ChamaKids.data.ChamaKidsDatabase

import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AttendanceViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val database =
        ChamaKidsDatabase
            .getDatabase(
                application
            )

    private val repository =
        AttendanceRepository(
            database
        )

    private val _frequencias =
        MutableStateFlow<Map<Int, Float?>>(
            emptyMap()
        )

    val frequencias: StateFlow<Map<Int, Float?>> =
        _frequencias.asStateFlow()

    val chamadas =
        repository
            .chamadas
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(
                    5_000
                ),
                initialValue = emptyList()
            )


    /**
     * Salva a chamada e executa onSucesso
     * somente depois de concluir o banco.
     */
    fun salvarChamada(
        presencas: Map<Int, Boolean>,
        onSucesso: () -> Unit = {}
    ) {

        viewModelScope.launch {

            repository
                .salvarChamada(
                    presencas
                )

            onSucesso()
        }
    }

    suspend fun buscarRegistrosDaChamada(
        chamadaId: Int
    ) = repository.buscarRegistros(
        chamadaId
    )

    fun carregarFrequencias(
        membrosIds: List<Int>
    ) {
        viewModelScope.launch {

            val resultado =
                mutableMapOf<Int, Float?>()

            membrosIds.forEach { memberId ->

                resultado[memberId] =
                    repository.calcularFrequencia(
                        memberId
                    )
            }

            _frequencias.value = resultado
        }
    }
}