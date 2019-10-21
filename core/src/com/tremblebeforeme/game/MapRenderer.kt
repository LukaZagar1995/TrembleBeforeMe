package com.tremblebeforeme.game

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.tremblebeforeme.game.Animation.Companion.ANIMATION_LOOPING
import com.tremblebeforeme.game.Ant.Companion.ANT_HEIGHT
import com.tremblebeforeme.game.Ant.Companion.ANT_STATE_HIT
import com.tremblebeforeme.game.Ant.Companion.ANT_STATE_NOT_HIT
import com.tremblebeforeme.game.Ant.Companion.ANT_STATE_NOT_SPAWN
import com.tremblebeforeme.game.Ant.Companion.ANT_TYPE_STATIC
import com.tremblebeforeme.game.Ant.Companion.ANT_WIDTH
import com.tremblebeforeme.game.Avatar.Companion.AVATAR_HEIGHT
import com.tremblebeforeme.game.Avatar.Companion.AVATAR_WIDTH
import com.tremblebeforeme.game.GameScreen.Companion.GAME_RUNNING
import com.tremblebeforeme.game.MainMenuScreen.Companion.DEVICE_HEIGHT
import com.tremblebeforeme.game.MainMenuScreen.Companion.DEVICE_WIDTH
import com.tremblebeforeme.game.Map.Companion.SPIKE_SIZE
import com.tremblebeforeme.game.Platform.Companion.PLATFORM_HEIGHT
import com.tremblebeforeme.game.Platform.Companion.PLATFORM_WIDTH


class MapRenderer(var batch: SpriteBatch, var map: Map) {

    private var guiCam: OrthographicCamera = OrthographicCamera(DEVICE_WIDTH,DEVICE_HEIGHT)
    private var lavaSound: Boolean = false

    init {
        guiCam.position.set(DEVICE_WIDTH/2.0f, DEVICE_HEIGHT/2.0f, 0.0f)
    }

    fun render(time: Int, gameState: Int) {

        guiCam.update()
        batch.projectionMatrix = guiCam.combined
        renderBackground(time, gameState)
        renderObjects(time)
    }

    private fun renderObjects(time: Int) {
        batch.enableBlending()
        batch.begin()
        renderAvatar()
        renderAnt(time)
        batch.end()
    }

    private fun renderAvatar() {
        val keyFrame: TextureRegion = Assets.avatarWalk.getKeyFrame(map.avatar.stateTime, ANIMATION_LOOPING)
        val side = (if (map.avatar.speed.x < 2) -1 else 1).toFloat()
        if (side < 0)
            batch.draw(keyFrame, map.avatar.position.x + AVATAR_WIDTH / 2, map.avatar.position.y -  AVATAR_HEIGHT / 2, side * AVATAR_WIDTH, AVATAR_HEIGHT)
        else
            batch.draw(keyFrame, map.avatar.position.x  - AVATAR_WIDTH / 2, map.avatar.position.y -  AVATAR_HEIGHT / 2, side * AVATAR_WIDTH, AVATAR_HEIGHT)
    }

    private fun renderBackground(time: Int, gameState: Int) {
        batch.disableBlending()
        batch.begin()
        batch.draw(Assets.backgroundRegion, guiCam.position.x -DEVICE_WIDTH / 2, guiCam.position.y - DEVICE_HEIGHT / 2, DEVICE_WIDTH,
                DEVICE_HEIGHT)
        batch.end()
        renderLava(time, gameState)
        batch.enableBlending()
        batch.begin()
        for (i in  0..10){
            batch.draw(Assets.spikeRegion, i * SPIKE_SIZE.toFloat(), 0f, SPIKE_SIZE.toFloat(),
                    SPIKE_SIZE.toFloat())
        }
        for (i in  0..10){
            batch.draw(Assets.spikeRegion, i * SPIKE_SIZE.toFloat(), DEVICE_HEIGHT, -SPIKE_SIZE.toFloat(),
                    -SPIKE_SIZE.toFloat())
        }

        for (i in  1..13){
            batch.draw(Assets.spikeRegion, 0f, i* SPIKE_SIZE.toFloat(), SPIKE_SIZE.toFloat(),
                    SPIKE_SIZE.toFloat(),  SPIKE_SIZE.toFloat(),  SPIKE_SIZE.toFloat(), 1f, 1f, 1f, true)
        }

        for (i in  1..13){
            batch.draw(Assets.spikeRegion, DEVICE_WIDTH - SPIKE_SIZE, i* SPIKE_SIZE.toFloat(), SPIKE_SIZE.toFloat(),
                    SPIKE_SIZE.toFloat(),  SPIKE_SIZE.toFloat(),  SPIKE_SIZE.toFloat(), 1f, 1f, 1f, false)
        }
        batch.draw(Assets.stoneRegion, map.platform.x, map.platform.y, PLATFORM_WIDTH, PLATFORM_HEIGHT)
        batch.end()

    }

    private fun renderAnt(time: Int) {
        if (time > 0) {
            if (time % 5 == 0) {
                if (map.ants[time / 5].state == ANT_STATE_NOT_SPAWN) {
                    map.ants[time / 5].state = ANT_STATE_NOT_HIT
                }
            }
            for (ant in map.ants) {
                if (ant.state == ANT_STATE_NOT_HIT) {
                    if(ant.type == ANT_TYPE_STATIC) {
                        batch.draw(Assets.antRegion, ant.position.x + ANT_WIDTH/ 2, ant.position.y - ANT_HEIGHT / 2, ANT_WIDTH, ANT_HEIGHT)
                    } else {
                        val side = (if (ant.speed.x < 0.5) -1 else 1).toFloat()
                        if (side < 0)
                            batch.draw(Assets.antRegion, ant.position.x + ANT_WIDTH / 2, ant.position.y - ANT_HEIGHT/ 2, side * ANT_WIDTH, ANT_HEIGHT)
                        else
                            batch.draw(Assets.antRegion, ant.position.x - ANT_WIDTH / 2, ant.position.y - ANT_HEIGHT / 2, side * ANT_WIDTH, ANT_HEIGHT)
                    }
                }
            }

        }
    }

    private fun renderLava(time: Int, gameState: Int){
        if(time > 5) {
            if(time % 30 == 0 || time % 30 == 1 || time % 30 == 2 || time % 30 == 3 || time % 30 == 4){
                if(!lavaSound) {
                    Assets.playSound(Assets.lavaSound)
                lavaSound = true }
                for (ant in map.ants) {
                    if (ant.state == ANT_STATE_NOT_HIT) {
                       ant.state = ANT_STATE_HIT
                    }
                }
                batch.begin()
                batch.draw(Assets.lavaRegion, 0f, 0f, DEVICE_WIDTH, DEVICE_HEIGHT)
                batch.end()
                if(gameState == GAME_RUNNING){
                    map.checkLavaCollisions()
                }
            } else {
                lavaSound = false
            }
        }
    }
}
