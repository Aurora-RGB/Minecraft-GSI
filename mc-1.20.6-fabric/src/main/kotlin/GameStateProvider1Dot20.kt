import com.project_aurora.GameStateProvider
import com.project_aurora.nodes.GameState
import com.project_aurora.nodes.KeyBindingNode
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.ChatScreen
import net.minecraft.client.gui.screen.option.ControlsOptionsScreen
import net.minecraft.client.option.KeyBinding
import net.minecraft.entity.effect.StatusEffect
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.registry.entry.RegistryEntry

class GameStateProvider1Dot20 : GameStateProvider {
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
            gameNode.keys = minecraft.options.allKeys
                .map { key: KeyBinding ->
                    val context = if (key.category === "key.categories.inventory") "GUI" else "UNIVERSAL"
                    val keyCode = AuroraKeycodeMapper.toAuroraKeyCode(key.boundKeyTranslationKey)
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
        worldNode.dimensionID = when (world.registryKey.value.path) {
            "the_end" -> 1
            "the_nether" -> -1
            else -> 0
        }
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
        playerNode.healthMax = player.maxHealth
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

        // clear before attempting to get the player, else there may be values on the mainmenu
        playerNode.playerEffects.clear()
        // Populate the player's effect map
        for (potion in TARGET_POTIONS.entries)
            playerNode.playerEffects[potion.key] = player.getStatusEffect(potion.value) != null

        playerNode.inGame = true
    }

    companion object {
        // Potion effects that will be added to the playerEffects map.
        private val TARGET_POTIONS: Map<String, RegistryEntry<StatusEffect>> = mapOf(
            "moveSpeed"        to StatusEffects.SPEED,
            "moveSlowdown"     to StatusEffects.SLOWNESS,
            "haste"            to StatusEffects.HASTE,
            "miningFatigue"    to StatusEffects.MINING_FATIGUE,
            "strength"         to StatusEffects.STRENGTH,
            "jumpBoost"        to StatusEffects.JUMP_BOOST,
            "confusion"        to StatusEffects.NAUSEA,
            "regeneration"     to StatusEffects.REGENERATION,
            "resistance"       to StatusEffects.RESISTANCE,
            "fireResistance"   to StatusEffects.FIRE_RESISTANCE,
            "waterBreathing"   to StatusEffects.WATER_BREATHING,
            "invisibility"     to StatusEffects.INVISIBILITY,
            "blindness"        to StatusEffects.BLINDNESS,
            "nightVision"      to StatusEffects.NIGHT_VISION,
            "hunger"           to StatusEffects.HUNGER,
            "weakness"         to StatusEffects.WEAKNESS,
            "poison"           to StatusEffects.POISON,
            "wither"           to StatusEffects.WITHER,
            "absorption"       to StatusEffects.ABSORPTION,
            "glowing"          to StatusEffects.GLOWING,
            "levitation"       to StatusEffects.LEVITATION,
            "luck"             to StatusEffects.LUCK,
            "badLuck"          to StatusEffects.UNLUCK,
            "slowFalling"      to StatusEffects.SLOW_FALLING,
            "conduitPower"     to StatusEffects.CONDUIT_POWER,
            "dolphinsGrace"    to StatusEffects.DOLPHINS_GRACE,
            "bad_omen"         to StatusEffects.BAD_OMEN,
            "villageHero"      to StatusEffects.HERO_OF_THE_VILLAGE
        )
    }
}

private fun getMinecraft(): MinecraftClient = MinecraftClient.getInstance()