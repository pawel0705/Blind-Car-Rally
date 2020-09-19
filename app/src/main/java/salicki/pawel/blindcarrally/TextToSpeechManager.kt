package salicki.pawel.blindcarrally

import android.content.Context
import android.os.Build
import android.speech.tts.TextToSpeech
import android.util.Log
import java.util.*

object TextToSpeechManager {
    private var textToSpeechEnglish: TextToSpeech? = null
    private var textToSpeechPolish: TextToSpeech? = null

    fun initTextToSpeech() {
        textToSpeechEnglish = TextToSpeech(Settings.CONTEXT) { status ->
            if (status != TextToSpeech.ERROR) {
                textToSpeechEnglish?.language = Locale(LanguageTTS.ENGLISH.locale)
            }
        }

        textToSpeechPolish = TextToSpeech(Settings.CONTEXT) { status ->
            if (status != TextToSpeech.ERROR) {
                textToSpeechPolish?.language = Locale(LanguageTTS.POLISH.locale)
            }
        }
    }

    fun setPitch(pitch: Float) {
        if (textToSpeechEnglish != null) {
            textToSpeechEnglish?.setPitch(pitch)
        }

        if (textToSpeechPolish != null) {
            textToSpeechPolish?.setPitch(pitch)
        }
    }

    fun setSpeechRate(speechRate: Float) {
        if (textToSpeechEnglish != null) {
            textToSpeechEnglish?.setSpeechRate(speechRate)
        }

        if (textToSpeechPolish != null) {
            textToSpeechPolish?.setSpeechRate(speechRate)
        }
    }

    /*
    fun setLanguage(language: LanguageTTS) {
        if(textToSpeechEnglish != null){
            textToSpeechEnglish?.language = Locale(language.locale)
        }
    }
*/


    fun isSpeaking(): Boolean {
        if (Settings.languageTTS == LanguageTTS.ENGLISH) {
            if (textToSpeechEnglish != null) {
                return textToSpeechEnglish!!.isSpeaking
            }
        } else {
            if (textToSpeechPolish != null) {
                return textToSpeechPolish!!.isSpeaking
            }
        }

        return false
    }

    fun destroy() {
        if (Settings.languageTTS == LanguageTTS.ENGLISH) {
            if (textToSpeechEnglish != null) {
                textToSpeechEnglish?.stop()
                textToSpeechEnglish?.shutdown()
            }
        } else {
            if (textToSpeechPolish != null) {
                textToSpeechPolish?.stop()
                textToSpeechPolish?.shutdown()
            }
        }

    }

    fun stop() {
        if (Settings.languageTTS == LanguageTTS.ENGLISH) {
            if (textToSpeechEnglish != null) {
                textToSpeechEnglish?.stop()
            }
        } else {
            if (textToSpeechPolish != null) {
                textToSpeechPolish?.stop()
            }
        }
    }

    fun speakQueue(text: String) {

        Log.d("QUEUE", Settings.languageTTS.toString())

        if (Settings.languageTTS == LanguageTTS.ENGLISH) {
            if (textToSpeechEnglish != null) {
                textToSpeechEnglish?.speak(text, TextToSpeech.QUEUE_ADD, null, null)
            }
        } else {
            if (textToSpeechPolish != null) {
                textToSpeechPolish?.speak(text, TextToSpeech.QUEUE_ADD, null, null)
            }
        }
    }

    fun speakNow(text: String) {
        Log.d("NOW", Settings.languageTTS.toString())

        if (Settings.languageTTS == LanguageTTS.ENGLISH) {

            Log.d("NOW", "W ANGIELSKIM")

            if (textToSpeechEnglish != null) {
                textToSpeechEnglish?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
            }
        }else {
            Log.d("NOW", "W POLSKIM")

            if (textToSpeechPolish != null) {
                textToSpeechPolish?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
            }
        }
    }
}