package org.ReDiego0.myElytras.manager

import org.ReDiego0.myElytras.config.ElytraConfigLoader
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Display
import org.bukkit.entity.ItemDisplay
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.util.Transformation
import org.joml.AxisAngle4f
import org.joml.Vector3f
import java.util.UUID

class ElytraVisualManager(private val loader: ElytraConfigLoader) {

    // Almacenar la entidad visual asociada al UUID del jugador
    private val activeDisplays = HashMap<UUID, ItemDisplay>()

    fun updateVisuals(player: Player, isGliding: Boolean) {
        val chestplate = player.inventory.chestplate
        val customElytra = if (chestplate != null) loader.getElytraFromItem(chestplate) else null
        if (customElytra == null) {
            removeVisual(player)
            return
        }

        val display = if (activeDisplays.containsKey(player.uniqueId)) {
            val d = activeDisplays[player.uniqueId]!!
            if (!d.isValid) {
                d.remove()
                createDisplay(player)
            } else {
                d
            }
        } else {
            createDisplay(player)
        }

        // Usamos el CustomModelData base para cerrado, y +1 para abierto.
        // Ej: Cerrado=1001, Abierto=1002.
        val baseModelData = customElytra.customModelData
        val targetModelData = if (isGliding) baseModelData + 1 else baseModelData

        val stack = ItemStack(Material.ELYTRA)
        val meta = stack.itemMeta
        // Solo actualizamos el ítem si cambió el estado para ahorrar procesamiento
        val currentStack = display.itemStack
        if (currentStack == null || currentStack.itemMeta?.customModelData != targetModelData) {
            meta.setCustomModelData(targetModelData)
            stack.itemMeta = meta
            display.setItemStack(stack)
        }

        // Calcular la posición detrás de la espalda del jugador
        val targetLocation = calculateBackLocation(player)

        // Teletrar y rotar para que coincida con el cuerpo del jugador
        display.teleport(targetLocation)
        display.setRotation(player.location.yaw, 0f)
    }

    private fun createDisplay(player: Player): ItemDisplay {
        val loc = calculateBackLocation(player)
        val display = player.world.spawn(loc, ItemDisplay::class.java) { d ->
            d.billboard = Display.Billboard.FIXED
            d.viewRange = 1.0f // Visible a distancia normal
            d.brightness = Display.Brightness(15, 15) // Full brillo (opcional)
            d.isPersistent = false // No se guardan al reiniciar el server

            // Transformación inicial (Escala y Ajustes finos)
            d.transformation = Transformation(
                Vector3f(0f, 0f, 0f),       // Traslación local
                AxisAngle4f(0f, 0f, 0f, 1f), // Rotación izquierda
                Vector3f(1f, 1f, 1f),       // Escala (1.0 = tamaño normal)
                AxisAngle4f(0f, 0f, 0f, 1f)  // Rotación derecha
            )
        }
        activeDisplays[player.uniqueId] = display
        return display
    }

    private fun calculateBackLocation(player: Player): Location {
        val loc = player.location.clone()
        val dir = loc.direction.setY(0).normalize().multiply(-0.25) // 0.25 bloques hacia atrás

        // Ajuste según si está agachado (Sneaking)
        val heightOffset = if (player.isSneaking) 0.6 else 0.85

        return loc.add(0.0, heightOffset, 0.0).add(dir)
    }

    fun removeVisual(player: Player) {
        activeDisplays[player.uniqueId]?.remove()
        activeDisplays.remove(player.uniqueId)
    }

    fun removeAll() {
        activeDisplays.values.forEach { if (it.isValid) it.remove() }
        activeDisplays.clear()
    }
}