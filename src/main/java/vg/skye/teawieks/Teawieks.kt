package vg.skye.teawieks

import at.petrak.hexcasting.common.lib.HexItems
import net.minecraft.network.chat.Component
import net.minecraft.server.packs.PackType
import net.minecraft.server.packs.repository.Pack
import net.minecraft.server.packs.repository.PackSource
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.DyeableLeatherItem
import net.minecraft.world.item.Item
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.client.event.RegisterColorHandlersEvent
import net.minecraftforge.event.AddPackFindersEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.ModList
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.loading.FMLEnvironment
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries
import net.minecraftforge.resource.PathPackResources
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import thedarkcolour.kotlinforforge.forge.MOD_BUS
import vg.skye.teawieks.scaler.ScalerItem
import vg.skye.teawieks.wie.HiPolywieBlock
import java.io.IOException
import java.util.function.Consumer


@Mod(Teawieks.ID)
object Teawieks {
    const val ID = "teawieks"
    @JvmField
    val LOGGER: Logger = LogManager.getLogger(ID)

    init {
        DeferredRegister.create(ForgeRegistries.BLOCKS, ID).apply {
            register("hipolywie") { HiPolywieBlock }
            register(MOD_BUS)
        }
        DeferredRegister.create(ForgeRegistries.ITEMS, ID).apply {
            register("scaler") { ScalerItem }
            register("hipolywie") { BlockItem(HiPolywieBlock, Item.Properties()) }
            register(MOD_BUS)
        }
        if (FMLEnvironment.dist == Dist.CLIENT) {
            MOD_BUS.register(this)
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
}
