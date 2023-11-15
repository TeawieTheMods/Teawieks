package vg.skye.teawieks

import at.petrak.hexcasting.api.casting.ActionRegistryEntry
import at.petrak.hexcasting.api.casting.math.HexDir
import at.petrak.hexcasting.api.casting.math.HexPattern
import at.petrak.hexcasting.common.items.storage.ItemSpellbook
import at.petrak.hexcasting.common.lib.HexItems
import at.petrak.hexcasting.common.lib.HexRegistries
import com.samsthenerd.wnboi.interfaces.KeyboundItem
import net.minecraft.client.Minecraft
import net.minecraft.network.chat.Component
import net.minecraft.server.packs.PackType
import net.minecraft.server.packs.repository.Pack
import net.minecraft.server.packs.repository.PackSource
import net.minecraft.world.item.DyeableLeatherItem
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.client.event.RegisterColorHandlersEvent
import net.minecraftforge.client.event.RegisterKeyMappingsEvent
import net.minecraftforge.event.AddPackFindersEvent
import net.minecraftforge.event.TickEvent
import net.minecraftforge.event.TickEvent.ClientTickEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.ModList
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.loading.FMLEnvironment
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries
import net.minecraftforge.resource.PathPackResources
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import thedarkcolour.kotlinforforge.forge.FORGE_BUS
import thedarkcolour.kotlinforforge.forge.MOD_BUS
import vg.skye.teawieks.scaler.ScalerItem
import vg.skye.teawieks.vazkiisbane.OpGetMana
import vg.skye.teawieks.vazkiisbane.OpGetMaxMana
import vg.skye.teawieks.wheex.OpSetLabel
import java.io.IOException
import java.util.function.Consumer


@Mod(Teawieks.ID)
object Teawieks {
    const val ID = "teawieks"
    @JvmField
    val LOGGER: Logger = LogManager.getLogger(ID)

    init {
        val register = DeferredRegister.create(ForgeRegistries.ITEMS, ID)
        register.register("scaler") { ScalerItem }
        register.register(MOD_BUS)

        val patternRegister = DeferredRegister.create(HexRegistries.ACTION, ID)
        patternRegister.apply {
            register("label") {
                ActionRegistryEntry(HexPattern.fromAngles("wwedwewdweqawqwqwqwqwqw", HexDir.SOUTH_WEST), OpSetLabel())
            }
            register("get_mana") {
                ActionRegistryEntry(HexPattern.fromAngles("qaawdd", HexDir.NORTH_EAST), OpGetMana())
            }
            register("get_max_mana") {
                ActionRegistryEntry(HexPattern.fromAngles("eddwaa", HexDir.NORTH_WEST), OpGetMaxMana())
            }
        }
        patternRegister.register(MOD_BUS)

        if (FMLEnvironment.dist == Dist.CLIENT) {
            MOD_BUS.register(this)
            FORGE_BUS.register(ForgeBusListener)
        }
    }

    @SubscribeEvent
    fun registerItemColors(event: RegisterColorHandlersEvent.Item){
        LOGGER.info("registering staves for dying")
        val staves = arrayOf(
            HexItems.STAFF_EDIFIED,
            HexItems.STAFF_OAK,
            HexItems.STAFF_SPRUCE,
            HexItems.STAFF_BIRCH,
            HexItems.STAFF_JUNGLE,
            HexItems.STAFF_ACACIA,
            HexItems.STAFF_DARK_OAK,
            HexItems.STAFF_CRIMSON,
            HexItems.STAFF_WARPED,
            HexItems.STAFF_MANGROVE,
            HexItems.STAFF_QUENCHED,
            HexItems.STAFF_MINDSPLICE,
            HexItems.STAFF_CHERRY
        )
        event.register({ stack, tintIndex ->
            if (tintIndex == 1) {
                return@register (stack.item as DyeableLeatherItem).getColor(stack)
            }
            0xFFFFFF
        }, *staves)
    }

    @SubscribeEvent
    fun registerKeyMappings(event: RegisterKeyMappingsEvent) {
        event.register(TeawieksKeybinds.IOTA_WHEEL_KEYBIND)
    }

    @SubscribeEvent
    fun addResourcePack(event: AddPackFindersEvent) {
        try {
            if (event.packType == PackType.CLIENT_RESOURCES) {
                val resourcePath =
                    ModList.get().getModFileById(ID).file.findResource("resourcepacks/dyeablestaffs")
                val resources = PathPackResources(
                    "$ID:dyeablestaffs",
                    true,
                    resourcePath
                )
                val pack = Pack.readMetaAndCreate(
                    "$ID:dyeablestaffs",
                    Component.literal("Dyeable Staves and Spellbooks"),
                    false,
                    { resources },
                    PackType.CLIENT_RESOURCES,
                    Pack.Position.TOP,
                    PackSource.BUILT_IN
                )
                event.addRepositorySource { packConsumer: Consumer<Pack?> ->
                    packConsumer.accept(pack)
                }

            }
        } catch (ex: IOException) {
            throw RuntimeException(ex)
        }
    }

    object ForgeBusListener {
        @SubscribeEvent
        fun registerOnTick(event: ClientTickEvent) {
            if (event.phase != TickEvent.Phase.END) return
            if (Minecraft.getInstance().player == null) return
            if (TeawieksKeybinds.IOTA_WHEEL_KEYBIND.isDown) {
                val mainHand = Minecraft.getInstance().player!!.mainHandItem.item
                val offHand = Minecraft.getInstance().player!!.offhandItem.item
                if (mainHand is ItemSpellbook && mainHand is KeyboundItem) {
                    LOGGER.info("opening mainhand")
                    mainHand.openScreen()
                } else if (offHand is ItemSpellbook && offHand is KeyboundItem) {
                    LOGGER.info("opening offhand")
                    offHand.openScreen()
                }
            }
        }
    }
}
