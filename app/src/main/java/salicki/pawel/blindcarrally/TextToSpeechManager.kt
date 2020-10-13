package salicki.pawel.blindcarrally

import android.speech.tts.TextToSpeech
import android.speech.tts.Voice
import android.util.Log
import androidx.core.os.bundleOf
import java.util.*
import kotlin.collections.HashMap

object TextToSpeechManager {
    private var textToSpeechLanguages: HashMap<LanguageTTS, TextToSpeech?> = hashMapOf(
        LanguageTTS.ENGLISH to null, LanguageTTS.POLISH to null)
    private var selectedLanguage: LanguageTTS = LanguageTTS.ENGLISH

    fun initTextToSpeech() {

        textToSpeechLanguages[LanguageTTS.ENGLISH] = TextToSpeech(Settings.CONTEXT) { status ->
            if (status != TextToSpeech.ERROR) {
                textToSpeechLanguages[LanguageTTS.ENGLISH]?.language =
                    Locale(LanguageTTS.ENGLISH.locale)
            }
        }

        textToSpeechLanguages[LanguageTTS.POLISH] = TextToSpeech(Settings.CONTEXT) { status ->
            if (status != TextToSpeech.ERROR) {
                textToSpeechLanguages[LanguageTTS.POLISH]?.language =
                    Locale(LanguageTTS.POLISH.locale)
                var a: Set<String> = hashSetOf("male")
                val v = Voice("pl-pl-x-bmg-local", Locale("pl", "PL"), 400, 200, false, a)
                textToSpeechLanguages[LanguageTTS.POLISH]?.voice = v
            }
        }
    }

    fun setPitch(pitch: Float) {
        textToSpeechLanguages[selectedLanguage]?.setPitch(pitch)
    }

    fun setSpeechRate(speechRate: Float) {
        textToSpeechLanguages[selectedLanguage]?.setSpeechRate(speechRate)
    }


    fun setLanguage(language: LanguageTTS) {
        selectedLanguage = language
    }

    fun isSpeaking(): Boolean {
        if(textToSpeechLanguages[selectedLanguage] != null){
            if(textToSpeechLanguages[selectedLanguage]?.isSpeaking!!){
                return true
            }
        }

        return false
    }

    fun destroy() {
        for ((k, v) in textToSpeechLanguages) {
            textToSpeechLanguages[k]?.stop()
            textToSpeechLanguages[k]?.shutdown()
        }
    }

    fun changeLanguage(language : LanguageTTS){
        selectedLanguage = language
    }

    fun stop() {
        textToSpeechLanguages[selectedLanguage]?.stop()
    }

    fun speakQueue(text: String) {
        val params = bundleOf(TextToSpeech.Engine.KEY_PARAM_VOLUME to (Settings.reader * 0.1F))

        textToSpeechLanguages[selectedLanguage]?.speak(text, TextToSpeech.QUEUE_ADD, params, null)
    }

    fun speakNow(text: String) {
        val params = bundleOf(TextToSpeech.Engine.KEY_PARAM_VOLUME to (Settings.reader * 0.1F))

        textToSpeechLanguages[selectedLanguage]?.speak(text, TextToSpeech.QUEUE_FLUSH, params, null)
    }
}
