package com.abster.home.service

import com.abster.home.model.ConnectedDevice
import com.jcraft.jsch.ChannelExec
import com.jcraft.jsch.JSch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Properties

object RouterService {
    suspend fun fetchDevices(host: String, user: String, pass: String): List<ConnectedDevice> = withContext(Dispatchers.IO) {
        if (pass.isEmpty()) return@withContext emptyList()
        
        try {
            val jsch = JSch()
            val session = jsch.getSession(user, host, 22)
            session.setPassword(pass.toByteArray())
            val config = Properties()
            config["StrictHostKeyChecking"] = "no"
            session.setConfig(config)
            session.connect(5000)

            val channel = session.openChannel("exec") as ChannelExec
            channel.setCommand("cat /proc/net/arp")
            val inputStream = channel.inputStream
            channel.connect()

            val arpOutput = inputStream.bufferedReader().use { it.readText() }
            channel.disconnect()
            session.disconnect()

            val lines = arpOutput.lines().drop(1)
            lines.filter { it.isNotBlank() }.map { line ->
                val parts = line.split(Regex("\\s+")).filter { it.isNotBlank() }
                if (parts.size >= 6) {
                    ConnectedDevice(
                        name = "Device ${parts[0].takeLast(2)}",
                        ip = parts[0],
                        mac = parts[3],
                        interfaceName = parts[5]
                    )
                } else null
            }.filterNotNull()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}
