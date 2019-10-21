package com.tremblebeforeme.game

import com.tremblebeforeme.game.Ant.Companion.ANT_SCORE
import com.tremblebeforeme.game.Ant.Companion.ANT_STATE_HIT
import com.tremblebeforeme.game.Ant.Companion.ANT_STATE_NOT_HIT
import com.tremblebeforeme.game.Avatar.Companion.AVATAR_HEIGHT
import com.tremblebeforeme.game.Avatar.Companion.AVATAR_WIDTH
import com.tremblebeforeme.game.MainMenuScreen.Companion.DEVICE_HEIGHT
import com.tremblebeforeme.game.MainMenuScreen.Companion.DEVICE_WIDTH
import com.tremblebeforeme.game.Platform.Companion.PLATFORM_HEIGHT
import com.tremblebeforeme.game.Platform.Companion.PLATFORM_WIDTH
import java.util.*
import kotlin.collections.ArrayList

class Map(var listener: MapListener) {

    companion object {
        const val AVATAR_POSITION_X = DEVICE_WIDTH / 2
        const val AVATAR_POSITION_Y = DEVICE_HEIGHT / 2
        const val SPIKE_SIZE = 32
        const val SPIKE_SCORE = 500
        const val LAVA_SCORE = 500
    }

    var avatar: Avatar = Avatar(AVATAR_POSITION_X, AVATAR_POSITION_Y)
    private var spikeTimer = System.currentTimeMillis()
    private var lavaTimer = System.currentTimeMillis()
    private var soundTimer = System.currentTimeMillis()
    val ants: ArrayList<Ant> = ArrayList()
    val platform = Platform(0f,0f)
    private var rand = Random()
    var score = 0

    init {
        generateMap()
    }

    private fun generateMap() {
        platform.x = rand.nextFloat()* (DEVICE_WIDTH - PLATFORM_WIDTH - SPIKE_SIZE -  PLATFORM_WIDTH / 2 - SPIKE_SIZE - 1) + PLATFORM_WIDTH / 2 + SPIKE_SIZE
        platform.y = rand.nextFloat()* (DEVICE_HEIGHT - PLATFORM_HEIGHT - SPIKE_SIZE - PLATFORM_HEIGHT / 2 - SPIKE_SIZE - 1) + PLATFORM_HEIGHT / 2 + SPIKE_SIZE

        for ( i in 0..36) {
            val x = rand.nextFloat()* (DEVICE_WIDTH - Ant.ANT_WIDTH - SPIKE_SIZE -  Ant.ANT_WIDTH / 2 - SPIKE_SIZE - 1) + Ant.ANT_WIDTH / 2 + SPIKE_SIZE
            val y = rand.nextFloat()* (DEVICE_HEIGHT - Ant.ANT_HEIGHT - SPIKE_SIZE - Ant.ANT_HEIGHT / 2 - SPIKE_SIZE - 1) + Ant.ANT_HEIGHT / 2 + SPIKE_SIZE

            val type = if (rand.nextFloat() > 0.8f) Ant.ANT_TYPE_MOVING else Ant.ANT_TYPE_STATIC

            val ant = Ant(type,x,y)

            ants.add(ant)
        }

    }

    fun update(deltaTime: Float, accelX: Float, accelY: Float) {
        updateAvatar(deltaTime, accelX, accelY)
        updateAnts(deltaTime)
        checkCollisions()
    }

    private fun updateAnts(deltaTime: Float) {
        for (ant in ants){
            if (ant.state == ANT_STATE_NOT_HIT)
            ant.update(deltaTime)
        }
    }

    private fun updateAvatar(deltaTime: Float, accelX: Float, accelY: Float) {
        avatar.speed.x = -accelX / 3 * Avatar.AVATAR_MOVE_SPEED
        avatar.speed.y = -accelY / 3 * Avatar.AVATAR_MOVE_SPEED
        if(score > 1000){
            avatar.speed.y = -accelY / 3 * Avatar.AVATAR_MOVE_SPEED * score / 2000
            avatar.speed.x = -accelX / 3 * Avatar.AVATAR_MOVE_SPEED * score / 2000
        }

        if (avatar.position.x <= SPIKE_SIZE + AVATAR_WIDTH /2 ) {
            avatar.speed.x =0f
            if (-accelX / 3 * Avatar.AVATAR_MOVE_SPEED > 0)
                if(score > 1000)
                    avatar.speed.x = -accelX / 3 * Avatar.AVATAR_MOVE_SPEED * score / 2000
                else
                    avatar.speed.x = -accelX / 3 * Avatar.AVATAR_MOVE_SPEED
        }
        if (avatar.position.x >= DEVICE_WIDTH - SPIKE_SIZE - AVATAR_WIDTH / 2) {
            avatar.speed.x =0f
            if (-accelX / 3 * Avatar.AVATAR_MOVE_SPEED < 0)
                if(score > 1000)
                    avatar.speed.x = -accelX / 3 * Avatar.AVATAR_MOVE_SPEED * score / 2000
                else
                    avatar.speed.x = -accelX / 3 * Avatar.AVATAR_MOVE_SPEED
        }
        if (avatar.position.y <= SPIKE_SIZE + AVATAR_HEIGHT /2 ) {
            avatar.speed.y =0f
            if (-accelY / 3 * Avatar.AVATAR_MOVE_SPEED > 0)
                if(score > 1000)
                    avatar.speed.y = -accelY / 3 * Avatar.AVATAR_MOVE_SPEED * score / 2000
                else
                    avatar.speed.y = -accelY / 3 * Avatar.AVATAR_MOVE_SPEED
        }
        if (avatar.position.y >= DEVICE_HEIGHT - SPIKE_SIZE - AVATAR_HEIGHT / 2) {
            avatar.speed.y =0f
            if (-accelY / 3 * Avatar.AVATAR_MOVE_SPEED < 0)
                if(score > 1000)
                    avatar.speed.y = -accelY / 3 * Avatar.AVATAR_MOVE_SPEED * score / 2000
                else
                    avatar.speed.y = -accelY / 3 * Avatar.AVATAR_MOVE_SPEED
        }
        avatar.update(deltaTime)
    }

    private fun checkCollisions() {
        checkAntCollisions()
        if((System.currentTimeMillis() - spikeTimer) % 1000 >= 200){
            checkSpikeCollisions()
            spikeTimer = System.currentTimeMillis()
        }
    }

    fun checkLavaCollisions() {

        if((System.currentTimeMillis() - lavaTimer) % 1000 >= 200){
            if (avatar.position.x  > platform.x  + PLATFORM_WIDTH - AVATAR_WIDTH / 2 || avatar.position.x - AVATAR_WIDTH / 2 < platform.x
                    || avatar.position.y  > platform.y  + PLATFORM_HEIGHT - AVATAR_HEIGHT / 2 || avatar.position.y - AVATAR_HEIGHT / 2 < platform.y) {
                if((System.currentTimeMillis() - soundTimer) % 1000  >= 400){
                    listener.lavaHit()
                    soundTimer = System.currentTimeMillis()
                }
                if(score > 0){
                    score -= LAVA_SCORE
                }
            }
            lavaTimer = System.currentTimeMillis()
        }
    }

    private fun checkSpikeCollisions() {
        if (avatar.position.x <= SPIKE_SIZE  + AVATAR_WIDTH /2|| avatar.position.x >= DEVICE_WIDTH - SPIKE_SIZE - AVATAR_WIDTH / 2
                || avatar.position.y >= DEVICE_HEIGHT - SPIKE_SIZE - AVATAR_HEIGHT / 2 || avatar.position.y <= SPIKE_SIZE + AVATAR_HEIGHT /2) {
            if((System.currentTimeMillis() - soundTimer) % 1000  >= 400){
                listener.spike()
                soundTimer = System.currentTimeMillis()
            }
            if(score > 0){
                score -= SPIKE_SCORE
            }
        }

    }

    private fun checkAntCollisions() {
        for (ant in ants){
            if ( ant.state == ANT_STATE_NOT_HIT) {
                if(ant.bounds.overlaps(avatar.bounds)){
                    ant.state = ANT_STATE_HIT
                    listener.ant()
                    score += ANT_SCORE
                }
            }
        }
    }

}