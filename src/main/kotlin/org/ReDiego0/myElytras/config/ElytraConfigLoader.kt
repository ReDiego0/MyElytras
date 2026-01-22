package org.ReDiego0.myElytras.config

import net.kyori.adventure.text.minimessage.MiniMessage
import org.ReDiego0.myElytras.MyElytras
import org.ReDiego0.myElytras.model.CustomElytra
import org.bukkit.potion.PotionEffectType
import java.util.logging.Level

class ElytraConfigLoader(private val plugin: MyElytras) {

    private val elytraRegistry = HashMap<String, CustomElytra>()
    // Instancia de MiniMessage para deserializar textos
    private val mm = MiniMessage.miniMessage()

    fun load() {
        elytraRegistry.clear()
        val section = plugin.config.getConfigurationSection("elytras") ?: return

        for (key in section.getKeys(false)) {
            try {
                val path = "elytras.$key"

                val nameRaw = plugin.config.getString("$path.display.name") ?: "<red>Error Name"
                val nameComponent = mm.deserialize(nameRaw)

                val loreComponent = plugin.config.getStringList("$path.display.lore").map {
                    mm.deserialize(it)
                }

                val effectsList = mutableListOf<Triple<PotionEffectType, Int, Boolean>>()
                plugin.config.getStringList("$path.effects").forEach { effectString ->
                    val parts = effectString.split(";")
                    if (parts.size >= 2) {
                        val type = PotionEffectType.getByName(parts[0])
                        val amp = parts.getOrNull(2)?.toIntOrNull() ?: 0
                        val ambient = parts.getOrNull(3)?.toBoolean() ?: false
                        if (type != null) effectsList.add(Triple(type, amp, ambient))
                    }
                }

                val elytra = CustomElytra(
                    id = key,
                    displayName = nameComponent,
                    lore = loreComponent,
                    customModelData = plugin.config.getInt("$path.display.custom-model-data", 0),
                    armor = plugin.config.getDouble("$path.attributes.armor", 0.0),
                    toughness = plugin.config.getDouble("$path.attributes.toughness", 0.0),
                    speedMalus = plugin.config.getDouble("$path.attributes.movement-speed", 0.0),
                    canGlide = plugin.config.getBoolean("$path.mechanics.glide.can-glide", true),
                    fallSpeedMultiplier = plugin.config.getDouble("$path.mechanics.glide.fall-speed-multiplier", 1.0),
                    maxDistance = plugin.config.getInt("$path.mechanics.glide.max-distance-blocks", -1),
                    canBoost = plugin.config.getBoolean("$path.mechanics.flight.can-boost", true),
                    boostMultiplier = plugin.config.getDouble("$path.mechanics.flight.boost-speed-multiplier", 1.0),
                    passiveEffects = effectsList
                )

                elytraRegistry[key] = elytra
                plugin.logger.info("Cargada elytra: $key")

            } catch (e: Exception) {
                plugin.logger.log(Level.SEVERE, "Error en elytra '$key'", e)
            }
        }
    }

    fun getElytra(id: String): CustomElytra? = elytraRegistry[id]

    fun getElytraFromItem(item: org.bukkit.inventory.ItemStack): CustomElytra? {
        if (!item.hasItemMeta() || !item.itemMeta.hasCustomModelData()) return null
        val cmd = item.itemMeta.customModelData
        return elytraRegistry.values.find { it.customModelData == cmd }
    }
}