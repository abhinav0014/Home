package com.abster.home.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abster.home.data.repository.RouterRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class RouterViewModel(
    private val repository: RouterRepository
) : ViewModel() {

    val isProcessing = repository.isProcessing
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val stats = repository.stats
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val abstraction = stats.map { repository.getAbstractionMapping(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), repository.getAbstractionMapping(null))

    init {
        repository.startMonitoring()
    }

    fun onToggleWifi(id: String, enabled: Boolean) {
        viewModelScope.launch {
            val status = if (enabled) "0" else "1"
            val command = "uci set wireless.$id.disabled=$status"
            repository.commitConfiguration(command)
        }
    }
}
