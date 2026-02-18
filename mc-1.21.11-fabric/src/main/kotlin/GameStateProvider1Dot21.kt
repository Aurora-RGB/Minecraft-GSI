import com.project_aurora.GameStateProvider
import com.project_aurora.nodes.GameState
import com.project_aurora.nodes.KeyBindingNode
import net.minecraft.client.KeyMapping.Category
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.screens.ChatScreen
import net.minecraft.client.gui.screens.options.controls.KeyBindsScreen
import net.minecraft.core.Holder
import net.minecraft.world.effect.MobEffect
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.level.Level

class GameStateProvider1Dot21 : GameStateProvider {
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

        gameNode.chatGuiOpen = minecraft.screen is ChatScreen
        gameNode.controlsGuiOpen = minecraft.screen is KeyBindsScreen
        if (gameNode.controlsGuiOpen) {
            // Convert the array to a list of KeyBindingNode
            gameNode.keys = minecraft.options.keyMappings
                .filter { key -> key.category !== Category.DEBUG }
                .map { key ->
                    val context = if (key.category === Category.INVENTORY) "GUI" else "UNIVERSAL"
                    val keyCode = AuroraKeycodeMapper.toAuroraKeyCode(key.name)
                    KeyBindingNode(keyCode, null, context)
                }
                .toList()
        } else {
            gameNode.keys = emptyList()
        }
    }

    private fun updateWorldNode() {
        val world = getMinecraft().level
        val worldNode = gameState.world
        if (world == null) {
            worldNode.reset()
            return
        }

        worldNode.worldTime = world.dayTime
        worldNode.isDayTime = !world.isDarkOutside
        worldNode.rainStrength = world.getRainLevel(1.0f)
        worldNode.isRaining = world.isRaining
        worldNode.dimensionID = when (world.dimension()) {
            Level.NETHER -> -1
            Level.OVERWORLD -> 2
            Level.END -> 1
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
        playerNode.armor = player.armorValue
        playerNode.experienceLevel = player.experienceLevel
        playerNode.experience = player.experienceProgress
        playerNode.foodLevel = player.foodData.foodLevel
        playerNode.saturationLevel = player.foodData.saturationLevel
        playerNode.isSneaking = player.isSteppingCarefully
        playerNode.isRidingHorse = player.isVehicle || player.isPassenger
        playerNode.isBurning = player.isOnFire
        playerNode.isInWater = player.isInWater

        // clear before attempting to get the player, else there may be values on the mainmenu
        playerNode.playerEffects.clear()
        // Populate the player's effect map
        for (potion in TARGET_POTIONS.entries)
            playerNode.playerEffects[potion.key] = player.getEffect(potion.value) != null

        playerNode.inGame = true
    }

    companion object {
        // Potion effects that will be added to the playerEffects map.
        private val TARGET_POTIONS : Map<String, Holder<MobEffect>> = mapOf(
            "moveSpeed"        to MobEffects.SPEED,
            "moveSlowdown"     to MobEffects.SLOWNESS,
            "haste"            to MobEffects.HASTE,
            "miningFatigue"    to MobEffects.MINING_FATIGUE,
            "strength"         to MobEffects.STRENGTH,
            "jumpBoost"        to MobEffects.JUMP_BOOST,
            "confusion"        to MobEffects.NAUSEA,
            "regeneration"     to MobEffects.REGENERATION,
            "resistance"       to MobEffects.RESISTANCE,
            "fireResistance"   to MobEffects.FIRE_RESISTANCE,
            "waterBreathing"   to MobEffects.WATER_BREATHING,
            "invisibility"     to MobEffects.INVISIBILITY,
            "blindness"        to MobEffects.BLINDNESS,
            "nightVision"      to MobEffects.NIGHT_VISION,
            "hunger"           to MobEffects.HUNGER,
            "weakness"         to MobEffects.WEAKNESS,
            "poison"           to MobEffects.POISON,
            "wither"           to MobEffects.WITHER,
            "absorption"       to MobEffects.ABSORPTION,
            "glowing"          to MobEffects.GLOWING,
            "levitation"       to MobEffects.LEVITATION,
            "luck"             to MobEffects.LUCK,
            "badLuck"          to MobEffects.UNLUCK,
            "slowFalling"      to MobEffects.SLOW_FALLING,
            "conduitPower"     to MobEffects.CONDUIT_POWER,
            "dolphinsGrace"    to MobEffects.DOLPHINS_GRACE,
            "bad_omen"         to MobEffects.BAD_OMEN,
            "villageHero"      to MobEffects.HERO_OF_THE_VILLAGE
        )
    }
}

private fun getMinecraft(): Minecraft = Minecraft.getInstance()