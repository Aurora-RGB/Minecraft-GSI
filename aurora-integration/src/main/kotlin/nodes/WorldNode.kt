package com.project_aurora.nodes

import kotlinx.serialization.Serializable

@Serializable
data class WorldNode(
    var worldTime: Long = 0L,
    var isDayTime: Boolean = true,
    var isRaining: Boolean = false,
    var rainStrength: Float = 0.0f,
    var dimensionID: Int = 0,
    var name: String = "",
) {
    fun reset() {
        worldTime = 0L
        isDayTime = true
        isRaining = false
        rainStrength = 0.0f
        dimensionID = 0
        name = ""
    }
}
