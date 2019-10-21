package com.tremblebeforeme.game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.GlyphLayout
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector3
import com.tremblebeforeme.game.HelpScreen1.Companion.ARROW_BOUND_HEIGHT
import com.tremblebeforeme.game.HelpScreen1.Companion.ARROW_BOUND_WIDTH
import com.tremblebeforeme.game.MainMenuScreen.Companion.DEVICE_HEIGHT
import com.tremblebeforeme.game.MainMenuScreen.Companion.DEVICE_WIDTH
import com.tremblebeforeme.game.MainMenuScreen.Companion.USER_PREF_FIRST_TIME
import com.tremblebeforeme.game.MainMenuScreen.Companion.prefs

class HelpScreen6(internal var game: TrembleBeforeMe, var scoreListener: IScores) : ScreenAdapter() {


    internal var guiCam: OrthographicCamera = OrthographicCamera()
    internal var nextBounds: Rectangle
    internal var touchPoint: Vector3
    internal var helpImage: Texture
    internal var helpRegion: TextureRegion
    private var firstLineLayout = GlyphLayout()
    private var secondLineLayout = GlyphLayout()
    private var thirdLineLayout = GlyphLayout()
    private val TEXT_FIRST_LINE = "STAND ON STONE PLATFORM"
    private val TEXT_SECOND_LINE = "AND YOUR SCORE WON'T GO DOWN"
    private val TEXT_THIRD_LINE = "ANTS AREN'T SO LUCKY!"

    init {
        guiCam.setToOrtho(false, DEVICE_WIDTH, DEVICE_HEIGHT)
        nextBounds = Rectangle(DEVICE_WIDTH - ARROW_BOUND_WIDTH, 0f, ARROW_BOUND_WIDTH, ARROW_BOUND_HEIGHT)
        touchPoint = Vector3()
        helpImage = Assets.loadTexture("help6.jpg")
        helpRegion = TextureRegion(helpImage, 0, 0, 1080, 1920)
    }

    fun update() {
        if (Gdx.input.justTouched()) {
            guiCam.unproject(touchPoint.set(Gdx.input.x.toFloat(), Gdx.input.y.toFloat(), 0f))

            if (nextBounds.contains(touchPoint.x, touchPoint.y)) {
                prefs.putBoolean(USER_PREF_FIRST_TIME,false)
                prefs.flush()
                game.screen = MainMenuScreen(game, scoreListener)
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
        game.batch.draw(helpRegion, 0f, 0f, DEVICE_WIDTH, DEVICE_HEIGHT)
        game.batch.end()

        game.batch.enableBlending()
        game.batch.begin()
        firstLineLayout.setText(Assets.font, TEXT_FIRST_LINE)
        secondLineLayout.setText(Assets.font, TEXT_SECOND_LINE)
        thirdLineLayout.setText(Assets.font, TEXT_THIRD_LINE)
        Assets.font.draw(game.batch, TEXT_FIRST_LINE, DEVICE_WIDTH / 2 - firstLineLayout.width / 2, DEVICE_HEIGHT - 40f)
        Assets.font.draw(game.batch, TEXT_SECOND_LINE, DEVICE_WIDTH / 2 - secondLineLayout.width / 2, DEVICE_HEIGHT - 55f - firstLineLayout.height)
        Assets.font.draw(game.batch, TEXT_THIRD_LINE, DEVICE_WIDTH / 2 - thirdLineLayout.width / 2, DEVICE_HEIGHT - 70f - firstLineLayout.height - secondLineLayout.height)
        game.batch.draw(Assets.arrow, DEVICE_WIDTH, 0f, - ARROW_BOUND_WIDTH, ARROW_BOUND_HEIGHT)
        game.batch.end()
    }

    override fun render(delta: Float) {
        draw()
        update()
    }

    override fun hide() {
        helpImage.dispose()
    }
}