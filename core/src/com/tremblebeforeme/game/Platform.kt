package com.tremblebeforeme.game

import com.tremblebeforeme.game.Avatar.Companion.AVATAR_HEIGHT
import com.tremblebeforeme.game.Avatar.Companion.AVATAR_WIDTH

class Platform(var x:Float, var y:Float): GameObject(x, y, PLATFORM_HEIGHT, PLATFORM_WIDTH) {

    companion object {
        const val PLATFORM_HEIGHT = AVATAR_HEIGHT * 2
        const val PLATFORM_WIDTH = AVATAR_WIDTH * 2
    }

}