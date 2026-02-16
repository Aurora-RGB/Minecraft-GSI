import com.project_aurora.GameStateProvider
import com.project_aurora.nodes.GameState
import com.project_aurora.nodes.KeyBindingNode
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.screen.ChatScreen
import net.minecraft.client.gui.screen.ControlsScreen
import net.minecraft.client.settings.KeyBinding
import net.minecraft.potion.Effect
import net.minecraft.potion.Effects

class GameStateProvider1Dot16 : GameStateProvider {
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
        val minecraft = Minecraft.getInstance()

        gameNode.chatGuiOpen = minecraft.currentScreen is ChatScreen
        gameNode.controlsGuiOpen = minecraft.currentScreen is ControlsScreen
        if (gameNode.controlsGuiOpen) {
            // Convert the array to a list of KeyBindingNode
            gameNode.keys = minecraft.gameSettings.keyBindings
                .map { kb: KeyBinding ->
                    val keyCode = AuroraKeycodeMapper.toAuroraKeyCode(kb.key.translationKey)
                    KeyBindingNode(keyCode, kb.keyModifier.name, "")
                }
                .toList()
        } else {
            gameNode.keys = emptyList()
        }
    }

    private fun updateWorldNode() {
        val world = Minecraft.getInstance().world
        val worldNode = gameState.world
        if (world == null) {
            worldNode.reset()
            return
        }

        worldNode.worldTime = world.dayTime
        worldNode.isDayTime = world.isDaytime
        worldNode.rainStrength = world.rainingStrength
        worldNode.isRaining = world.isRaining
        worldNode.dimensionID = when (world.dimensionKey.location.path) {
            "the_end" -> 1
            "the_nether" -> -1
            else -> 0
        }
    }

    private fun updatePlayerNode() {
        val playerNode = gameState.player

        // Attempt to get a player and store their health and stuff
        val player = Minecraft.getInstance().player
        if (player == null) {
            playerNode.inGame = false
            playerNode.playerEffects.clear()
            return
        }

        playerNode.health = player.health
        playerNode.healthMax = player.maxHealth
        playerNode.absorption = player.absorptionAmount
        playerNode.isDead = !player.isAlive
        playerNode.armor = player.totalArmorValue
        playerNode.experienceLevel = player.experienceLevel
        playerNode.experience = player.experience
        playerNode.foodLevel = player.getFoodStats().foodLevel
        playerNode.saturationLevel = player.getFoodStats().saturationLevel
        playerNode.isSneaking = player.isSneaking
        playerNode.isRidingHorse = player.isRidingHorse
        playerNode.isBurning = player.isBurning
        playerNode.isInWater = player.isInWater

        // clear before attempting to get the player, else there may be values on the mainmenu
        playerNode.playerEffects.clear()
        // Populate the player's effect map
        for (potion in TARGET_POTIONS.entries)
            playerNode.playerEffects[potion.key] = player.getActivePotionEffect(potion.value) != null

        playerNode.inGame = true
    }

    companion object {
        // Potion effects that will be added to the playerEffects map.
        private val TARGET_POTIONS = HashMap<String, Effect>()

        init {
            TARGET_POTIONS["moveSpeed"] = Effects.SPEED
            TARGET_POTIONS["moveSlowdown"] = Effects.SLOWNESS
            TARGET_POTIONS["haste"] = Effects.HASTE
            TARGET_POTIONS["miningFatigue"] = Effects.MINING_FATIGUE
            TARGET_POTIONS["strength"] = Effects.STRENGTH
            //TARGET_POTIONS.put("instantHealth", INSTANT_HEALTH)
            //TARGET_POTIONS.put("instantDamage", INSTANT_DAMAGE)
            TARGET_POTIONS["jumpBoost"] = Effects.JUMP_BOOST
            TARGET_POTIONS["confusion"] = Effects.NAUSEA
            TARGET_POTIONS["regeneration"] = Effects.REGENERATION
            TARGET_POTIONS["resistance"] = Effects.RESISTANCE
            TARGET_POTIONS["fireResistance"] = Effects.FIRE_RESISTANCE
            TARGET_POTIONS["waterBreathing"] = Effects.WATER_BREATHING
            TARGET_POTIONS["invisibility"] = Effects.INVISIBILITY
            TARGET_POTIONS["blindness"] = Effects.BLINDNESS
            TARGET_POTIONS["nightVision"] = Effects.NIGHT_VISION
            TARGET_POTIONS["hunger"] = Effects.HUNGER
            TARGET_POTIONS["weakness"] = Effects.WEAKNESS
            TARGET_POTIONS["poison"] = Effects.POISON
            TARGET_POTIONS["wither"] = Effects.WITHER
            //TARGET_POTIONS.put("healthBoost"] = Effects.HEALTH_BOOST)
            TARGET_POTIONS["absorption"] = Effects.ABSORPTION
            //TARGET_POTIONS.put("saturation"] = Effects.SATURATION)
            TARGET_POTIONS["glowing"] = Effects.GLOWING
            TARGET_POTIONS["levitation"] = Effects.LEVITATION
            TARGET_POTIONS["luck"] = Effects.LUCK
            TARGET_POTIONS["badLuck"] = Effects.UNLUCK
            TARGET_POTIONS["slowFalling"] = Effects.SLOW_FALLING
            TARGET_POTIONS["conduitPower"] = Effects.CONDUIT_POWER
            TARGET_POTIONS["dolphinsGrace"] = Effects.DOLPHINS_GRACE
        }
    }
}