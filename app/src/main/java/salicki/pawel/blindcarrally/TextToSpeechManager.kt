package salicki.pawel.blindcarrally

import android.content.Context
import android.os.Build
import android.speech.tts.TextToSpeech
import android.util.Log
import java.util.*

object TextToSpeechManager {
    private  var textToSpeech: TextToSpeech? = null

    init {
        textToSpeech = TextToSpeech(Settings.CONTEXT) { status ->
            if (status != TextToSpeech.ERROR) {
                textToSpeech?.language = Locale.ENGLISH
            }
        }
    }
    
    fun setPitch(pitch : Float) {
        if(textToSpeech != null) {
            textToSpeech?.setPitch(pitch)
        }
    }

    fun setSpeechRate(speechRate : Float) {
        if(textToSpeech != null) {
            textToSpeech?.setSpeechRate(speechRate)
        }
    }

    fun setLanguage(language: LanguageTTS) {
        if(textToSpeech != null){
            textToSpeech?.language = Locale(language.locale)
        }
    }

    fun isSpeaking() : Boolean {
        if(textToSpeech != null) {
            return textToSpeech!!.isSpeaking
        }

        return false
    }

    fun pause() {
        if(textToSpeech != null){
            textToSpeech?.stop()
            textToSpeech?.shutdown()
        }
    }

    fun speak(text: String) {
        Log.d("JESTEM", "JESTEM")

        if(textToSpeech != null) {
            textToSpeech?.speak(text, TextToSpeech.QUEUE_FLUSH,null,null);

            Log.d("W IFIE", "W IFIE")
        }
    }
}