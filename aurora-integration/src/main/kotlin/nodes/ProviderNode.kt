package com.project_aurora.nodes

import kotlinx.serialization.Serializable

@Serializable
data class ProviderNode(
    var name: String = "minecraft",
    // appid with default PID
    var appid: Int = -1,
)
