package org.ReDiego0.myElytras

import org.ReDiego0.myElytras.command.GiveCommand
import org.ReDiego0.myElytras.config.ElytraConfigLoader
import org.ReDiego0.myElytras.listener.ElytraMechanicListener
import org.ReDiego0.myElytras.manager.ElytraVisualManager
import org.ReDiego0.myElytras.task.ElytraTask
import org.bukkit.plugin.java.JavaPlugin

class MyElytras : JavaPlugin() {

    private lateinit var configLoader: ElytraConfigLoader
    private lateinit var visualManager: ElytraVisualManager

    override fun onEnable() {
        saveDefaultConfig()
        configLoader = ElytraConfigLoader(this)
        configLoader.load()

        visualManager = ElytraVisualManager(configLoader)

        getCommand("myelytra")?.setExecutor(GiveCommand(configLoader))
        server.pluginManager.registerEvents(ElytraMechanicListener(configLoader), this)

        // 1L, 1L = Correr cada tick (0.05s).
        ElytraTask(configLoader, visualManager).runTaskTimer(this, 0L, 1L)

        logger.info("MyElytras activado con Mini-Engine nativo (≧◡≦) ♡")
    }

    override fun onDisable() {
        if (::visualManager.isInitialized) {
            visualManager.removeAll()
        }
        logger.info("MyElytras desactivado.")
    }
}