package com.project_aurora.nodes

import kotlinx.serialization.Serializable


@Serializable
data class GameState(
    var provider: ProviderNode = ProviderNode(),
    var game: GameNode = GameNode(),
    var world: WorldNode = WorldNode(),
    var player: PlayerNode = PlayerNode(),
)
