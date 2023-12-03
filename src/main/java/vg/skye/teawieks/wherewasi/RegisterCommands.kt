package vg.skye.teawieks.wherewasi

import com.mojang.brigadier.CommandDispatcher
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.network.chat.Component

fun wherewasiRegisterCommands(dispatcher: CommandDispatcher<CommandSourceStack>) {
    dispatcher.register(
        Commands
            .literal("back")
            .executes { ctx ->
                val player = ctx.source.playerOrException
                val location = (player as ILastLocationHolder).getLastLocation()
                if (location != null) {
                    (player as ILastLocationHolder).saveLastLocation()
                    player.teleportTo(
                        location.dim,
                        location.pos.x, location.pos.y, location.pos.z,
                        location.look.y, location.look.x
                    )
                    ctx.source.sendSuccess({ Component.translatable("teawieks.command.back.success") }, false)
                    0
                } else {
                    ctx.source.sendFailure(Component.translatable("teawieks.command.back.failure"))
                    1
                }
            }
    )
}