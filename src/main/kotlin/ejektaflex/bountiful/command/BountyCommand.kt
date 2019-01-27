package ejektaflex.bountiful.command

import ejektaflex.bountiful.Bountiful
import ejektaflex.bountiful.api.enum.EnumBountyRarity
import ejektaflex.bountiful.config.BountifulIO
import ejektaflex.bountiful.api.ext.sendMessage
import ejektaflex.bountiful.api.ext.sendTranslation
import ejektaflex.bountiful.item.ItemBounty
import ejektaflex.bountiful.logic.BountyCreator
import ejektaflex.bountiful.logic.error.BountyCreationException
import ejektaflex.bountiful.registry.BountyRegistry
import ejektaflex.bountiful.registry.RewardRegistry
import net.minecraft.command.CommandException
import net.minecraft.command.ICommand
import net.minecraft.command.ICommandSender
import net.minecraft.entity.EntityLiving
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
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

    private fun hasBasicPerms(sender: ICommandSender): Boolean {
        val playerProfile = sender.server?.playerList?.onlinePlayerProfiles?.find { sender.name == it.name }
        return playerProfile != null && sender.server?.playerList?.canSendCommands(playerProfile) == true
    }

    private fun safeGivePlayer(sender: ICommandSender, item: ItemStack) {
        if (hasBasicPerms(sender)) {
            ItemHandlerHelper.giveItemToPlayer(sender.commandSenderEntity as EntityPlayer, item)
        }
    }

    @Throws(CommandException::class)
    override fun execute(server: MinecraftServer, sender: ICommandSender, args: Array<String>) {

        if (args.isNotEmpty()) {
            when (val curr: String = args[0]) {

                // "/bo bounties" & "/bo rewards" are only meant to be used in dev right now
                "bounties" -> {
                    if (hasBasicPerms(sender)) {
                        sender.sendMessage("§6Bounties:")
                        for (bo in BountyRegistry.items) {
                            sender.sendMessage(bo.toString())
                        }
                    }

                }
                "rewards" -> {
                    if (hasBasicPerms(sender)) {
                        sender.sendMessage("§6Rewards:")
                        for (re in RewardRegistry.items) {
                            sender.sendMessage(re.toString())
                        }
                    }
                }

                "reload" -> {
                    try {
                        sender.sendTranslation("bountiful.reloading.data")

                        val bountyBackup = BountyRegistry.backup()

                        BountifulIO.safeHotReloadAll().also {
                            for (msg in it) {
                                sender.sendMessage(msg)
                            }
                        }

                        if (BountyRegistry.items.size < Bountiful.config.bountyAmountRange.last) {
                            sender.sendTranslation("bountiful.toofew.bounties")
                            BountyRegistry.restore(bountyBackup)
                        }

                        sender.sendTranslation("bountiful.reloaded.data")
                    } catch (bce: BountyCreationException) {
                        sender.sendMessage("§4" + bce.message!!)
                        //sender.sendMessage("§4Defaulting to previous data. Correct it and try again.")
                    }
                }
                "gen" -> {
                    //BountyCreator.createStack(sender.entityWorld)
                    when (args.drop(1).size) {
                        0 -> safeGivePlayer(sender, BountyCreator.createStack(sender.entityWorld))
                        else -> {
                            val rarity = EnumBountyRarity.values().find { it.name.toLowerCase().startsWith(args.drop(1).first()) }
                            if (rarity != null) {
                                safeGivePlayer(sender, BountyCreator.createStack(sender.entityWorld, rarity))
                            } else {
                                sender.sendTranslation("bountiful.command.gen.invalid")
                            }
                        }
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

