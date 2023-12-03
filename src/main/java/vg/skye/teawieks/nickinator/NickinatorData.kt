package vg.skye.teawieks.nickinator

import at.petrak.hexcasting.api.utils.asCompound
import at.petrak.hexcasting.api.utils.getList
import at.petrak.hexcasting.api.utils.putList
import com.google.gson.GsonBuilder
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.network.chat.Component
import net.minecraft.server.MinecraftServer
import net.minecraft.world.level.saveddata.SavedData
import java.util.UUID

class NickinatorData: SavedData() {
    companion object {
        private val gson = GsonBuilder()
            .registerTypeHierarchyAdapter(Component::class.java, Component.Serializer())
            .create()

        private var instance: NickinatorData? = null

        @JvmStatic
        fun getInstance(): NickinatorData? {
            return instance
        }

        fun attach(server: MinecraftServer) {
            instance = server.overworld().dataStorage.computeIfAbsent(
                {
                    val list = it.getList("nicknames", CompoundTag.TAG_COMPOUND)
                    val data = NickinatorData()
                    for (elem in list) {
                        val uuid = elem.asCompound.getUUID("uuid")
                        val nick = gson.fromJson(elem.asCompound.getString("nick"), Component::class.java)
                        data.nicknames[uuid] = nick
                    }
                    data
                },
                {
                    NickinatorData()
                },
                "teawieks_nicknames"
            )
        }
    }
    private val nicknames: MutableMap<UUID, Component> = HashMap()

    fun set(uuid: UUID, nickname: Component?) {
        if (nickname == null) {
            nicknames.remove(uuid)
        } else {
            nicknames[uuid] = nickname
        }
        setDirty()
    }

    fun get(uuid: UUID): Component? {
        return nicknames[uuid]
    }

    override fun save(compoundTag: CompoundTag): CompoundTag {
        val list = ListTag()
        for (pair in nicknames) {
            val tag = CompoundTag()
            tag.putUUID("uuid", pair.key)
            tag.putString("nick", gson.toJson(pair.value))
            list.add(tag)
        }
        compoundTag.putList("nicknames", list)
        return compoundTag
    }
}