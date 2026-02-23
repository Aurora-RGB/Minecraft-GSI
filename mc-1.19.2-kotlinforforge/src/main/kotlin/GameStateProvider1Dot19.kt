package com.project_aurora

import com.project_aurora.nodes.GameState
import com.project_aurora.nodes.KeyBindingNode
import net.minecraft.client.KeyMapping
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.screens.ChatScreen
import net.minecraft.client.gui.screens.controls.KeyBindsScreen
import net.minecraft.world.effect.MobEffect
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.level.dimension.DimensionType

class GameStateProvider1Dot19 : GameStateProvider {
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

        gameNode.chatGuiOpen = minecraft.screen is ChatScreen
        gameNode.controlsGuiOpen = minecraft.screen is KeyBindsScreen
        if (gameNode.controlsGuiOpen) {
            // Convert the array to a list of KeyBindingNode
            gameNode.keys = minecraft.options.keyMappings
                .map { kb: KeyMapping ->
                    val keyCode = AuroraKeycodeMapper.toAuroraKeyCode(kb.key.name)
                    KeyBindingNode(keyCode, kb.keyModifier.name, "")
                }
                .toList()
        } else {
            gameNode.keys = emptyList()
        }
    }

    private fun updateWorldNode() {
        val world = Minecraft.getInstance().level
        val worldNode = gameState.world
        if (world == null) {
            worldNode.reset()
            return
        }

        worldNode.worldTime = world.dayTime
        worldNode.isDayTime = world.isDay
        worldNode.rainStrength = world.rainLevel
        worldNode.isRaining = world.isRaining
        val dimension = world.dimension()
        worldNode.dimensionID = when (dimension.location().path) {
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
        playerNode.armor = player.armorValue
        playerNode.experienceLevel = player.experienceLevel
        playerNode.experience = player.experienceProgress
        playerNode.foodLevel = player.foodData.foodLevel
        playerNode.saturationLevel = player.foodData.saturationLevel
        playerNode.isSneaking = player.isSteppingCarefully
        playerNode.isRidingHorse = player.isVehicle || player.isPassenger
        playerNode.isBurning = player.isOnFire
        playerNode.isInWater = player.isInWater

        // clear before attempting to get the player else there may be values on the mainmenu
        playerNode.playerEffects.clear()
        // Populate the player's effect map
        for (potion in TARGET_POTIONS.entries)
            playerNode.playerEffects[potion.key] = player.getEffect(potion.value) != null

        playerNode.inGame = true
    }

    companion object {
        // Potion effects that will be added to the playerEffects map.
        private val TARGET_POTIONS = HashMap<String, MobEffect>()

        init {
            TARGET_POTIONS["moveSpeed"] = MobEffects.MOVEMENT_SPEED
            TARGET_POTIONS["moveSlowdown"] = MobEffects.MOVEMENT_SLOWDOWN
            TARGET_POTIONS["haste"] = MobEffects.DIG_SPEED
            TARGET_POTIONS["miningFatigue"] = MobEffects.DIG_SLOWDOWN
            TARGET_POTIONS["strength"] = MobEffects.DAMAGE_BOOST
            //TARGET_POTIONS.put("instantHealth", INSTANT_HEALTH)
            //TARGET_POTIONS.put("instantDamage", INSTANT_DAMAGE)
            TARGET_POTIONS["jumpBoost"] = MobEffects.JUMP
            TARGET_POTIONS["confusion"] = MobEffects.CONFUSION
            TARGET_POTIONS["regeneration"] = MobEffects.REGENERATION
            TARGET_POTIONS["resistance"] = MobEffects.DAMAGE_RESISTANCE
            TARGET_POTIONS["fireResistance"] = MobEffects.FIRE_RESISTANCE
            TARGET_POTIONS["waterBreathing"] = MobEffects.WATER_BREATHING
            TARGET_POTIONS["invisibility"] = MobEffects.INVISIBILITY
            TARGET_POTIONS["blindness"] = MobEffects.BLINDNESS
            TARGET_POTIONS["nightVision"] = MobEffects.NIGHT_VISION
            TARGET_POTIONS["hunger"] = MobEffects.HUNGER
            TARGET_POTIONS["weakness"] = MobEffects.WEAKNESS
            TARGET_POTIONS["poison"] = MobEffects.POISON
            TARGET_POTIONS["wither"] = MobEffects.WITHER
            //TARGET_POTIONS.put("healthBoost"] = MobEffects.HEALTH_BOOST)
            TARGET_POTIONS["absorption"] = MobEffects.ABSORPTION
            //TARGET_POTIONS.put("saturation"] = MobEffects.SATURATION)
            TARGET_POTIONS["glowing"] = MobEffects.GLOWING
            TARGET_POTIONS["levitation"] = MobEffects.LEVITATION
            TARGET_POTIONS["luck"] = MobEffects.LUCK
            TARGET_POTIONS["badLuck"] = MobEffects.UNLUCK
            TARGET_POTIONS["slowFalling"] = MobEffects.SLOW_FALLING
            TARGET_POTIONS["conduitPower"] = MobEffects.CONDUIT_POWER
            TARGET_POTIONS["dolphinsGrace"] = MobEffects.DOLPHINS_GRACE
            TARGET_POTIONS["badOmen"] = MobEffects.BAD_OMEN
            TARGET_POTIONS["villageHero"] = MobEffects.HERO_OF_THE_VILLAGE

        }
    }
}