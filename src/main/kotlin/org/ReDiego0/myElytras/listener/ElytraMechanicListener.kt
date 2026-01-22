package org.ReDiego0.myElytras.listener

import com.destroystokyo.paper.event.player.PlayerElytraBoostEvent
import org.ReDiego0.myElytras.config.ElytraConfigLoader
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityToggleGlideEvent

class ElytraMechanicListener(private val loader: ElytraConfigLoader) : Listener {

    @EventHandler
    fun onGlideToggle(event: EntityToggleGlideEvent) {
        if (event.entity !is Player) return
        // Solo importa si está intentando ABRIR las alas (isGliding == true)
        if (!event.isGliding) return

        val player = event.entity as Player
        val chestplate = player.inventory.chestplate ?: return
        val customElytra = loader.getElytraFromItem(chestplate) ?: return

        // Si la configuración dice que NO puede planear se cancela
        if (!customElytra.canGlide) {
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onElytraBoost(event: PlayerElytraBoostEvent) {
        val player = event.player
        val chestplate = player.inventory.chestplate ?: return

        val customElytra = loader.getElytraFromItem(chestplate) ?: return

        // Si no se permite el boost
        if (!customElytra.canBoost) {
            event.isCancelled = true
            // Al cancelar el evento, el cohete no se gasta ni impulsa.
        }
    }
}