package salicki.pawel.blindcarrally

import android.speech.tts.TextToSpeech
import android.speech.tts.Voice
import android.util.Log
import androidx.core.os.bundleOf
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
                var a: Set<String> = hashSetOf("male")
                val v = Voice("pl-pl-x-bmg-local", Locale("pl", "PL"), 400, 200, false, a)
                textToSpeechPolish?.voice = v
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
        val params = bundleOf(TextToSpeech.Engine.KEY_PARAM_VOLUME to (Settings.reader * 0.1F))

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
        val params = bundleOf(TextToSpeech.Engine.KEY_PARAM_VOLUME to (Settings.reader * 0.1F))

        if (Settings.languageTTS == LanguageTTS.ENGLISH) {
            if (textToSpeechEnglish != null) {
                textToSpeechEnglish?.speak(text, TextToSpeech.QUEUE_FLUSH, params, null)
            }
        } else {
            if (textToSpeechPolish != null) {
                textToSpeechPolish?.speak(text, TextToSpeech.QUEUE_FLUSH, params, null)
            }
        }
    }
}
