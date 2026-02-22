package com.project_aurora

import com.project_aurora.nodes.GameState
import kotlinx.serialization.json.Json
import java.net.HttpURLConnection
import java.net.URL
import java.util.function.Consumer

var AuroraHost = "http://localhost:"
var AuroraPort = 9088

var Url: URL = URL("$AuroraHost$AuroraPort/gameState/minecraft")

val defaultJson = Json {
    encodeDefaults = true
}

class GameStateUpdater(
    private var gameStateProvider: GameStateProvider,
    private val logFunc: Consumer<String>,
): Runnable {

    val gameStateSerializer = GameState.serializer()

    override fun run() {
        try{
            // make POST request to Url with state as body
            val gameState = gameStateProvider.getGameState()
            val json = defaultJson.encodeToString(gameStateSerializer, gameState)

            postJson(Url, json)
        } catch (e: Error) {
            logFunc.accept("Failed to send game state update: ${e.message}\n${e.stackTraceToString()}")
            Thread.sleep(6000)
        }
    }

    fun postJson(url: URL, jsonBody: String) {
        val connection = (url.openConnection() as HttpURLConnection).apply {
            requestMethod = "POST"
            doOutput = true
            setRequestProperty("Content-Type", "application/json")
        }

        connection.outputStream.use { out ->
            out.write(jsonBody.toByteArray(Charsets.UTF_8))
        }

        // Trigger the request; ignore body
        connection.responseCode

        connection.disconnect()
    }

}