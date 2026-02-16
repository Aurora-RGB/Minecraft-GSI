package com.project_aurora

import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.common.config.Configuration
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

@Mod(modid = AuroraGsiPlugin.MODID, useMetadata = true)
class AuroraGsiPlugin {
    private val gameStateUpdater: GameStateUpdater = GameStateUpdater(
        GameStateProvider1Dot8()
    ) { logString: String? -> logger?.info("[AuroraGSI] {}", logString) }
    private val scheduler: ScheduledExecutorService = Executors.newScheduledThreadPool(1)

    /**
     * Grab a reference to the logger and show a warning on servers that the mod isn't needed.
     */
    @Mod.EventHandler
    fun preInit(evt: FMLPreInitializationEvent) {
        logger = evt.modLog
        MinecraftForge.EVENT_BUS.register(this) // Required to listen for config event

        config = Configuration(evt.suggestedConfigurationFile)
        syncConfig()

        if (!FMLCommonHandler.instance().side
                .isClient
        ) logger?.warn("There is no need for Aurora GSI to be installed on the server.")
    }

    /**
     * Start a timer that will send a request to the Aurora HTTP listening server containing the game data.
     */
    @Mod.EventHandler
    @SideOnly(Side.CLIENT)
    fun init(evt: FMLInitializationEvent?) {
        logger?.info("Starting Aurora GSI with update rate of {}ms and port {}", UpdateRate, AuroraPort)
        scheduler.scheduleAtFixedRate(gameStateUpdater, 0, UpdateRate.toLong(), TimeUnit.MILLISECONDS)
    }

    companion object {
        const val MODID: String = "auroragsi"

        var config: Configuration? = null
        var AuroraPort: Int = 9088
        var UpdateRate: Int = 100

        private var logger: Logger? = null

        /**
         * Load the configuration file.
         */
        fun syncConfig() {
            if (config == null)
                return
            try {
                // Load config
                config!!.load()

                // Read props from config
                AuroraPort = config!!.get(
                    Configuration.CATEGORY_GENERAL,
                    "AuroraPort",
                    "9088",
                    "This should probably not need to be changed! The target port that the GSI updates should be POSTed to. Should match the port that Aurora is listening on."
                ).getInt(9088)
                UpdateRate = config!!.get(
                    Configuration.CATEGORY_GENERAL,
                    "UpdateRate",
                    "100",
                    "The rate at which the Aurora GSI mod sends data to Aurora application. The number of updates per second can be calculated by taking 1000 and dividing it by the number here. E.G. update rate of 100 means 10 updates per second."
                ).getInt(100)
            } finally {
                // Save props to config IF config changed
                if (config!!.hasChanged()) config!!.save()
            }
        }
    }
}