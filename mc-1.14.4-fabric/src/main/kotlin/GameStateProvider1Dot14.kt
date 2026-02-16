import com.project_aurora.GameStateProvider
import com.project_aurora.nodes.GameState
import com.project_aurora.nodes.KeyBindingNode
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.ChatScreen
import net.minecraft.client.gui.screen.options.ControlsOptionsScreen
import net.minecraft.client.options.KeyBinding
import net.minecraft.entity.effect.StatusEffect
import java.util.*
import java.util.stream.Collectors

class GameStateProvider1Dot14 : GameStateProvider {
    private val gameState = GameState()

    override fun getGameState(): GameState {
        updateGameState()

        return gameState
    }

    private fun updateGameState() {
        updateGameNode()
        updateWorldNode()
        updatePlayerNode()
    }

    private fun updateGameNode() {
        val gameNode = gameState.game
        val minecraft = getMinecraft()

        gameNode.chatGuiOpen = minecraft.currentScreen is ChatScreen
        gameNode.controlsGuiOpen = minecraft.currentScreen is ControlsOptionsScreen
        if (gameNode.controlsGuiOpen) {
            // Convert the array to a list of KeyBindingNode
            gameNode.keys = minecraft.options.keysAll
                .filter { key: KeyBinding -> !key.name.contains("unknown") && key.name.contains("keyboard") }
                .map { key: KeyBinding ->
                    val context = if (key.category === "key.categories.inventory") "GUI" else "UNIVERSAL"
                    val keyCode = AuroraKeycodeMapper.toAuroraKeyCode(key.name)
                    KeyBindingNode(keyCode, null, context)
                }
                .toList()
        } else {
            gameNode.keys = emptyList()
        }
    }

    private fun updateWorldNode() {
        val world = getMinecraft().world
        val worldNode = gameState.world
        if (world == null) {
            worldNode.reset()
            return
        }

        worldNode.worldTime = world.timeOfDay
        worldNode.isDayTime = world.isDay
        worldNode.rainStrength = world.getRainGradient(1.0f)
        worldNode.isRaining = world.isRaining
        worldNode.dimensionID = world.dimension.type.rawId
    }

    private fun updatePlayerNode() {
        val playerNode = gameState.player

        // Attempt to get a player and store their health and stuff
        val player = getMinecraft().player
        if (player == null) {
            playerNode.inGame = false
            playerNode.playerEffects.clear()
            return
        }

        playerNode.health = player.health
        playerNode.healthMax = player.maximumHealth
        playerNode.absorption = player.absorptionAmount
        playerNode.isDead = !player.isAlive
        playerNode.armor = player.armor
        playerNode.experienceLevel = player.experienceLevel
        playerNode.experience = player.experienceProgress
        playerNode.foodLevel = player.hungerManager.foodLevel
        playerNode.saturationLevel = player.hungerManager.saturationLevel
        playerNode.isSneaking = player.isSneaking
        playerNode.isRidingHorse = player.hasVehicle()
        playerNode.isBurning = player.isOnFire
        playerNode.isInWater = player.isTouchingWater

        // clear before attempting to get the player else there may be values on the mainmenu
        playerNode.playerEffects.clear()
        // Populate the player's effect map
        for (potion in TARGET_POTIONS.entries)
            playerNode.playerEffects[potion.key] = player.getStatusEffect(potion.value) != null

        playerNode.inGame = true
    }

    companion object {
        // Potion effects that will be added to the playerEffects map.
        private val TARGET_POTIONS = HashMap<String, StatusEffect>()

        init {
            TARGET_POTIONS["moveSpeed"] = StatusEffect.byRawId(1)!!
            TARGET_POTIONS["moveSlowdown"] = StatusEffect.byRawId(2)!!
            TARGET_POTIONS["haste"] = StatusEffect.byRawId(3)!!
            TARGET_POTIONS["miningFatigue"] = StatusEffect.byRawId(4)!!
            TARGET_POTIONS["strength"] = StatusEffect.byRawId(5)!!
            //TARGET_POTIONS.put("instantHealth", INSTANT_HEALTH)
            //TARGET_POTIONS.put("instantDamage", INSTANT_DAMAGE)
            TARGET_POTIONS["jumpBoost"] = StatusEffect.byRawId(8)!!
            TARGET_POTIONS["confusion"] = StatusEffect.byRawId(9)!!
            TARGET_POTIONS["regeneration"] = StatusEffect.byRawId(10)!!
            TARGET_POTIONS["resistance"] = StatusEffect.byRawId(11)!!
            TARGET_POTIONS["fireResistance"] = StatusEffect.byRawId(12)!!
            TARGET_POTIONS["waterBreathing"] = StatusEffect.byRawId(13)!!
            TARGET_POTIONS["invisibility"] = StatusEffect.byRawId(14)!!
            TARGET_POTIONS["blindness"] = StatusEffect.byRawId(15)!!
            TARGET_POTIONS["nightVision"] = StatusEffect.byRawId(16)!!
            TARGET_POTIONS["hunger"] = StatusEffect.byRawId(17)!!
            TARGET_POTIONS["weakness"] = StatusEffect.byRawId(18)!!
            TARGET_POTIONS["poison"] = StatusEffect.byRawId(19)!!
            TARGET_POTIONS["wither"] = StatusEffect.byRawId(20)!!
            //TARGET_POTIONS.put("healthBoost"] = StatusEffect.HEALTH_BOOST)
            TARGET_POTIONS["absorption"] = StatusEffect.byRawId(22)!!
            //TARGET_POTIONS.put("saturation"] = StatusEffect.SATURATION)
            TARGET_POTIONS["glowing"] = StatusEffect.byRawId(24)!!
            TARGET_POTIONS["levitation"] = StatusEffect.byRawId(25)!!
            TARGET_POTIONS["luck"] = StatusEffect.byRawId(26)!!
            TARGET_POTIONS["badLuck"] = StatusEffect.byRawId(27)!!
            TARGET_POTIONS["slowFalling"] = StatusEffect.byRawId(28)!!
            TARGET_POTIONS["conduitPower"] = StatusEffect.byRawId(29)!!
            TARGET_POTIONS["dolphinsGrace"] = StatusEffect.byRawId(30)!!
            TARGET_POTIONS["bad_omen"] = StatusEffect.byRawId(31)!!
            TARGET_POTIONS["villageHero"] = StatusEffect.byRawId(32)!!
        }
    }
}

private fun getMinecraft(): MinecraftClient = MinecraftClient.getInstance()