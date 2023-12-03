package vg.skye.teawieks.scotty

import net.minecraft.network.chat.Component
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerPlayer
import vg.skye.teawieks.wherewasi.ILastLocationHolder
import java.util.Timer
import java.util.UUID
import kotlin.concurrent.timerTask

data class TeleportRequest(
    val from: UUID,
    val to: UUID,
    val tpahere: Boolean
)

private data class TimedTeleportRequest(
    val request: TeleportRequest,
    val timer: Timer
)

object ScottyManager {
    private val requests: MutableList<TimedTeleportRequest> = mutableListOf()

    fun list() : List<TeleportRequest> {
        return requests.map {
            it.request
        }
    }

    fun request(request: TeleportRequest, server: MinecraftServer) {
        if (requests.find {
                it.request.from == request.from && it.request.to == request.to
            } != null) {
            return
        }
        val timer = Timer()
        val wrapped = TimedTeleportRequest(request, timer)
        timer.schedule(
            timerTask {
                requests.remove(wrapped)
                val from = server
                    .playerList
                    .players
                    .find {
                        it.uuid == request.from
                    }
                val to = server
                    .playerList
                    .players
                    .find {
                        it.uuid == request.to
                    }
                from?.sendSystemMessage(Component.translatable(
                    "teawieks.command.tpa.expired",
                    to?.displayName ?: request.to.toString()
                ), false)
                to?.sendSystemMessage(Component.translatable(
                    "teawieks.command.tpa.request.expired",
                    from?.displayName ?: request.from.toString()
                ), false)
            },
            5 * 60 * 1000
        )
        requests.add(wrapped)
    }

    fun accept(from: UUID?, to: UUID, server: MinecraftServer): ServerPlayer? {
        val request = requests.find {
            if (from == null) {
                it.request.to == to
            } else {
                it.request.to == to && it.request.from == from
            }
        } ?: return null
        request.timer.cancel()
        requests.remove(request)
        val from = server.playerList.players.find {
             it.uuid == request.request.from
        } ?: return null
        val to = server.playerList.players.find {
            it.uuid == request.request.to
        } ?: return null
        if (request.request.tpahere) {
            (to as ILastLocationHolder).saveLastLocation()
            to.teleportTo(from.serverLevel(), from.x, from.y, from.z, from.yRot, from.xRot)
        } else {
            (from as ILastLocationHolder).saveLastLocation()
            from.teleportTo(to.serverLevel(), to.x, to.y, to.z, to.yRot, to.xRot)
        }
        return from
    }

    fun deny(from: UUID?, to: UUID): UUID? {
        val request = requests.find {
            if (from == null) {
                it.request.to == to
            } else {
                it.request.to == to && it.request.from == from
            }
        } ?: return null
        request.timer.cancel()
        requests.remove(request)
        return request.request.from
    }

    fun cancel(from: UUID, to: UUID?): UUID? {
        val request = requests.find {
            if (to == null) {
                it.request.from == from
            } else {
                it.request.to == to && it.request.from == from
            }
        } ?: return null
        request.timer.cancel()
        requests.remove(request)
        return to
    }
}