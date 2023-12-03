package vg.skye.teawieks.countryroad

import at.petrak.hexcasting.api.utils.asCompound
import at.petrak.hexcasting.api.utils.getList
import at.petrak.hexcasting.api.utils.putList
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.MinecraftServer
import net.minecraft.world.level.saveddata.SavedData
import net.minecraft.world.phys.Vec3
import java.util.*

data class PositionWithDimension(val dim: ResourceLocation, val pos: Vec3)

class CountryroadData: SavedData() {
    companion object {
        private var instance: CountryroadData? = null

        @JvmStatic
        fun getInstance(): CountryroadData? {
            return instance
        }

        fun attach(server: MinecraftServer) {
            instance = server.overworld().dataStorage.computeIfAbsent(
                {
                    val list = it.getList("homes", CompoundTag.TAG_COMPOUND)
                    val data = CountryroadData()
                    for (elem in list) {
                        val uuid = elem.asCompound.getUUID("uuid")
                        val pos = Vec3(
                            elem.asCompound.getDouble("x"),
                            elem.asCompound.getDouble("y"),
                            elem.asCompound.getDouble("z")
                        )
                        val dim = ResourceLocation(elem.asCompound.getString("dim"))
                        val home = PositionWithDimension(dim, pos)
                        data.homes[uuid] = home
                    }
                    data
                },
                {
                    CountryroadData()
                },
                "teawieks_homes"
            )
        }
    }
    private val homes: MutableMap<UUID, PositionWithDimension> = HashMap()

    fun set(uuid: UUID, home: PositionWithDimension?) {
        if (home == null) {
            homes.remove(uuid)
        } else {
            homes[uuid] = home
        }
        setDirty()
    }

    fun get(uuid: UUID): PositionWithDimension? {
        return homes[uuid]
    }

    override fun save(compoundTag: CompoundTag): CompoundTag {
        val list = ListTag()
        for (pair in homes) {
            val tag = CompoundTag()
            tag.putUUID("uuid", pair.key)
            tag.putString("dim", pair.value.dim.toString())
            tag.putDouble("x", pair.value.pos.x)
            tag.putDouble("y", pair.value.pos.y)
            tag.putDouble("z", pair.value.pos.z)
            list.add(tag)
        }
        compoundTag.putList("homes", list)
        return compoundTag
    }
}