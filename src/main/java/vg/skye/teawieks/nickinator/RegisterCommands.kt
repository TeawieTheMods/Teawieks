package vg.skye.teawieks.nickinator

import com.mojang.brigadier.CommandDispatcher
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.commands.arguments.ComponentArgument
import net.minecraft.commands.arguments.MessageArgument

fun nickinatorRegisterCommands(dispatcher: CommandDispatcher<CommandSourceStack>) {
    dispatcher.register(
        Commands
            .literal("nick")
            .then(
                Commands
                    .argument("nickname", MessageArgument.message())
                    .executes { ctx ->
                        val user = ctx.source.playerOrException.uuid
                        val nickname = MessageArgument.getMessage(ctx, "nickname")
                        NickinatorData.getInstance()!!.set(user, nickname)
                        0
                    }
            )
            .executes { ctx ->
                val user = ctx.source.playerOrException.uuid
                NickinatorData.getInstance()!!.set(user, null)
                0
            }
    )
    dispatcher.register(
        Commands
            .literal("nickraw")
            .then(
                Commands
                .argument("nickname", ComponentArgument.textComponent())
                .executes { ctx ->
                    val user = ctx.source.playerOrException.uuid
                    val nickname = ComponentArgument.getComponent(ctx, "nickname")
                    NickinatorData.getInstance()!!.set(user, nickname)
                    0
                }
            )
            .executes { ctx ->
                val user = ctx.source.playerOrException.uuid
                NickinatorData.getInstance()!!.set(user, null)
                0
            }
    )
}