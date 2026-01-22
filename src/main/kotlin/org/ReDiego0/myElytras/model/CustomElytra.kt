package org.ReDiego0.myElytras.model

import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.ItemFlag
import org.bukkit.potion.PotionEffectType
import java.util.UUID

data class CustomElytra(
    val id: String,
    val displayName: Component,
    val lore: List<Component>,
    val customModelData: Int,
    val armor: Double,
    val toughness: Double,
    val speedMalus: Double,
    val canGlide: Boolean,
    val fallSpeedMultiplier: Double,
    val maxDistance: Int,
    val canBoost: Boolean,
    val boostMultiplier: Double,
    val passiveEffects: List<Triple<PotionEffectType, Int, Boolean>>
) {
    fun buildItem(): ItemStack {
        val item = ItemStack(Material.ELYTRA)
        item.editMeta { meta ->
            meta.displayName(displayName)
            meta.lore(lore)
            meta.setCustomModelData(customModelData)

            // Atributos
            if (armor > 0) {
                val modifier = AttributeModifier(UUID.randomUUID(), "gen.armor", armor, AttributeModifier.Operation.ADD_NUMBER, org.bukkit.inventory.EquipmentSlot.CHEST)
                meta.addAttributeModifier(Attribute.GENERIC_ARMOR, modifier)
            }
            if (toughness > 0) {
                val modifier = AttributeModifier(UUID.randomUUID(), "gen.tough", toughness, AttributeModifier.Operation.ADD_NUMBER, org.bukkit.inventory.EquipmentSlot.CHEST)
                meta.addAttributeModifier(Attribute.GENERIC_ARMOR_TOUGHNESS, modifier)
            }
            if (speedMalus != 0.0) {
                val modifier = AttributeModifier(UUID.randomUUID(), "gen.speed", speedMalus, AttributeModifier.Operation.ADD_NUMBER, org.bukkit.inventory.EquipmentSlot.CHEST)
                meta.addAttributeModifier(Attribute.GENERIC_MOVEMENT_SPEED, modifier)
            }

            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
        }
        return item
    }
}