package com.project_aurora

import com.project_aurora.nodes.GameState

interface GameStateProvider {
    fun getGameState(): GameState
}