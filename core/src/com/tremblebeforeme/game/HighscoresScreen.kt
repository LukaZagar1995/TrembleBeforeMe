package com.tremblebeforeme.game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.GlyphLayout
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector3
import com.tremblebeforeme.game.MainMenuScreen.Companion.DEVICE_HEIGHT
import com.tremblebeforeme.game.MainMenuScreen.Companion.DEVICE_WIDTH

class HighscoresScreen(var game: TrembleBeforeMe, var scoreListener: IScores) : ScreenAdapter() {

    companion object {
        const val SCORES_STATE_LAST_10 = 0
        const val SCORES_STATE_LAST_MONTH = 1
        const val SCORES_STATE_YEAR = 2
        const val SCORES_STATE_LAST_YEAR = 3
        const val BACK_HEIGHT = 35f
        const val BACK_WIDTH = 35f
        const val HIGHSCORES_TEXT = "HIGHSCORES"
        const val LAST_10_TEXT = "LAST 10 GAMES"
        const val LAST_MONTH_TEXT = "LAST MONTH"
        const val YEAR_TEXT = "THIS YEAR"
        const val LAST_YEAR_TEXT = "LAST YEAR"
        const val NO_DATA_TEXT = "NO DATA"
        const val SCORE_UP = "YOUR SCORE IS IMPROVING!"
        const val SCORE_DOWN_FIRST = "YOUR SCORE IS GETTING WORSE!"
        const val SCORE_DOWN_SECOND = "WATCH OUT!"
    }

    private var guiCam: OrthographicCamera = OrthographicCamera(DEVICE_WIDTH, DEVICE_HEIGHT)
    private var last10: Rectangle
    private var lastMonth: Rectangle
    private var year: Rectangle
    private var lastYear: Rectangle
    private var backBounds: Rectangle
    private var highscoresLayout = GlyphLayout()
    private var last10Layout = GlyphLayout()
    private var lastMonthLayout = GlyphLayout()
    private var thisYearLayout = GlyphLayout()
    private var lastYearLayout = GlyphLayout()
    private var touchPoint: Vector3
    private var state = SCORES_STATE_LAST_10
    private val last10Scores = scoreListener.getLast10Scores()
    private val lastMonthScores = scoreListener.getLastMonthScores()
    private val thisYearScores = scoreListener.getThisYearScores()
    private val lastYearScores = scoreListener.getLastYearScores()


    init {
        guiCam.position.set(DEVICE_WIDTH / 2.0f, DEVICE_HEIGHT / 2.0f, 0.0f)
        last10Layout.setText(Assets.font, LAST_10_TEXT)
        lastMonthLayout.setText(Assets.font, LAST_MONTH_TEXT)
        thisYearLayout.setText(Assets.font, YEAR_TEXT)
        lastYearLayout.setText(Assets.font, LAST_YEAR_TEXT)
        backBounds = Rectangle(5f, DEVICE_HEIGHT - 10f - BACK_HEIGHT, BACK_WIDTH, BACK_HEIGHT)
        last10 = Rectangle(20f, 30f + lastMonthLayout.height, last10Layout.width, last10Layout.height)
        lastMonth = Rectangle(20f, 10f, lastMonthLayout.width, lastMonthLayout.height)
        year = Rectangle(DEVICE_WIDTH - 20f - lastYearLayout.width, 30f + lastYearLayout.height, thisYearLayout.width, thisYearLayout.height)
        lastYear = Rectangle(DEVICE_WIDTH - 20f - lastYearLayout.width, 10f, lastYearLayout.width, lastYearLayout.height)
        touchPoint = Vector3()
    }

    fun update() {

        if (Gdx.input.justTouched()) {
            guiCam.unproject(touchPoint.set(Gdx.input.x.toFloat(), Gdx.input.y.toFloat(), 0f))
            if (backBounds.contains(touchPoint.x, touchPoint.y)) {
                game.screen = MainMenuScreen(game, scoreListener)
                return
            }

        }

        when (state) {
            SCORES_STATE_LAST_10 -> updateLast10()
            SCORES_STATE_LAST_MONTH -> updateLastMonth()
            SCORES_STATE_YEAR -> updateThisYear()
            SCORES_STATE_LAST_YEAR -> updateLastYear()
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
        game.batch.draw(Assets.arrow, 5f, DEVICE_HEIGHT - 5f - BACK_HEIGHT , BACK_WIDTH, BACK_HEIGHT)
        Assets.font.data.setScale(1.2f)
        highscoresLayout.setText(Assets.font, HIGHSCORES_TEXT)
        Assets.font.draw(game.batch, HIGHSCORES_TEXT, DEVICE_WIDTH / 2 - highscoresLayout.width / 2, DEVICE_HEIGHT - 10f)
        Assets.font.data.setScale(0.7f)
        when (state) {
            SCORES_STATE_LAST_10 -> presentLast10()
            SCORES_STATE_LAST_MONTH -> presentLastMonth()
            SCORES_STATE_YEAR -> presentYear()
            SCORES_STATE_LAST_YEAR -> presentLastYear()
        }
        game.batch.end()
    }

    override fun render(delta: Float) {
        update()
        draw()
    }

    private fun updateLast10() {
        if (Gdx.input.justTouched()) {
            guiCam.unproject(touchPoint.set(Gdx.input.x.toFloat(), Gdx.input.y.toFloat(), 0f))

            if (lastMonth.contains(touchPoint.x, touchPoint.y)) {
                state = SCORES_STATE_LAST_MONTH
            }
            if (year.contains(touchPoint.x, touchPoint.y)) {
                state = SCORES_STATE_YEAR
            }
            if (lastYear.contains(touchPoint.x, touchPoint.y)) {
                state = SCORES_STATE_LAST_YEAR
            }
        }
    }

    private fun updateLastMonth() {
        if (Gdx.input.justTouched()) {
            guiCam.unproject(touchPoint.set(Gdx.input.x.toFloat(), Gdx.input.y.toFloat(), 0f))

            if (last10.contains(touchPoint.x, touchPoint.y)) {
                state = SCORES_STATE_LAST_10
            }
            if (year.contains(touchPoint.x, touchPoint.y)) {
                state = SCORES_STATE_YEAR
            }
            if (lastYear.contains(touchPoint.x, touchPoint.y)) {
                state = SCORES_STATE_LAST_YEAR
            }
        }
    }

    private fun updateThisYear() {
        if (Gdx.input.justTouched()) {
            guiCam.unproject(touchPoint.set(Gdx.input.x.toFloat(), Gdx.input.y.toFloat(), 0f))

            if (last10.contains(touchPoint.x, touchPoint.y)) {
                state = SCORES_STATE_LAST_10
            }
            if (lastMonth.contains(touchPoint.x, touchPoint.y)) {
                state = SCORES_STATE_LAST_MONTH
            }
            if (lastYear.contains(touchPoint.x, touchPoint.y)) {
                state = SCORES_STATE_LAST_YEAR
            }
        }
    }

    private fun updateLastYear() {
        if (Gdx.input.justTouched()) {
            guiCam.unproject(touchPoint.set(Gdx.input.x.toFloat(), Gdx.input.y.toFloat(), 0f))

            if (last10.contains(touchPoint.x, touchPoint.y)) {
                state = SCORES_STATE_LAST_10
            }
            if (lastMonth.contains(touchPoint.x, touchPoint.y)) {
                state = SCORES_STATE_LAST_MONTH
            }
            if (year.contains(touchPoint.x, touchPoint.y)) {
                state = SCORES_STATE_YEAR
            }
        }
    }

    private fun presentLast10() {

        if (last10Scores.size > 0) {
            if (last10Scores.size < 10) {
                for (u in last10Scores.size - 1 downTo 0) {
                    Assets.font.draw(game.batch, last10Scores[u].date + ": " + last10Scores[u].score, DEVICE_WIDTH / 2 - 100, DEVICE_HEIGHT - 20f * (last10Scores.size - u) - highscoresLayout.height)
                }
            } else {
                for (u in last10Scores.size - 1 downTo last10Scores.size - 10) {
                    Assets.font.draw(game.batch, last10Scores[u].date + ": " + last10Scores[u].score, DEVICE_WIDTH / 2 - 100, DEVICE_HEIGHT - 20f * (last10Scores.size - u) - highscoresLayout.height)
                }
            }

        } else {
            Assets.font.data.setScale(1.5f)
            highscoresLayout.setText(Assets.font, NO_DATA_TEXT)
            Assets.font.draw(game.batch, NO_DATA_TEXT, DEVICE_WIDTH / 2 - highscoresLayout.width / 2, DEVICE_HEIGHT / 2 + highscoresLayout.height)
            Assets.font.data.setScale(0.7f)
        }
        calculateRiskOfDisease(last10Scores)
        last10Layout.setText(Assets.font, LAST_10_TEXT)
        lastMonthLayout.setText(Assets.font, LAST_MONTH_TEXT)
        thisYearLayout.setText(Assets.font, YEAR_TEXT)
        lastYearLayout.setText(Assets.font, LAST_YEAR_TEXT)
        Assets.font.setColor(1f, 1f, 1f, 0.6f)
        Assets.font.draw(game.batch, LAST_10_TEXT, 20f, 40f + lastMonthLayout.height)
        Assets.font.setColor(1f, 1f, 1f, 1f)
        Assets.font.draw(game.batch, LAST_MONTH_TEXT, 20f, 20f)
        Assets.font.draw(game.batch, YEAR_TEXT, DEVICE_WIDTH - 20f - lastYearLayout.width, 40f + lastYearLayout.height)
        Assets.font.draw(game.batch, LAST_YEAR_TEXT, DEVICE_WIDTH - 20f - lastYearLayout.width, 20f)
    }

    private fun presentLastMonth() {
        Assets.font.data.setScale(0.6f)
        if (lastMonthScores.size > 0) {
            if (lastMonthScores.size > 15) {
                for (u in lastMonthScores.size - 1 downTo  lastMonthScores.size - 16) {
                    Assets.font.draw(game.batch, lastMonthScores[u].date + ": " + lastMonthScores[u].score, 20f, DEVICE_HEIGHT - 20f * (lastMonthScores.size - u ) - highscoresLayout.height)
                }
                for (u in lastMonthScores.size - 17 downTo  0) {
                    Assets.font.draw(game.batch, lastMonthScores[u].date + ": " + lastMonthScores[u].score, DEVICE_WIDTH / 2 + 10f, DEVICE_HEIGHT - 20f * (lastMonthScores.size - u - 16) - highscoresLayout.height)
                }

            } else {
                for (u in 0 until lastMonthScores.size) {
                    Assets.font.draw(game.batch, lastMonthScores[u].date + ": " + lastMonthScores[u].score, DEVICE_WIDTH / 2 - 100, DEVICE_HEIGHT - 20f * (u +1) - highscoresLayout.height)
                }
            }
        } else {
            Assets.font.data.setScale(1.5f)
            highscoresLayout.setText(Assets.font, NO_DATA_TEXT)
            Assets.font.draw(game.batch, NO_DATA_TEXT, DEVICE_WIDTH / 2 - highscoresLayout.width / 2, DEVICE_HEIGHT / 2 + highscoresLayout.height)
        }
        calculateRiskOfDisease(lastMonthScores)
        Assets.font.data.setScale(0.7f)
        last10Layout.setText(Assets.font, LAST_10_TEXT)
        lastMonthLayout.setText(Assets.font, LAST_MONTH_TEXT)
        thisYearLayout.setText(Assets.font, YEAR_TEXT)
        lastYearLayout.setText(Assets.font, LAST_YEAR_TEXT)
        Assets.font.setColor(1f, 1f, 1f, 0.6f)
        Assets.font.draw(game.batch, LAST_MONTH_TEXT, 20f, 20f)
        Assets.font.setColor(1f, 1f, 1f, 1f)
        Assets.font.draw(game.batch, LAST_10_TEXT, 20f, 40f + lastMonthLayout.height)
        Assets.font.draw(game.batch, YEAR_TEXT, DEVICE_WIDTH - 20f - lastYearLayout.width, 40f + lastYearLayout.height)
        Assets.font.draw(game.batch, LAST_YEAR_TEXT, DEVICE_WIDTH - 20f - lastYearLayout.width, 20f)
    }

    private fun presentYear() {
        if (thisYearScores.size > 0) {
            for (u in 0 until thisYearScores.size) {
                Assets.font.draw(game.batch, thisYearScores[u].date + ": " + thisYearScores[u].score, DEVICE_WIDTH / 2 - 100, DEVICE_HEIGHT - 20f * (u + 1) - highscoresLayout.height)
            }
        } else {
            Assets.font.data.setScale(1.5f)
            highscoresLayout.setText(Assets.font, NO_DATA_TEXT)
            Assets.font.draw(game.batch, NO_DATA_TEXT, DEVICE_WIDTH / 2 - highscoresLayout.width / 2, DEVICE_HEIGHT / 2 + highscoresLayout.height )
            Assets.font.data.setScale(0.7f)
        }
        calculateRiskOfDiseaseMonth(thisYearScores)
        last10Layout.setText(Assets.font, LAST_10_TEXT)
        lastMonthLayout.setText(Assets.font, LAST_MONTH_TEXT)
        thisYearLayout.setText(Assets.font, YEAR_TEXT)
        lastYearLayout.setText(Assets.font, LAST_YEAR_TEXT)
        Assets.font.setColor(1f, 1f, 1f, 0.6f)
        Assets.font.draw(game.batch, YEAR_TEXT, DEVICE_WIDTH - 20f - lastYearLayout.width, 40f + lastYearLayout.height)
        Assets.font.setColor(1f, 1f, 1f, 1f)
        Assets.font.draw(game.batch, LAST_10_TEXT, 20f, 40f + lastMonthLayout.height)
        Assets.font.draw(game.batch, LAST_MONTH_TEXT, 20f, 20f)
        Assets.font.draw(game.batch, LAST_YEAR_TEXT, DEVICE_WIDTH - 20f - lastYearLayout.width, 20f)
    }

    private fun presentLastYear() {
        if (lastYearScores.size > 0) {
            for (u in 0 until lastYearScores.size) {
                Assets.font.draw(game.batch, lastYearScores[u].date + ": " + lastYearScores[u].score, DEVICE_WIDTH / 2 - 100, DEVICE_HEIGHT - 20f * (u + 1) - highscoresLayout.height)
            }
        } else {
            Assets.font.data.setScale(1.5f)
            highscoresLayout.setText(Assets.font, NO_DATA_TEXT)
            Assets.font.draw(game.batch, NO_DATA_TEXT, DEVICE_WIDTH / 2 - highscoresLayout.width / 2, DEVICE_HEIGHT / 2 + highscoresLayout.height)
            Assets.font.data.setScale(0.7f)
        }
        calculateRiskOfDiseaseMonth(lastYearScores)
        last10Layout.setText(Assets.font, LAST_10_TEXT)
        lastMonthLayout.setText(Assets.font, LAST_MONTH_TEXT)
        thisYearLayout.setText(Assets.font, YEAR_TEXT)
        lastYearLayout.setText(Assets.font, LAST_YEAR_TEXT)
        Assets.font.setColor(1f, 1f, 1f, 0.6f)
        Assets.font.draw(game.batch, LAST_YEAR_TEXT, DEVICE_WIDTH - 20f - lastYearLayout.width, 20f)
        Assets.font.setColor(1f, 1f, 1f, 1f)
        Assets.font.draw(game.batch, LAST_10_TEXT, 20f, 40f + lastMonthLayout.height)
        Assets.font.draw(game.batch, LAST_MONTH_TEXT, 20f, 20f)
        Assets.font.draw(game.batch, YEAR_TEXT, DEVICE_WIDTH - 20f - lastYearLayout.width, 40f + lastYearLayout.height)
    }

    private fun calculateRiskOfDisease(scores: ArrayList<Score>){
        var sum = 0
        var averageScore: Int
        if (scores.size > 5) {
            for (u in 0 until scores.size) {
                sum = (sum + scores[u].score).toInt()
                }
            averageScore = sum / scores.size

            if(scores[scores.size-1].score >= averageScore || scores[scores.size-2].score >= averageScore){
                Assets.font.data.setScale(0.6f)
                highscoresLayout.setText(Assets.font, SCORE_UP)
                Assets.font.draw(game.batch, SCORE_UP, DEVICE_WIDTH / 2 - highscoresLayout.width / 2, 100f)
                Assets.font.data.setScale(0.7f)
            } else {
                Assets.font.data.setScale(0.6f)
                highscoresLayout.setText(Assets.font, SCORE_DOWN_FIRST)
                Assets.font.draw(game.batch, SCORE_DOWN_FIRST, DEVICE_WIDTH / 2 - highscoresLayout.width / 2, 100f)
                highscoresLayout.setText(Assets.font, SCORE_DOWN_SECOND)
                Assets.font.draw(game.batch, SCORE_DOWN_SECOND, DEVICE_WIDTH / 2 - highscoresLayout.width / 2, 100f - highscoresLayout.height * 1.5f)
                Assets.font.data.setScale(0.7f)
            }
        }
    }

    private fun calculateRiskOfDiseaseMonth(scores: ArrayList<Score>){
        var sum = 0
        var averageScore: Int
        if (scores.size > 1) {
            for (u in 0 until scores.size) {
                sum = (sum + scores[u].score).toInt()
            }
            averageScore = sum / scores.size

            if(scores[scores.size-1].score >= averageScore){
                Assets.font.data.setScale(0.6f)
                highscoresLayout.setText(Assets.font, SCORE_UP)
                Assets.font.draw(game.batch, SCORE_UP, DEVICE_WIDTH / 2 - highscoresLayout.width / 2, 100f)
                Assets.font.data.setScale(0.7f)
            } else {
                Assets.font.data.setScale(0.6f)
                highscoresLayout.setText(Assets.font, SCORE_DOWN_FIRST)
                Assets.font.draw(game.batch, SCORE_DOWN_FIRST, DEVICE_WIDTH / 2 - highscoresLayout.width / 2, 100f)
                highscoresLayout.setText(Assets.font, SCORE_DOWN_SECOND)
                Assets.font.draw(game.batch, SCORE_DOWN_SECOND, DEVICE_WIDTH / 2 - highscoresLayout.width / 2, 100f - highscoresLayout.height * 1.5f)
                Assets.font.data.setScale(0.7f)
            }
        }
    }

}