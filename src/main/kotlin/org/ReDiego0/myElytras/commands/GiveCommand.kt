package org.ReDiego0.myElytras.command

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.ReDiego0.myElytras.config.ElytraConfigLoader
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter

class GiveCommand(private val loader: ElytraConfigLoader) : CommandExecutor, TabCompleter {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (!sender.hasPermission("myelytras.admin")) {
            sender.sendMessage(Component.text("¡No tienes permisos! (qwq)", NamedTextColor.RED))
            return true
        }

        if (args.size < 3 || !args[0].equals("give", ignoreCase = true)) {
            sender.sendMessage(Component.text("Uso correcto: /myelytra give <jugador> <id>", NamedTextColor.RED))
            return true
        }

        val target = Bukkit.getPlayer(args[1])
        if (target == null) {
            sender.sendMessage(Component.text("Jugador '${args[1]}' no encontrado.", NamedTextColor.RED))
            return true
        }

        val elytraId = args[2]
        val elytra = loader.getElytra(elytraId)

        if (elytra == null) {
            sender.sendMessage(Component.text("La elytra '$elytraId' no existe en config.yml", NamedTextColor.RED))
            return true
        }

        target.inventory.addItem(elytra.buildItem())

        val successMsg = Component.text("¡Entregada ", NamedTextColor.GREEN)
            .append(elytra.displayName)
            .append(Component.text(" a ${target.name}!", NamedTextColor.GREEN))

        sender.sendMessage(successMsg)
        return true
    }

    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<out String>): MutableList<String>? {
        if (args.size == 1) {
            return mutableListOf("give")
        }
        if (args.size == 2) {
            return null
        }
        if (args.size == 3) {
            return loader.getElytraIds().toMutableList()
        }
        return mutableListOf()
    }
}