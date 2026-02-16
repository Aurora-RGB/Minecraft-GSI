package com.project_aurora.nodes

import kotlinx.serialization.Serializable

@Serializable
data class GameNode(
    var keys: List<KeyBindingNode> = emptyList(),
    var controlsGuiOpen: Boolean = false,
    var chatGuiOpen: Boolean = false,
)
