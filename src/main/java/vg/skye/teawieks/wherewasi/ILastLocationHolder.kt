package vg.skye.teawieks.wherewasi

import net.minecraft.server.level.ServerLevel
import net.minecraft.world.phys.Vec2
import net.minecraft.world.phys.Vec3

data class FullLocationData(val dim: ServerLevel, val pos: Vec3, val look: Vec2)

interface ILastLocationHolder {
    fun getLastLocation(): FullLocationData?
    fun saveLastLocation()
}