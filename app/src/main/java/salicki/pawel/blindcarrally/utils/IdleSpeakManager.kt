package salicki.pawel.blindcarrally.utils

import salicki.pawel.blindcarrally.gameresources.TextToSpeechManager
import salicki.pawel.blindcarrally.information.Settings

class IdleSpeakManager {
    private var idleTime: Int = 0
    private var idleTimeSeconds: Int = 0
    private var textToSpeak: String = ""

    fun initIdleString(textToSpeak: String = "") {
        this.textToSpeak = textToSpeak
    }

    fun updateIdleStatus() {
        if (!TextToSpeechManager.isSpeaking()) {
            idleTime++

            if (idleTime % Settings.FPS == 0) {
                idleTimeSeconds++
            }
        }

        if (idleTimeSeconds > Settings.FPS / 3) {
            TextToSpeechManager.speakNow(textToSpeak)

            idleTimeSeconds = 0
        }
    }

    fun resetIdleTimeSeconds() {
        idleTimeSeconds = 0
    }
}