package com.tremblebeforeme.game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.tremblebeforeme.game.MainMenuScreen.Companion.USER_PREF_SOUND

object Assets {

    private var background: Texture
    private var items: Texture
    var ant: Texture
    var font: BitmapFont
    var avatarWalk: Animation
    var antRegion: TextureRegion
    var pauseMenu: TextureRegion
    var mainMenu: TextureRegion
    var arrow: TextureRegion
    var soundOn: TextureRegion
    var spike: Texture
    var spikeRegion: TextureRegion
    var stone: Texture
    var stoneRegion: TextureRegion
    var lava: Texture
    var lavaRegion: TextureRegion
    var soundOff: TextureRegion
    var backgroundRegion: TextureRegion
    var music: Music
    var hitSound: Sound
    var lavaSound: Sound
    var spikeSound: Sound
    var lavaHitSound: Sound
    var warningSound: Sound
    var pause: TextureRegion

    init {
        background = loadTexture("background2.png")
        backgroundRegion = TextureRegion(background, 0, 0, 2048, 3072)

        items = loadTexture("items.png")
        ant = loadTexture("ant.png")
        spike = loadTexture("spike.png")
        stone = loadTexture("stone.png")
        lava = loadTexture("lava3.png")
        lavaRegion = TextureRegion(lava, 0, 0, 1080, 1920)
        stoneRegion = TextureRegion(stone, 0, 0, 339, 335)
        mainMenu = TextureRegion(items, 0, 224, 300, 110)
        pauseMenu = TextureRegion(items, 224, 128, 192, 96)
        pause = TextureRegion(items, 64, 64, 64, 64)
        avatarWalk = Animation(0.2f, TextureRegion(items, 0, 128, 32, 32), TextureRegion(items, 32, 128, 32, 32))
        antRegion = TextureRegion(ant, 0, 0, 500, 500)
        spikeRegion = TextureRegion(spike, 0, 0, 184, 195)
        arrow = TextureRegion(items, 0, 64, 64, 64)

        font = BitmapFont(Gdx.files.internal("font.fnt"), Gdx.files.internal("font.png"), false)

        soundOff = TextureRegion(items, 0, 0, 64, 64)
        soundOn = TextureRegion(items, 64, 0, 64, 64)
        music = Gdx.audio.newMusic(Gdx.files.internal("mainmenumusic.wav"))
        music.isLooping = true
        music.volume = 0.5f
        if(MainMenuScreen.prefs.getBoolean(USER_PREF_SOUND, true))music.play()
        hitSound = Gdx.audio.newSound(Gdx.files.internal("hit.wav"))
        lavaSound = Gdx.audio.newSound(Gdx.files.internal("lava.mp3"))
        spikeSound =  Gdx.audio.newSound(Gdx.files.internal("spike.mp3"))
        lavaHitSound = Gdx.audio.newSound(Gdx.files.internal("lavaHit.mp3"))
        warningSound = Gdx.audio.newSound(Gdx.files.internal("warning.mp3"))
    }

    fun loadTexture(file: String): Texture {
        return Texture(Gdx.files.internal(file))
    }

    fun playSound(sound: Sound) {
        if (MainMenuScreen.prefs.getBoolean(USER_PREF_SOUND, true)) sound.play(1f)
    }


}