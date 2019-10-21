package com.tremblebeforeme.game

import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2

open class GameObject(x:Float, y:Float, width: Float, height:Float) {
    var position: Vector2 = Vector2(x, y)
    var bounds: Rectangle = Rectangle(x + width/2, y - height /2, width, height)
}