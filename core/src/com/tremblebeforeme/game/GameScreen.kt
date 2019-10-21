package com.tremblebeforeme.game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.GlyphLayout
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector3
import com.tremblebeforeme.game.MainMenuScreen.Companion.DEVICE_HEIGHT
import com.tremblebeforeme.game.MainMenuScreen.Companion.DEVICE_WIDTH
import com.tremblebeforeme.game.Map.Companion.SPIKE_SIZE
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.MathUtils
import kotlin.math.abs


class GameScreen(var game: TrembleBeforeMe, var scoreListener: IScores) : ScreenAdapter() {

    companion object {
        const val GAME_READY = 0
        const val GAME_RUNNING = 1
        const val GAME_PAUSED = 2
        const val GAME_END = 3
        const val PAUSE_MENU_WIDTH = 200.0f
        const val PAUSE_BUTTON_BOUNDS = 42.0f
        const val PAUSE_MENU_HEIGHT = 96.0f
        const val FADE_IN_TIME = 1f
    }

    private var guiCam: OrthographicCamera = OrthographicCamera(DEVICE_WIDTH, DEVICE_HEIGHT)
    private var isSaved: Boolean = false
    private var mapListener: MapListener = object : MapListener {
        override fun lavaHit() {
            Assets.playSound(Assets.lavaHitSound)
        }

        override fun spike() {
            Assets.playSound(Assets.spikeSound)
        }

        override fun ant() {
            Assets.playSound(Assets.hitSound)
        }
    }
    private var map: Map
    private var pauseBounds: Rectangle
    private var resumeBounds: Rectangle
    private var quitBounds: Rectangle
    private var touchPoint: Vector3
    private var lastScore = 0
    private var renderer: MapRenderer
    private var glyphLayout = GlyphLayout()
    private var scoreString = "SCORE: $lastScore"
    private var font = Assets.font
    private var state = GAME_READY
    private var gameTime = 0
    private var startTime = 0
    private var pauseTime = 0
    private var pauseStartTime = 0
    private var fadeElapsedIn = 0f


    init {
        map = Map(mapListener)
        renderer = MapRenderer(game.batch, map)
        guiCam.position.set(DEVICE_WIDTH / 2.0f, DEVICE_HEIGHT / 2.0f, 0.0f)
        quitBounds = Rectangle(DEVICE_WIDTH / 2 - PAUSE_MENU_WIDTH / 2, DEVICE_HEIGHT / 2 - PAUSE_MENU_HEIGHT / 2, PAUSE_MENU_WIDTH, PAUSE_MENU_HEIGHT / 2)
        resumeBounds = Rectangle(DEVICE_WIDTH / 2 - PAUSE_MENU_WIDTH / 2, DEVICE_HEIGHT / 2, PAUSE_MENU_WIDTH, PAUSE_MENU_HEIGHT / 2)
        pauseBounds = Rectangle(DEVICE_WIDTH - SPIKE_SIZE, DEVICE_HEIGHT - SPIKE_SIZE, PAUSE_BUTTON_BOUNDS, PAUSE_BUTTON_BOUNDS)
        touchPoint = Vector3()
    }

    fun update(deltaTime: Float) {
        var deltaTime = deltaTime
        if (deltaTime > 0.1f) deltaTime = 0.1f

        when (state) {
            GAME_READY -> updateReady()
            GAME_RUNNING -> updateRunning(deltaTime)
            GAME_PAUSED -> updatePaused()
            GAME_END -> updateGameEnd()
        }
    }

    private fun updateRunning(deltaTime: Float) {
        getTime()
        if (Gdx.input.justTouched()) {
            guiCam.unproject(touchPoint.set(Gdx.input.x.toFloat(), Gdx.input.y.toFloat(), 0f))

            if (pauseBounds.contains(touchPoint.x, touchPoint.y)) {
                state = GAME_PAUSED
                pauseStartTime = System.currentTimeMillis().toInt() - pauseTime * 1000
                return
            }
        }

        if (Gdx.input.isPeripheralAvailable(Input.Peripheral.Accelerometer)) {
            map.update(deltaTime, Gdx.input.accelerometerX, Gdx.input.accelerometerY)
        }
        if (map.score !== lastScore) {
            lastScore = map.score
            scoreString = "SCORE: $lastScore"
        }
        if (gameTime >= 180) {
            state = GAME_END
        }
    }

    private fun updateReady() {
        if (Gdx.input.justTouched()) {
            state = GAME_RUNNING
            startTime = System.currentTimeMillis().toInt()
        }
    }

    private fun updatePaused() {
        stopTime()
        if (Gdx.input.justTouched()) {
            guiCam.unproject(touchPoint.set(Gdx.input.x.toFloat(), Gdx.input.y.toFloat(), 0f))

            if (resumeBounds.contains(touchPoint.x, touchPoint.y)) {
                state = GAME_RUNNING
                getTime()
                return
            }

            if (quitBounds.contains(touchPoint.x, touchPoint.y)) {
                game.screen = MainMenuScreen(game, scoreListener)
                return
            }
        }
    }

    private fun updateGameEnd() {
        if (Gdx.input.justTouched()) {
            scoreListener.saveScore(lastScore)
            game.screen = MainMenuScreen(game, scoreListener)
        }
    }

    fun draw(delta: Float) {
        val gl = Gdx.gl
        gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        renderer.render(gameTime,state)

        guiCam.update()
        game.batch.projectionMatrix = guiCam.combined
        game.batch.enableBlending()
        game.batch.begin()
        when (state) {
            GAME_READY -> presentReady()
            GAME_RUNNING -> presentRunning(delta)
            GAME_PAUSED -> presentPaused()
            GAME_END -> presentGameEnd()
        }
        game.batch.end()
    }

    private fun presentGameEnd() {
        if (!isSaved) {
            scoreListener.saveScore(lastScore)
            isSaved = true
        }
        Assets.font.data.setScale(1.5f)
        glyphLayout.setText(Assets.font, "GAME\nOVER")
        Assets.font.draw(game.batch, "GAME\nOVER", DEVICE_WIDTH / 2 - glyphLayout.width / 2, DEVICE_HEIGHT / 2 + glyphLayout.height)
        glyphLayout.setText(Assets.font, "$lastScore")
        Assets.font.draw(game.batch, "$lastScore", DEVICE_WIDTH / 2 - glyphLayout.width / 2, DEVICE_HEIGHT / 2 - glyphLayout.height)
        Assets.font.data.setScale(1f)
    }

    private fun presentReady() {
                font.data.setScale(1f)
        glyphLayout.setText(Assets.font, "READY?")
        Assets.font.draw(game.batch, "READY?", DEVICE_WIDTH / 2 - glyphLayout.width / 2, SPIKE_SIZE.toFloat())
    }

    private fun presentRunning(deltaTime: Float) {
        game.batch.draw(Assets.pause, DEVICE_WIDTH - PAUSE_BUTTON_BOUNDS, DEVICE_HEIGHT - PAUSE_BUTTON_BOUNDS + 5, PAUSE_BUTTON_BOUNDS, PAUSE_BUTTON_BOUNDS)
        Assets.font.draw(game.batch, scoreString, SPIKE_SIZE / 2f, DEVICE_HEIGHT - 20)
        val timeMinutes = 180 - gameTime
        val minutes = timeMinutes / 60
        val seconds = timeMinutes % 60
        if (seconds == 35 || seconds == 5) {
            if (minutes == 0 && seconds == 5) {

            } else {
                Assets.playSound(Assets.warningSound)
            }
        }

            if (seconds == 35 || seconds == 34 || seconds == 33 || seconds == 32 || seconds == 31
                    || minutes != 0 && seconds == 5 || minutes != 0 && seconds == 4 || minutes != 0 && seconds == 3
                    || minutes != 0 && seconds == 2 || minutes != 0 && seconds == 1) {

                fadeElapsedIn += abs(MathUtils.sin(deltaTime))
                var fade = Interpolation.fade.apply(fadeElapsedIn / FADE_IN_TIME)
                if (fade >= 1) {
                    fade = 1f
                    fadeElapsedIn = 0f
                }
                font.setColor(1f, 1f, 1f, fade)
                glyphLayout.setText(Assets.font, "WARNING")
                font.draw(game.batch, "WARNING", DEVICE_WIDTH / 2 - glyphLayout.width / 2, DEVICE_HEIGHT / 2 + glyphLayout.height / 2)
                font.setColor(1f, 1f, 1f, 1f)
        }
        if (seconds > 9)
            Assets.font.draw(game.batch, "$minutes:$seconds", SPIKE_SIZE / 2f, DEVICE_HEIGHT - 40)
        else
            Assets.font.draw(game.batch, "$minutes:0$seconds", SPIKE_SIZE / 2f, DEVICE_HEIGHT - 40)
    }

    private fun presentPaused() {
        game.batch.draw(Assets.pauseMenu, DEVICE_WIDTH / 2 - PAUSE_MENU_WIDTH / 2, DEVICE_HEIGHT / 2 - PAUSE_MENU_HEIGHT / 2, PAUSE_MENU_WIDTH, PAUSE_MENU_HEIGHT)
        Assets.font.draw(game.batch, scoreString, SPIKE_SIZE / 2f, DEVICE_HEIGHT - 20)
    }

    override fun render(delta: Float) {
        update(delta)
        draw(delta)
    }

    override fun pause() {
        stopTime()
        if (state == GAME_RUNNING) state = GAME_PAUSED
    }

    private fun getTime() {
        gameTime = (System.currentTimeMillis() - startTime).toInt() / 1000 - pauseTime

    }

    private fun stopTime() {
        pauseTime = (System.currentTimeMillis() - pauseStartTime).toInt() / 1000

    }

}