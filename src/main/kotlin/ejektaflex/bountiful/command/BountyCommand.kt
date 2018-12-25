package ejektaflex.bountiful.command

import ejektaflex.bountiful.config.BountifulIO
import ejektaflex.bountiful.ext.sendMessage
import ejektaflex.bountiful.logic.error.BountyCreationException
import ejektaflex.bountiful.registry.BountyRegistry
import ejektaflex.bountiful.registry.RewardRegistry
import net.minecraft.command.CommandException
import net.minecraft.command.ICommand
import net.minecraft.command.ICommandSender
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.server.MinecraftServer
import net.minecraft.util.math.BlockPos


class BountyCommand : ICommand {

    override fun compareTo(other: ICommand): Int {
        return 0
    }

    override fun getName(): String {
        return "bountiful"
    }

    override fun getUsage(sender: ICommandSender): String {
        return "/bountiful"
    }

    override fun getAliases(): List<String> {
        val aliases = ArrayList<String>()
        aliases.add("bo")
        aliases.add("bounty")
        return aliases
    }

    @Throws(CommandException::class)
    override fun execute(server: MinecraftServer, sender: ICommandSender, args: Array<String>) {

        if (args.isNotEmpty()) {
            when (val curr: String = args[0]) {
                "reload" -> {
                    try {
                        BountifulIO.hotReloadJson(BountyRegistry, "bounties.json")
                        BountifulIO.hotReloadJson(RewardRegistry, "rewards.json")
                        sender.sendMessage("Json config files reloaded.")
                    } catch (bce: BountyCreationException) {
                        sender.sendMessage("§4" + bce.message!!)
                        sender.sendMessage("§4Defaulting to previous data. Correct it and try again.")
                    }
                }
            }
        } else {
            sender.sendMessage("Valid commands: '/bo reload'")
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

