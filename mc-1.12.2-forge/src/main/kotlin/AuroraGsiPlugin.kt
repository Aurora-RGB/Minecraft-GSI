package com.project_aurora

import com.project_aurora.AuroraGsiPlugin.Companion.MODID
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.common.config.Config
import net.minecraftforge.common.config.Config.RangeInt
import net.minecraftforge.common.config.Config.RequiresMcRestart
import net.minecraftforge.common.config.ConfigManager
import net.minecraftforge.fml.common.FMLCommonHandler
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import org.apache.logging.log4j.Logger
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

@Mod(modid = MODID, useMetadata = true, clientSideOnly = true)
class AuroraGsiPlugin {
    private val gameStateUpdater: GameStateUpdater = GameStateUpdater(
        GameStateProvider1Dot12()
    ) { logString: String? -> logger?.info("[AuroraGSI] {}", logString) }
    private val scheduler: ScheduledExecutorService = Executors.newScheduledThreadPool(1)

    /**
     * Grab a reference to the logger and show a warning on servers that the mod isn't needed.
     */
    @Mod.EventHandler
    fun preInit(evt: FMLPreInitializationEvent) {
        logger = evt.modLog
        MinecraftForge.EVENT_BUS.register(this) // Required to listen for config event

        if (!FMLCommonHandler.instance().side.isClient)
            logger?.warn("There is no need for Aurora GSI to be installed on the server.")
    }

    /**
     * Start a timer that will send a request to the Aurora HTTP listening server containing the game data.
     */
    @Mod.EventHandler
    @SideOnly(Side.CLIENT)
    fun init(evt: FMLInitializationEvent) {
        ConfigManager.sync(MODID, Config.Type.INSTANCE)
        logger?.info("Starting Aurora GSI with update rate of {}ms and port {}", UpdateRate, AuroraPort)
        scheduler.scheduleAtFixedRate(gameStateUpdater, 0, AuroraGSIConfig.UpdateRate, TimeUnit.MILLISECONDS)
    }

    companion object {
        const val MODID: String = "auroragsi"

        var AuroraPort: Int = 9088
        var UpdateRate: Int = 100

        private var logger: Logger? = null

    }
}

/** Settings store class.  */
@Config(modid = MODID, type = Config.Type.INSTANCE)
object AuroraGSIConfig {
    @Config.Name("Aurora Port")
    @Config.Comment(
        "This should probably not need to be changed!",
        "The target port that the GSI updates should be POSTed to. Should match the port that Aurora is listening on."
    )
    @RangeInt(min = 1, max = 65535)
    @RequiresMcRestart
    var AuroraPort: Int = 9088

    @Config.Name("Update Rate (ms)")
    @Config.Comment(
        "The rate at which the Aurora GSI mod sends data to Aurora application.",
        "The number of updates per second can be calculated by taking 1000 and dividing it by the number here. E.G. update rate of 100 means 10 updates per second."
    )
    @RangeInt(min = 50, max = 1500)
    @RequiresMcRestart
    var UpdateRate: Long = 100
}