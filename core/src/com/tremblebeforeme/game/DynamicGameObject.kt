package com.tremblebeforeme.game

import com.badlogic.gdx.math.Vector2

open class DynamicGameObject(x:Float, y:Float, width: Float, height:Float): GameObject(x,y, width, height) {
    var speed = Vector2()
}