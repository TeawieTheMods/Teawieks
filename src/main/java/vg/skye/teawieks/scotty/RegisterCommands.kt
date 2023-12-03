package vg.skye.teawieks.scotty

import com.mojang.brigadier.CommandDispatcher
import net.minecraft.ChatFormatting
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.commands.arguments.EntityArgument
import net.minecraft.network.chat.ClickEvent
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.server.level.ServerPlayer

fun scottyRegisterCommands(dispatcher: CommandDispatcher<CommandSourceStack>) {
    dispatcher.register(
        Commands
            .literal("tpa")
            .then(
                Commands
                    .argument("target", EntityArgument.player())
                    .executes { ctx ->
                        val from = ctx.source.playerOrException
                        val to = EntityArgument.getPlayer(ctx, "target")
                        ScottyManager.request(
                            TeleportRequest(from.uuid, to.uuid, false),
                            ctx.source.server
                        )
                        ctx.source.sendSuccess({
                            Component.translatable("teawieks.command.tpa", to.displayName)
                        }, false)
                        to.sendSystemMessage(
                            Component.translatable(
                                "teawieks.command.tpa.request",
                                Component.translatable(
                                    "teawieks.command.tpa.request.header",
                                    from.displayName
                                ),
                                tpaButtons(from)
                            )
                        )
                        0
                    }
            )
    )
    dispatcher.register(
        Commands
            .literal("tpahere")
            .then(
                Commands
                    .argument("target", EntityArgument.player())
                    .executes { ctx ->
                        val from = ctx.source.playerOrException
                        val to = EntityArgument.getPlayer(ctx, "target")
                        ScottyManager.request(
                            TeleportRequest(from.uuid, to.uuid, true),
                            ctx.source.server
                        )
                        ctx.source.sendSuccess({
                            Component.translatable("teawieks.command.tpa", to.displayName)
                        }, false)
                        to.sendSystemMessage(
                            Component.translatable(
                                "teawieks.command.tpa.request",
                                Component.translatable(
                                    "teawieks.command.tpa.request.header.tpahere",
                                    from.displayName
                                ),
                                tpaButtons(from)
                            )
                        )
                        0
                    }
            )
    )
    dispatcher.register(
        Commands
            .literal("tpaaccept")
            .then(
                Commands
                    .argument("source", EntityArgument.player())
                    .executes { ctx ->
                        val from = EntityArgument.getPlayer(ctx, "source")
                        val to = ctx.source.playerOrException
                        val success = ScottyManager.accept(from.uuid, to.uuid, ctx.source.server) != null
                        if (success) {
                            ctx.source.sendSuccess({
                                Component.translatable(
                                    "teawieks.command.tpa.request.accepted",
                                    from.displayName
                                )
                            }, false)
                            from.sendSystemMessage(
                                Component.translatable(
                                    "teawieks.command.tpa.accepted",
                                    to.displayName
                                )
                            )
                            0
                        } else {
                            ctx.source.sendFailure(Component.translatable(
                                "teawieks.command.tpa.request.invalid",
                                from.displayName
                            ))
                            1
                        }
                    }
            )
            .executes { ctx ->
                val from = ScottyManager.accept(null, ctx.source.playerOrException.uuid, ctx.source.server)
                if (from != null) {
                    ctx.source.sendSuccess({
                        Component.translatable(
                            "teawieks.command.tpa.request.accepted",
                            from.displayName
                        )
                    }, false)
                    from.sendSystemMessage(Component.translatable(
                        "teawieks.command.tpa.accepted",
                        ctx.source.playerOrException.displayName
                    ))
                    0
                } else {
                    ctx.source.sendFailure(Component.translatable(
                        "teawieks.command.tpa.request.empty"
                    ))
                    1
                }
            }
    )
    dispatcher.register(
        Commands
            .literal("tpadeny")
            .then(
                Commands
                    .argument("source", EntityArgument.player())
                    .executes { ctx ->
                        val from = EntityArgument.getPlayer(ctx, "source")
                        val to = ctx.source.playerOrException
                        val success = ScottyManager.deny(from.uuid, to.uuid) != null
                        if (success) {
                            ctx.source.sendSuccess({
                                Component.translatable(
                                    "teawieks.command.tpa.request.denied",
                                    from.displayName
                                )
                            }, false)
                            from.sendSystemMessage(
                                Component.translatable(
                                    "teawieks.command.tpa.denied",
                                    to.displayName
                                )
                            )
                            0
                        } else {
                            ctx.source.sendFailure(Component.translatable(
                                "teawieks.command.tpa.request.invalid",
                                from.displayName
                            ))
                            1
                        }
                    }
            )
            .executes { ctx ->
                val from = ScottyManager.deny(null, ctx.source.playerOrException.uuid).run {
                    ctx.source.server.playerList.players.find {
                        it.uuid == this
                    }
                }
                if (from != null) {
                    ctx.source.sendSuccess({
                        Component.translatable(
                            "teawieks.command.tpa.request.denied",
                            from.displayName
                        )
                    }, false)
                    from.sendSystemMessage(
                        Component.translatable(
                            "teawieks.command.tpa.denied",
                            ctx.source.playerOrException.displayName
                        )
                    )
                    0
                } else {
                    ctx.source.sendFailure(Component.translatable(
                        "teawieks.command.tpa.request.empty"
                    ))
                    1
                }
            }
    )
    dispatcher.register(
        Commands
            .literal("tpacancel")
            .then(
                Commands
                    .argument("target", EntityArgument.player())
                    .executes { ctx ->
                        val to = EntityArgument.getPlayer(ctx, "target")
                        val from = ctx.source.playerOrException
                        if (ScottyManager.cancel(from.uuid, to.uuid) != null) {
                            ctx.source.sendSuccess({
                                Component.translatable(
                                    "teawieks.command.tpa.cancelled",
                                    to.displayName
                                )
                            }, false)
                            to.sendSystemMessage(
                                Component.translatable(
                                    "teawieks.command.tpa.request.cancelled",
                                    from.displayName
                                )
                            )
                            0
                        } else {
                            ctx.source.sendFailure(Component.translatable(
                                "teawieks.command.tpa.invalid",
                                to.displayName
                            ))
                            1
                        }
                    }
            )
            .executes { ctx ->
                val to = ScottyManager.cancel(ctx.source.playerOrException.uuid, null).run {
                    ctx.source.server.playerList.players.find {
                        it.uuid == this
                    }
                }
                if (to != null) {
                    ctx.source.sendSuccess({
                        Component.translatable(
                            "teawieks.command.tpa.cancelled",
                            to.displayName
                        )
                    }, false)
                    to.sendSystemMessage(
                        Component.translatable(
                            "teawieks.command.tpa.request.cancelled",
                            ctx.source.playerOrException.displayName
                        )
                    )
                    0
                } else {
                    ctx.source.sendFailure(Component.translatable(
                        "teawieks.command.tpa.empty"
                    ))
                    1
                }
            }
    )
}

private fun tpaButtons(from: ServerPlayer): MutableComponent {
    return Component.translatable(
        "teawieks.command.tpa.request.buttons",
        Component
            .translatable("teawieks.command.tpa.request.accept")
            .withStyle {
                it.withColor(ChatFormatting.GREEN)
                    .withClickEvent(
                        ClickEvent(
                            ClickEvent.Action.RUN_COMMAND,
                            "/tpaaccept ${from.name.string}"
                        )
                    )
            },
        Component
            .translatable("teawieks.command.tpa.request.deny")
            .withStyle {
                it.withColor(ChatFormatting.RED)
                    .withClickEvent(
                        ClickEvent(
                            ClickEvent.Action.RUN_COMMAND,
                            "/tpadeny ${from.name.string}"
                        )
                    )
            }
    )
}