package vg.skye.teawieks.countryroad

import com.mojang.brigadier.CommandDispatcher
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.core.registries.Registries
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceKey
import vg.skye.teawieks.wherewasi.ILastLocationHolder

fun countryroadRegisterCommands(dispatcher: CommandDispatcher<CommandSourceStack>) {
    dispatcher.register(
        Commands
            .literal("sethome")
            .executes { ctx ->
                val user = ctx.source.playerOrException
                val dim = user.level().dimension().location()
                val pos = user.position()
                CountryroadData
                    .getInstance()!!
                    .set(user.uuid, PositionWithDimension(dim, pos))
                ctx.source.sendSuccess({ Component.translatable("teawieks.command.sethome") }, false)
                0
            }
    )
    dispatcher.register(
        Commands
            .literal("home")
            .executes { ctx ->
                val user = ctx.source.playerOrException
                val home =
                    CountryroadData
                        .getInstance()!!
                        .get(user.uuid)
                if (home == null) {
                    ctx.source.sendFailure(
                        Component.translatable("teawieks.command.home.failure")
                    )
                    return@executes 1
                }
                val dim = ctx.source.server.getLevel(ResourceKey.create(
                    Registries.DIMENSION,
                    home.dim
                ))!!
                (user as ILastLocationHolder).saveLastLocation()
                user.teleportTo(
                    dim,
                    home.pos.x, home.pos.y, home.pos.z,
                    user.yRot, user.xRot
                )
                ctx.source.sendSuccess({ Component.translatable("teawieks.command.home.success") }, false)
                0
            }
    )
}