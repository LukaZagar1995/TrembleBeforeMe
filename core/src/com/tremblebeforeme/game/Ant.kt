package com.tremblebeforeme.game

import com.tremblebeforeme.game.MainMenuScreen.Companion.DEVICE_WIDTH
import com.tremblebeforeme.game.Map.Companion.SPIKE_SIZE

data class Ant(var type: Int , var x: Float ,var y: Float): DynamicGameObject(x, y, ANT_WIDTH, ANT_HEIGHT) {

    companion object{
        const val ANT_WIDTH = 20.0f
        const val ANT_HEIGHT = 20.0f
        const val ANT_TYPE_STATIC = 0
        const val ANT_TYPE_MOVING = 1
        const val ANT_STATE_HIT = 0
        const val ANT_STATE_NOT_HIT = 1
        const val ANT_STATE_NOT_SPAWN = 2
        const val ANT_SPEED = 100f
        const val ANT_SCORE = 1000
    }

    var stateTime: Float = 0.0f
    var state = ANT_STATE_NOT_SPAWN

    init {
        if (type == ANT_TYPE_MOVING) {
            speed.set(ANT_SPEED, 0.0f)
        }
    }

    fun update(deltaTime: Float) {
        if (type == ANT_TYPE_MOVING) {
        position.add(speed.x * deltaTime, speed.y * deltaTime)
            bounds.x = position.x - bounds.width / 2
            bounds.y = position.y - bounds.height / 2

        if (position.x < ANT_WIDTH / 2 + SPIKE_SIZE) {
            position.x = ANT_WIDTH / 2 + SPIKE_SIZE
            speed.x = ANT_SPEED
        }

        if (position.x > DEVICE_WIDTH - ANT_WIDTH / 2 - SPIKE_SIZE) {
            position.x = DEVICE_WIDTH - ANT_WIDTH / 2 - SPIKE_SIZE
            speed.x = - ANT_SPEED
        }
            }
        stateTime += deltaTime
    }

}