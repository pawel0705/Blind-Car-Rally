package salicki.pawel.blindcarrally.gameresources

import android.speech.tts.TextToSpeech
import android.speech.tts.Voice
import androidx.core.os.bundleOf
import salicki.pawel.blindcarrally.enums.LanguageTtsEnum
import salicki.pawel.blindcarrally.information.Settings
import java.util.*
import kotlin.collections.HashMap

object TextToSpeechManager {
    private var selectedLanguage: LanguageTtsEnum = LanguageTtsEnum.ENGLISH

    private var textToSpeechLanguages: HashMap<LanguageTtsEnum, TextToSpeech?> = hashMapOf(
        LanguageTtsEnum.ENGLISH to null, LanguageTtsEnum.POLISH to null
    )

    fun initTextToSpeech() {
        textToSpeechLanguages[LanguageTtsEnum.ENGLISH] = TextToSpeech(Settings.CONTEXT) { status ->
            if (status != TextToSpeech.ERROR) {
                textToSpeechLanguages[LanguageTtsEnum.ENGLISH]?.language =
                    Locale(LanguageTtsEnum.ENGLISH.locale)
            }
        }

        textToSpeechLanguages[LanguageTtsEnum.POLISH] = TextToSpeech(Settings.CONTEXT) { status ->
            if (status != TextToSpeech.ERROR) {
                textToSpeechLanguages[LanguageTtsEnum.POLISH]?.language =
                    Locale(LanguageTtsEnum.POLISH.locale)
                var a: Set<String> = hashSetOf("male")
                val v = Voice("pl-pl-x-bmg-local", Locale("pl", "PL"), 400, 200, false, a)
                textToSpeechLanguages[LanguageTtsEnum.POLISH]?.voice = v
            }
        }
    }

    fun setLanguage(language: LanguageTtsEnum) {
        selectedLanguage = language
    }

    fun isSpeaking(): Boolean {
        if (textToSpeechLanguages[selectedLanguage] != null) {
            if (textToSpeechLanguages[selectedLanguage]?.isSpeaking!!) {
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

    fun changeLanguage(language: LanguageTtsEnum) {
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
