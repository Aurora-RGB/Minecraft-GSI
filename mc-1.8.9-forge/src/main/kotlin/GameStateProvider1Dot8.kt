package com.project_aurora

import com.project_aurora.nodes.GameState
import com.project_aurora.nodes.KeyBindingNode
import net.minecraft.client.Minecraft
import net.minecraft.client.entity.EntityPlayerSP
import net.minecraft.client.gui.GuiChat
import net.minecraft.client.gui.GuiControls
import net.minecraft.client.multiplayer.WorldClient
import net.minecraft.client.settings.KeyBinding
import net.minecraft.potion.Potion
import java.util.*
import java.util.stream.Collectors

class GameStateProvider1Dot8 : GameStateProvider {
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
        val minecraft = Minecraft.getMinecraft()

        gameNode.chatGuiOpen = minecraft.currentScreen is GuiChat
        gameNode.controlsGuiOpen = minecraft.currentScreen is GuiControls
        if (gameNode.controlsGuiOpen) {
            // Convert the array to a list of KeyBindingNode
            gameNode.keys = minecraft.gameSettings.keyBindings
                .map { kb: KeyBinding ->
                    val keyCode = kb.keyCode
                    KeyBindingNode(keyCode, "", "")
                }
                .toList()
        } else {
            gameNode.keys = emptyList()
        }
    }

    private fun updateWorldNode() {
        val world = Minecraft.getMinecraft().theWorld
        val worldNode = gameState.world
        if (world == null) {
            worldNode.reset()
            return
        }

        worldNode.worldTime = world.worldTime
        worldNode.isDayTime = world.isDaytime
        worldNode.rainStrength = world.rainingStrength
        worldNode.isRaining = world.isRaining
        worldNode.dimensionID = world.provider.dimensionId
    }

    private fun updatePlayerNode() {
        val playerNode = gameState.player

        // Attempt to get a player and store their health and stuff
        val player = Minecraft.getMinecraft().thePlayer
        if (player == null) {
            playerNode.inGame = false
            playerNode.playerEffects.clear()
            return
        }

        playerNode.health = player.health
        playerNode.healthMax = player.maxHealth
        playerNode.absorption = player.absorptionAmount
        playerNode.isDead = player.isDead
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
            TARGET_POTIONS["moveSpeed"] = Potion.moveSpeed
            TARGET_POTIONS["moveSlowdown"] = Potion.moveSlowdown
            TARGET_POTIONS["haste"] = Potion.digSpeed
            TARGET_POTIONS["miningFatigue"] = Potion.digSlowdown
            TARGET_POTIONS["strength"] = Potion.damageBoost
            TARGET_POTIONS["instantHealth"] = Potion.heal
            TARGET_POTIONS["instantDamage"] = Potion.harm
            TARGET_POTIONS["jumpBoost"] = Potion.jump
            TARGET_POTIONS["confusion"] = Potion.confusion
            TARGET_POTIONS["regeneration"] = Potion.regeneration
            TARGET_POTIONS["resistance"] = Potion.resistance
            TARGET_POTIONS["fireResistance"] = Potion.fireResistance
            TARGET_POTIONS["waterBreathing"] = Potion.waterBreathing
            TARGET_POTIONS["invisibility"] = Potion.invisibility
            TARGET_POTIONS["blindness"] = Potion.blindness
            TARGET_POTIONS["nightVision"] = Potion.nightVision
            TARGET_POTIONS["hunger"] = Potion.hunger
            TARGET_POTIONS["weakness"] = Potion.weakness
            TARGET_POTIONS["poison"] = Potion.poison
            TARGET_POTIONS["wither"] = Potion.wither
        }
    }
}