package com.tremblebeforeme.game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Preferences
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector3
import java.util.logging.Logger

class MainMenuScreen(var game: TrembleBeforeMe, var scoreListener: IScores): ScreenAdapter() {

    companion object {
        const val MAIN_MENU_WIDTH_MARGIN = 10.0f
        const val MAIN_MENU_HEIGHT = 100.0f
        const val DEVICE_WIDTH = 320.0f
        const val DEVICE_HEIGHT = 480.0f
        const val SOUND_X_BOUND = 32.0f
        const val SOUND_Y_BOUND = 32.0f
        const val USER_PREF_SOUND = "sound"
        const val USER_PREF_FIRST_TIME = "first_time"
        var prefs: Preferences = Gdx.app.getPreferences("User Preferences")

    }

    private var guiCam: OrthographicCamera = OrthographicCamera(DEVICE_WIDTH, DEVICE_HEIGHT)
    private var soundBounds: Rectangle
    private var playBounds: Rectangle
    private var scoreBounds: Rectangle
    private var helpBounds: Rectangle
    private var touchPoint: Vector3

    init {
        guiCam.position.set(DEVICE_WIDTH/2.0f, DEVICE_HEIGHT/2.0f, 0.0f)
        playBounds = Rectangle(MAIN_MENU_WIDTH_MARGIN, DEVICE_HEIGHT / 2 + MAIN_MENU_HEIGHT / 6, DEVICE_WIDTH -  2 * MAIN_MENU_WIDTH_MARGIN, MAIN_MENU_HEIGHT/3)
        scoreBounds = Rectangle(MAIN_MENU_WIDTH_MARGIN, DEVICE_HEIGHT / 2 - MAIN_MENU_HEIGHT / 6, DEVICE_WIDTH -  2 * MAIN_MENU_WIDTH_MARGIN, MAIN_MENU_HEIGHT/3)
        soundBounds = Rectangle(0.0f, 0.0f, SOUND_X_BOUND, SOUND_Y_BOUND)
        helpBounds = Rectangle(MAIN_MENU_WIDTH_MARGIN, DEVICE_HEIGHT / 2 - MAIN_MENU_HEIGHT / 2, DEVICE_WIDTH - 2 * MAIN_MENU_WIDTH_MARGIN, MAIN_MENU_HEIGHT/3)
        touchPoint = Vector3()
    }

    fun update() {
        if (Gdx.input.justTouched()) {
            guiCam.unproject(touchPoint.set(Gdx.input.x.toFloat(), Gdx.input.y.toFloat(), 0f))

            if (playBounds.contains(touchPoint.x, touchPoint.y)) {
                game.screen = GameScreen(game, scoreListener)
                return
            }
            if (scoreBounds.contains(touchPoint.x, touchPoint.y)) {
                game.screen = HighscoresScreen(game, scoreListener)
                return
            }
            if (helpBounds.contains(touchPoint.x, touchPoint.y)) {
                game.screen = HelpScreen1(game, scoreListener)
                return
            }
            if (soundBounds.contains(touchPoint.x, touchPoint.y)) {
                prefs.putBoolean(USER_PREF_SOUND,!prefs.getBoolean(USER_PREF_SOUND,true))
                prefs.flush()
                if (prefs.getBoolean(USER_PREF_SOUND, true))
                    Assets.music.play()
                else
                    Assets.music.pause()
            }

        }
    }

    fun draw() {
        val gl = Gdx.gl
        gl.glClearColor(1f, 0f, 0f, 1f)
        gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        guiCam.update()
        game.batch.projectionMatrix = guiCam.combined

        game.batch.disableBlending()
        game.batch.begin()
        game.batch.draw(Assets.backgroundRegion, 0.0f, 0.0f, DEVICE_WIDTH, DEVICE_HEIGHT)
        game.batch.end()

        game.batch.enableBlending()
        game.batch.begin()
        game.batch.draw(Assets.mainMenu, MAIN_MENU_WIDTH_MARGIN, DEVICE_HEIGHT/2 - MAIN_MENU_HEIGHT/2, DEVICE_WIDTH - 2 * MAIN_MENU_WIDTH_MARGIN, MAIN_MENU_HEIGHT)
        game.batch.draw(if (prefs.getBoolean(USER_PREF_SOUND, true)) Assets.soundOn else Assets.soundOff, 0.0f, 0.0f, SOUND_X_BOUND, SOUND_Y_BOUND)
        game.batch.end()
    }

    override fun render(delta: Float) {
        if (prefs.getBoolean(USER_PREF_FIRST_TIME, true)){
            game.screen = HelpScreen1(game, scoreListener)
        }
        update()
        draw()
    }

}