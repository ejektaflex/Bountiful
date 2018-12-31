package ejektaflex.bountiful.command

import ejektaflex.bountiful.config.BountifulIO
import ejektaflex.bountiful.api.ext.sendMessage
import ejektaflex.bountiful.item.ItemBounty
import ejektaflex.bountiful.logic.BountyCreator
import ejektaflex.bountiful.logic.error.BountyCreationException
import ejektaflex.bountiful.registry.BountyRegistry
import ejektaflex.bountiful.registry.RewardRegistry
import net.minecraft.command.CommandException
import net.minecraft.command.ICommand
import net.minecraft.command.ICommandSender
import net.minecraft.entity.EntityLiving
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.server.MinecraftServer
import net.minecraft.util.math.BlockPos
import net.minecraftforge.fml.common.FMLCommonHandler
import net.minecraftforge.fml.common.registry.ForgeRegistries
import net.minecraftforge.items.ItemHandlerHelper


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
                        sender.sendMessage("Reloading data..")
                        try {
                            BountifulIO.hotReloadBounties("bounties.json").also {
                                if (it.isNotEmpty()) {
                                    sender.sendMessage("Invalid bounties: ${it.joinToString(", ")}. Skipped.")
                                }
                            }
                        } catch (e: Exception) {
                            println("JSON Structure of 'bounties.json' is incorrect! Details:")
                            e.printStackTrace()
                        }

                        try {
                            BountifulIO.hotReloadRewards("rewards.json").also {
                                if (it.isNotEmpty()) {
                                    sender.sendMessage("Invalid rewards: ${it.joinToString(", ")}. Skipped.")
                                }
                            }
                        } catch (e: Exception) {
                            println("JSON Structure of 'rewards.json' is incorrect! Details:")
                            e.printStackTrace()
                        }


                        sender.sendMessage("Json config files reloaded.")
                    } catch (bce: BountyCreationException) {
                        sender.sendMessage("§4" + bce.message!!)
                        //sender.sendMessage("§4Defaulting to previous data. Correct it and try again.")
                    }
                }
                "gen" -> {
                    val playerProfile = sender.server?.playerList?.onlinePlayerProfiles?.find { sender.name == it.name }
                    if (playerProfile != null && sender.server?.playerList?.canSendCommands(playerProfile) == true) {
                        ItemHandlerHelper.giveItemToPlayer(sender.commandSenderEntity as EntityPlayer, BountyCreator.createStack(sender.entityWorld))
                    }
                }
                "expire" -> {
                    val player = sender.commandSenderEntity as EntityPlayer
                    val holding = player.heldItemMainhand
                    if (holding.item is ItemBounty) {
                        (holding.item as ItemBounty).tryExpireBountyTime(holding)
                    }
                }
                "wt" -> {
                    sender.sendMessage("Time: ${sender.entityWorld.totalWorldTime}")
                }
                "entities" -> {
                    val names = ForgeRegistries.ENTITIES.filter {
                        EntityLiving::class.java.isAssignableFrom(it.entityClass)
                    }.map { it.name.toLowerCase() }.sorted()
                    sender.sendMessage(names.joinToString(", "))
                }
            }
        } else {
            sender.sendMessage("Valid commands: '/bo reload', '/bo entities', '/bo gen' (op), '/bo expire' (op)")
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

