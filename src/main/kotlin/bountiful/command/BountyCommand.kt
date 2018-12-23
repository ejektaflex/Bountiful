package bountiful.command

import bountiful.Bountiful
import bountiful.config.BountifulIO
import bountiful.gui.GuiHandler
import bountiful.registry.BountyRegistry
import bountiful.registry.RewardRegistry
import net.minecraft.command.CommandException
import net.minecraft.command.ICommand
import net.minecraft.command.ICommandSender
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.server.MinecraftServer
import net.minecraft.util.math.BlockPos
import net.minecraft.util.text.TextComponentString


class BountyCommand : ICommand {

    override fun compareTo(other: ICommand): Int {
        return 0
    }

    override fun getName(): String {
        return "bountiful"
    }

    override fun getUsage(sender: ICommandSender): String {
        return "/bo"
    }

    override fun getAliases(): List<String> {
        val aliases = ArrayList<String>()
        aliases.add("bo")
        return aliases
    }

    @Throws(CommandException::class)
    override fun execute(server: MinecraftServer, sender: ICommandSender, args: Array<String>) {

        if (args.isNotEmpty()) {
            when (val curr: String = args[0]) {
                "reload" -> {
                    BountifulIO.hotReloadJson(BountyRegistry, "bounties.json")
                    BountifulIO.hotReloadJson(RewardRegistry, "rewards.json")
                }
            }
        } else {
            println("Bounty command was empty.")
        }

    }

    override fun checkPermission(server: MinecraftServer, sender: ICommandSender): Boolean {
        return sender is EntityPlayer
    }

    override fun getTabCompletions(server: MinecraftServer, sender: ICommandSender, args: Array<String>, pos: BlockPos?): List<String>? {
        return null
    }

    override fun isUsernameIndex(args: Array<String>, index: Int): Boolean {
        return false
    }

}

