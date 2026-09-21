package com.pedro.ChamaKids.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pedro.ChamaKids.DeviceIdentifier
import com.pedro.ChamaKids.data.DatabaseProvider
import com.pedro.ChamaKids.data.FirebaseSyncManager
import com.pedro.ChamaKids.data.SecurityStateEntity
import com.pedro.ChamaKids.data.UserEntity
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlin.random.Random

class UserViewModel : ViewModel() {
    private val database = DatabaseProvider.getDatabase()
    private val userDao = database.userDao()
    private val securityDao = database.securityDao()

    val usuarios = userDao.observartodosUsuarios().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val isFirstAccess = usuarios.map { it.isEmpty() }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = true
    )

    val isFrozen = FirebaseSyncManager.isFrozen.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = false
    )

    private val _securityState = MutableStateFlow<SecurityStateEntity?>(null)
    val securityState = _securityState.asStateFlow()

    private val _isBlocked = MutableStateFlow(false)
    val isBlocked = _isBlocked.asStateFlow()

    private val _remainingTime = MutableStateFlow("")
    val remainingTime = _remainingTime.asStateFlow()

    private val _currentUser = MutableStateFlow<UserEntity?>(null)
    val currentUser = _currentUser.asStateFlow()

    private val BLOCK_TIME_MS = 35 * 60 * 1000L

    init {
        refreshSecurityState()
        startBlockCheckTimer()
    }

    fun refreshSecurityState() {
        viewModelScope.launch {
            val deviceId = DeviceIdentifier.getUniqueId()
            val state = securityDao.buscarEstado(deviceId) ?: SecurityStateEntity(deviceId)
            _securityState.value = state
            checkBlock(state)
        }
    }

    private fun checkBlock(state: SecurityStateEntity) {
        if (state.falhasConsecutivas >= 3) {
            val now = Clock.System.now().toEpochMilliseconds()
            val diff = now - state.ultimoBloqueioTimestamp
            if (diff < BLOCK_TIME_MS) {
                _isBlocked.value = true
            } else {
                _isBlocked.value = false
            }
        } else {
            _isBlocked.value = false
        }
    }

    private fun startBlockCheckTimer() {
        viewModelScope.launch {
            while (true) {
                val state = _securityState.value
                if (state != null && state.falhasConsecutivas >= 3) {
                    val now = Clock.System.now().toEpochMilliseconds()
                    val diff = now - state.ultimoBloqueioTimestamp
                    if (diff < BLOCK_TIME_MS) {
                        _isBlocked.value = true
                        val remaining = BLOCK_TIME_MS - diff
                        val minutes = (remaining / 1000) / 60
                        val seconds = (remaining / 1000) % 60
                        _remainingTime.value = "${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}"
                    } else {
                        if (_isBlocked.value) {
                            _isBlocked.value = false
                        }
                    }
                }
                delay(1000)
            }
        }
    }

    fun registrarFalha() {
        viewModelScope.launch {
            val currentState = _securityState.value ?: return@launch
            val newFalhas = currentState.falhasConsecutivas + 1
            val newTimestamp = if (newFalhas >= 3) Clock.System.now().toEpochMilliseconds() else currentState.ultimoBloqueioTimestamp
            
            val newState = currentState.copy(
                falhasConsecutivas = newFalhas,
                ultimoBloqueioTimestamp = newTimestamp
            )
            securityDao.salvarEstado(newState)
            _securityState.value = newState
            checkBlock(newState)
        }
    }

    fun resetFalhas() {
        viewModelScope.launch {
            val currentState = _securityState.value ?: return@launch
            val newState = currentState.copy(falhasConsecutivas = 0, ultimoBloqueioTimestamp = 0)
            securityDao.salvarEstado(newState)
            _securityState.value = newState
            _isBlocked.value = false
        }
    }

    fun cadastrarUsuario(nome: String, frase: String) {
        viewModelScope.launch {
            val user = UserEntity(
                serverId = com.pedro.ChamaKids.IdGenerator.generate(),
                nome = nome,
                fraseSecreta = frase,
                lastUpdated = Clock.System.now().toEpochMilliseconds()
            )
            userDao.inserir(user)
            FirebaseSyncManager.syncUser(user)
        }
    }

    fun autenticar(usuario: UserEntity) {
        _currentUser.value = usuario
    }
    
    fun logout() {
        _currentUser.value = null
    }

    // --- MODO SOFTWARE ---

    fun toggleFreezeApp() {
        viewModelScope.launch {
            FirebaseSyncManager.setFrozen(!isFrozen.value)
        }
    }

    fun toggleUserBlock(user: UserEntity) {
        viewModelScope.launch {
            val updated = user.copy(bloqueado = !user.bloqueado)
            userDao.inserir(updated)
            FirebaseSyncManager.syncUser(updated)
        }
    }

    fun factoryReset() {
        viewModelScope.launch {
            FirebaseSyncManager.factoryReset(database)
            _currentUser.value = null
        }
    }

    fun gerarOpcoesAleatorias(correta: String): List<String> {
        val palavras = listOf(
            "Sol", "Lua", "Mar", "Flor", "Céu", "Nuvem", "Vento", "Fogo", "Terra", "Água",
            "Monte", "Vale", "Pedra", "Rio", "Estrela", "Pássaro", "Árvore", "Fruta", "Bicho", "Peixe",
            "Luz", "Caminho", "Sonho", "Vida", "Amor", "Paz", "Fé", "Esperança", "Coragem", "Alegria"
        )
        val adjetivos = listOf(
            "Azul", "Branco", "Verde", "Doce", "Forte", "Suave", "Livre", "Alto", "Passageiro", "Radiante",
            "Profundo", "Cristalina", "Preciosa", "Encantado", "Ardente", "Crescente", "Dourado", "Brilhante",
            "Calmo", "Sereno", "Puro", "Claro", "Escuro", "Novo", "Antigo", "Belíssimo"
        )
        
        val distrações = mutableSetOf<String>()
        while (distrações.size < 4) {
            val tipo = Random.nextInt(3)
            val item = when(tipo) {
                0 -> palavras.random()
                1 -> "${palavras.random()} ${adjetivos.random()}"
                else -> "${palavras.random()} ${palavras.random().lowercase()} ${adjetivos.random().lowercase()}"
            }
            if (item != correta && item.length < 30) distrações.add(item)
        }
        
        return (distrações.toList() + correta).shuffled()
    }
}
