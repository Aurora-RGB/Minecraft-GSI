package com.project_aurora.nodes

import kotlinx.serialization.Serializable

@Serializable
data class KeyBindingNode(
    var keyCode: Int = -1,
    var modifier: String? = null,
    var context: String = "",
)
