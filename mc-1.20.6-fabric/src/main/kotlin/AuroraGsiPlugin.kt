import com.project_aurora.GameStateUpdater
import net.fabricmc.api.ClientModInitializer
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

class AuroraGsiPlugin : ClientModInitializer {
    private val gameStateUpdater: GameStateUpdater = GameStateUpdater(
        GameStateProvider1Dot20(), { logString: String? ->
            println("[AuroraGSI] $logString")
        }
    )
    private val scheduler: ScheduledExecutorService = Executors.newScheduledThreadPool(1)

    override fun onInitializeClient() {
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
