package com.project_aurora

import com.project_aurora.AuroraKeycodeMapper.toAuroraKeyCode
import com.project_aurora.nodes.GameState
import com.project_aurora.nodes.KeyBindingNode
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiChat
import net.minecraft.client.gui.GuiControls
import net.minecraft.client.settings.KeyBinding
import net.minecraft.init.MobEffects
import net.minecraft.potion.Potion

class GameStateProvider1Dot13 : GameStateProvider {
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

        gameNode.chatGuiOpen = minecraft.currentScreen is GuiChat
        gameNode.controlsGuiOpen = minecraft.currentScreen is GuiControls
        if (gameNode.controlsGuiOpen) {
            // Convert the array to a list of KeyBindingNode
            gameNode.keys = minecraft.gameSettings.keyBindings
                .map { kb: KeyBinding ->
                    val keyCode = toAuroraKeyCode(kb.key.translationKey)
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
        worldNode.dimensionID = world.dimension.type.id
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

        // clear before attempting to get the player, else there may be values on the main menu
        playerNode.playerEffects.clear()
        // Populate the player's effect map
        for (potion in TARGET_POTIONS.entries)
            playerNode.playerEffects[potion.key] = player.getActivePotionEffect(potion.value) != null

        playerNode.inGame = true
    }

    companion object {
        // Potion effects that will be added to the playerEffects map.
        private val TARGET_POTIONS = HashMap<String, Potion>()

        init {
            TARGET_POTIONS["moveSpeed"] = MobEffects.SPEED
            TARGET_POTIONS["moveSlowdown"] = MobEffects.SLOWNESS
            TARGET_POTIONS["haste"] = MobEffects.HASTE
            TARGET_POTIONS["miningFatigue"] = MobEffects.MINING_FATIGUE
            TARGET_POTIONS["strength"] = MobEffects.STRENGTH
            //TARGET_POTIONS.put("instantHealth", INSTANT_HEALTH)
            //TARGET_POTIONS.put("instantDamage", INSTANT_DAMAGE)
            TARGET_POTIONS["jumpBoost"] = MobEffects.JUMP_BOOST
            TARGET_POTIONS["confusion"] = MobEffects.NAUSEA
            TARGET_POTIONS["regeneration"] = MobEffects.REGENERATION
            TARGET_POTIONS["resistance"] = MobEffects.RESISTANCE
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
        }
    }
}