package org.ReDiego0.myElytras.task

import org.ReDiego0.myElytras.config.ElytraConfigLoader
import org.bukkit.Bukkit
import org.bukkit.potion.PotionEffect
import org.bukkit.scheduler.BukkitRunnable

class ElytraTask(private val loader: ElytraConfigLoader) : BukkitRunnable() {

    override fun run() {
        for (player in Bukkit.getOnlinePlayers()) {
            val chestplate = player.inventory.chestplate ?: continue

            val customElytra = loader.getElytraFromItem(chestplate) ?: continue

            for ((type, amplifier, ambient) in customElytra.passiveEffects) {
                player.addPotionEffect(PotionEffect(type, 40, amplifier, ambient, false))
            }
        }
    }
}