package com.tremblebeforeme.game

class Avatar(x: Float, y: Float): DynamicGameObject(x, y, AVATAR_WIDTH, AVATAR_HEIGHT){

    companion object {
        const val AVATAR_MOVE_SPEED = 50
        const val AVATAR_WIDTH = 23f
        const val AVATAR_HEIGHT = 23f
    }

    var stateTime: Float = 0.0f

    fun update(deltaTime: Float){
        speed.add(0.0f, 0.0f)
        position.add(speed.x * deltaTime, speed.y * deltaTime)
        bounds.x = position.x - bounds.width / 2
        bounds.y = position.y - bounds.height / 2
        stateTime += deltaTime
    }

}