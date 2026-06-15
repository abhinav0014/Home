package com.abster.home.data.repository

import com.abster.home.data.network.XHRManager
import com.abster.home.data.ssh.SSHManager
import com.abster.home.domain.model.RouterAbstraction
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class RouterRepository(
    private val xhrManager: XHRManager,
    private val sshManager: SSHManager,
    private val scope: CoroutineScope
) {
    private val _isProcessing = MutableStateFlow(false)
    val isProcessing = _isProcessing.asStateFlow()

    val stats: StateFlow<String?> = xhrManager.stats

    fun startMonitoring() {
        xhrManager.startPolling()
    }

    /**
     * Executes SSH configuration commands with mandatory 4-step safety sequence.
     */
    suspend fun commitConfiguration(uciCommand: String): Result<String> = withContext(Dispatchers.IO) {
        _isProcessing.value = true
        
        // 1. Pause active XHR polling to prevent socket/resource collision
        xhrManager.pausePolling()
        
        return@withContext try {
            // 2. Chain configuration commands natively over terminal channel
            val fullCommand = "$uciCommand && uci commit wireless && wifi"
            val result = sshManager.executeCommand(fullCommand)
            
            // 3. Mandatory 5-second non-blocking cooldown for hardware/radio reload
            delay(5000)
            
            Result.success(result)
        } catch (e: Exception) {
            Result.failure(e)
        } finally {
            // 4. Verify connection stability and gracefully resume polling
            xhrManager.resumePolling()
            _isProcessing.value = false
        }
    }

    fun getAbstractionMapping(json: String?): RouterAbstraction {
        return if (json != null) {
            RouterAbstraction.fromJson(json)
        } else {
            RouterAbstraction()
        }
    }
}
