package org.ReDiego0.myElytras

import org.ReDiego0.myElytras.config.ElytraConfigLoader
import org.ReDiego0.myElytras.listener.ElytraMechanicListener
import org.ReDiego0.myElytras.task.ElytraTask
import org.bukkit.plugin.java.JavaPlugin

class MyElytras : JavaPlugin() {
    private lateinit var configLoader: ElytraConfigLoader

    override fun onEnable() {
        saveDefaultConfig()

        configLoader = ElytraConfigLoader(this)
        configLoader.load()

        server.pluginManager.registerEvents(ElytraMechanicListener(configLoader), this)
        ElytraTask(configLoader).runTaskTimer(this, 0L, 20L)

        logger.info("MyElytras ha sido habilitado con éxito (≧◡≦) ♡")
    }

    override fun onDisable() {
        logger.info("MyElytras desactivado.")
    }

    fun getElytraLoader() = configLoader
}