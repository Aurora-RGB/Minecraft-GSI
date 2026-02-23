package com.project_aurora

import net.minecraft.client.Minecraft
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import thedarkcolour.kotlinforforge.forge.MOD_BUS
import thedarkcolour.kotlinforforge.forge.runForDist
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit
import java.util.function.Consumer

@Mod(AuroraGsiPlugin.MODID)
class AuroraGsiPlugin {
    private val gameStateUpdater: GameStateUpdater = GameStateUpdater(
        GameStateProvider1Dot19(), logConsumer()
    )
    private val scheduler: ScheduledExecutorService = Executors.newScheduledThreadPool(1)

    init {
        runForDist(
            clientTarget = {
                MOD_BUS.addListener(::onClientSetup)
                Minecraft.getInstance()
            },
            serverTarget = {
                MOD_BUS.addListener(::onServerSetup)
                "test"
            })

        logConsumer().accept("[AuroraGSI] Initialized")
    }

    private fun onClientSetup(event: FMLClientSetupEvent) {
        LOGGER.log(Level.INFO, "Initializing client...")
        scheduler.scheduleAtFixedRate(
            gameStateUpdater, 0, UpdateRate.toLong(), TimeUnit.MILLISECONDS
        )
    }

    /**
     * Fired on the global Forge bus.
     */
    private fun onServerSetup(event: FMLDedicatedServerSetupEvent) {
    }

    private fun logConsumer(): Consumer<String> = { logString: String? ->
        LOGGER.info("[AuroraGSI] $logString")
    }

    companion object {
        const val MODID: String = "auroragsi"
        val LOGGER: Logger = LogManager.getLogger(MODID)

        var AuroraPort: Int = 9088
        var UpdateRate: Int = 100
    }
}