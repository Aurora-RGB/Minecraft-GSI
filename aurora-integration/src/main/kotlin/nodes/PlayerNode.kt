package com.project_aurora.nodes

import kotlinx.serialization.Serializable

@Serializable
data class PlayerNode(
    var inGame: Boolean? = false,
    var health: Float = 0f,
    var healthMax: Float = 0f,
    var absorption: Float = 0f,
    var absorptionMax: Float = 0f,
    var isDead: Boolean? = false,
    var armor: Int = 0,
    var armorMax: Int = 0,
    var experienceLevel: Int = 0,
    var experience: Float = 0f,
    var experienceMax: Float = 0f,
    var foodLevel: Int = 0,
    var foodLevelMax: Int = 0,
    var saturationLevel: Float = 0f,
    var saturationLevelMax: Float = 0f,
    var isSneaking: Boolean? = false,
    var isRidingHorse: Boolean? = false,
    var isBurning: Boolean? = false,
    var isInWater: Boolean? = false,
    var playerEffects: MutableMap<String, Boolean> = mutableMapOf(),
)
