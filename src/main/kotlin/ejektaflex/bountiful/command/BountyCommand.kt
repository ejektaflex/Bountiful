package ejektaflex.bountiful.command

import ejektaflex.bountiful.Bountiful
import ejektaflex.bountiful.config.BountifulIO
import ejektaflex.bountiful.api.ext.sendMessage
import ejektaflex.bountiful.config.ConfigFile
import ejektaflex.bountiful.item.ItemBounty
import ejektaflex.bountiful.logic.BountyCreator
import ejektaflex.bountiful.logic.error.BountyCreationException
import ejektaflex.bountiful.registry.BountyRegistry
import ejektaflex.bountiful.registry.RewardRegistry
import net.minecraft.client.resources.I18n
import net.minecraft.command.CommandException
import net.minecraft.command.ICommand
import net.minecraft.command.ICommandSender
import net.minecraft.entity.EntityLiving
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.server.MinecraftServer
import net.minecraft.util.math.BlockPos
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
                        sender.sendMessage(I18n.format("bountiful.reloading.data"))

                        val bountyBackup = BountyRegistry.backup()
                        try {
                            BountifulIO.hotReloadBounties("bounties.json").also {
                                if (it.isNotEmpty()) {
                                    sender.sendMessage("Invalid bounties: ${it.joinToString(", ")}. Skipped.")
                                }
                            }
                        } catch (e: Exception) {
                            sender.sendMessage("§4JSON Structure of 'bounties.json' is incorrect! Reverting to previous bounty data. Details:")
                            BountyRegistry.restore(bountyBackup)
                            sender.sendMessage(e.message ?: "No details.")
                        }

                        if (BountyRegistry.items.size < Bountiful.config.bountyAmountRange.last) {
                            sender.sendMessage("§4Your config file does not contain enough valid bounties. Reverting to previous bounty data.")
                            BountyRegistry.restore(bountyBackup)
                        }

                        val rewardBackup = RewardRegistry.backup()
                        try {
                            BountifulIO.hotReloadRewards("rewards.json").also {
                                if (it.isNotEmpty()) {
                                    sender.sendMessage("Invalid rewards: ${it.joinToString(", ")}. Skipped.")
                                }
                            }
                        } catch (e: Exception) {
                            sender.sendMessage("§4JSON Structure of 'rewards.json' is incorrect! Reverting to previous reward data. Details:")
                            RewardRegistry.restore(rewardBackup)
                            sender.sendMessage(e.message ?: "No details.")
                        }


                        sender.sendMessage(I18n.format("bountiful.reloaded.data"))
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
                    val names = ForgeRegistries.ENTITIES.entries.filter {
                        EntityLiving::class.java.isAssignableFrom(it.value.entityClass)
                    }.map { it.value.registryName.toString() }.sorted()
                    sender.sendMessage(names.joinToString(", "))
                    println(names)
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

