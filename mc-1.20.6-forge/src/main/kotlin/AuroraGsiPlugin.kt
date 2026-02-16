package com.project_aurora

import net.minecraftforge.fml.common.Mod
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit
import java.util.function.Consumer

@Mod(AuroraGsiPlugin.MODID)
class AuroraGsiPlugin {
    private val gameStateUpdater: GameStateUpdater = GameStateUpdater(
        GameStateProvider1Dot20(), logConsumer()
    )
    private val scheduler: ScheduledExecutorService = Executors.newScheduledThreadPool(1)

    init {
        scheduler.scheduleAtFixedRate(
            gameStateUpdater, 0, UpdateRate.toLong(), TimeUnit.MILLISECONDS
        )
    }

    companion object {
        const val MODID: String = "auroragsi"

        var AuroraPort: Int = 9088
        var UpdateRate: Int = 100
    }
}

private fun logConsumer(): Consumer<String> = { logString: String? ->
    println("[AuroraGSI] $logString")
}