package org.ReDiego0.myElytras.task

import org.ReDiego0.myElytras.config.ElytraConfigLoader
import org.ReDiego0.myElytras.manager.ElytraVisualManager
import org.bukkit.Bukkit
import org.bukkit.potion.PotionEffect
import org.bukkit.scheduler.BukkitRunnable

class ElytraTask(
    private val loader: ElytraConfigLoader,
    private val visualManager: ElytraVisualManager
) : BukkitRunnable() {

    override fun run() {
        for (player in Bukkit.getOnlinePlayers()) {
            visualManager.updateVisuals(player, player.isGliding)
            if (Bukkit.getCurrentTick() % 20 == 0) {
                applyPassiveEffects(player)
            }
        }
    }

    private fun applyPassiveEffects(player: org.bukkit.entity.Player) {
        val chestplate = player.inventory.chestplate ?: return
        val customElytra = loader.getElytraFromItem(chestplate) ?: return

        for ((type, amplifier, ambient) in customElytra.passiveEffects) {
            player.addPotionEffect(PotionEffect(type, 40, amplifier, ambient, false))
        }
    }
}