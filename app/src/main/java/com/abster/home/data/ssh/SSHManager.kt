package com.abster.home.data.ssh

import com.jcraft.jsch.ChannelExec
import com.jcraft.jsch.JSch
import com.jcraft.jsch.Session
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.util.Properties

class SSHManager(
    private val host: String,
    private val port: Int = 22,
    private val user: String,
    private val pass: String
) {
    private var session: Session? = null

    suspend fun connect() = withContext(Dispatchers.IO) {
        if (session?.isConnected == true) return@withContext
        
        val jsch = JSch()
        session = jsch.getSession(user, host, port)
        session?.setPassword(pass.toByteArray())
        
        val config = Properties()
        config["StrictHostKeyChecking"] = "no"
        session?.setConfig(config)
        
        session?.connect(30000) // 30s timeout
    }

    suspend fun executeCommand(command: String): String = withContext(Dispatchers.IO) {
        connect()
        val channel = session?.openChannel("exec") as? ChannelExec
            ?: return@withContext "Failed to open channel"
        
        channel.setCommand(command)
        val inputStream: InputStream = channel.inputStream
        channel.connect()
        
        val output = inputStream.bufferedReader().use { it.readText() }
        channel.disconnect()
        output
    }

    fun disconnect() {
        session?.disconnect()
        session = null
    }
}
