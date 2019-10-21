package com.tremblebeforeme.game

import com.badlogic.gdx.graphics.g2d.TextureRegion
import kotlin.math.min

class Animation(private val frameDuration: Float, vararg keyFrames: TextureRegion) {

    private val keyFrames: Array<TextureRegion> = keyFrames as Array<TextureRegion>

    fun getKeyFrame(stateTime: Float, mode: Int): TextureRegion {
        var frameNumber = (stateTime / frameDuration).toInt()

        if (mode == ANIMATION_NONLOOPING) {
            frameNumber = min(keyFrames.size - 1, frameNumber)
        } else {
            frameNumber %= keyFrames.size
        }
        return keyFrames[frameNumber]
    }

    companion object {
        const val ANIMATION_LOOPING = 0
        const val ANIMATION_NONLOOPING = 1
    }
}