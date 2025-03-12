package com.knyazev.lingualearn.sound

import android.content.Context
import android.media.MediaPlayer
import com.knyazev.lingualearn.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton // SoundManager должен быть один на все приложение
class SoundManager @Inject constructor(@ApplicationContext private val context: Context) {

    private var mediaPlayer: MediaPlayer? = null

    //HashMap для хранения звуков
    private val soundPoolMap = HashMap<Int, MediaPlayer>()

    fun playBackgroundMusic() {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(context, R.raw.background_music).apply { //Загружаем
                isLooping = true // Зацикливаем
                start() //Начинаем проигрывать
            }
        }
    }
    fun stopBackgroundMusic() {
        mediaPlayer?.stop() //Останавливаем
        mediaPlayer?.release() //Освобождаем
        mediaPlayer = null
    }

    private fun playSound(soundResId: Int) {
        if (!soundPoolMap.containsKey(soundResId)) { //Если звука нет, загружаем
            soundPoolMap[soundResId] = MediaPlayer.create(context, soundResId)
        }
        soundPoolMap[soundResId]?.start() //Проигрываем
    }
    fun playClickSound() {
        playSound(R.raw.click_sound)
    }

    fun playUpgradeSound() {
        playSound(R.raw.upgrade_sound)
    }
    //Освобождаем все ресурсы
    fun release() {
        soundPoolMap.values.forEach {
            it.stop()
            it.release()
        }
        soundPoolMap.clear()
        stopBackgroundMusic() //Останавливаем музыку
    }
}