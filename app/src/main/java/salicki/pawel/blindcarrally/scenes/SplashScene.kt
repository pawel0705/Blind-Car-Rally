package salicki.pawel.blindcarrally.scenes

import android.graphics.Canvas
import android.os.CountDownTimer
import android.view.MotionEvent
import android.view.SurfaceView
import salicki.pawel.blindcarrally.*
import salicki.pawel.blindcarrally.enums.LanguageLevelFlowEnum
import salicki.pawel.blindcarrally.enums.LanguageTtsEnum
import salicki.pawel.blindcarrally.gameresources.OptionImage
import salicki.pawel.blindcarrally.gameresources.TextToSpeechManager
import salicki.pawel.blindcarrally.information.Settings
import salicki.pawel.blindcarrally.resources.DrawableResources
import salicki.pawel.blindcarrally.resources.RawResources
import salicki.pawel.blindcarrally.scenemanager.ILevel
import salicki.pawel.blindcarrally.scenemanager.LevelManager
import salicki.pawel.blindcarrally.utils.OpenerCSV
import salicki.pawel.blindcarrally.utils.SharedPreferencesManager
import salicki.pawel.blindcarrally.utils.SoundManager
import java.util.*
import kotlin.collections.HashMap


class SplashScene() : SurfaceView(Settings.CONTEXT), ILevel {
    private var texts: HashMap<String, String> = HashMap()
    private var languageTypeData: ArrayList<LanguageTtsEnum> =
        arrayListOf(LanguageTtsEnum.ENGLISH, LanguageTtsEnum.POLISH)

    private var soundManager: SoundManager =
        SoundManager()
    private var splashImage =
        OptionImage()

    private var waitIterator: Int = 0
    private var waitTime: Int = 60

    private var languageSelection: Boolean = true
    private var loading: Boolean = false

    init {
        isFocusable = true

        readTTSTextFile()

        initSplashScreen()
        initSoundManager()
        checkLanguageOption()
        checkVibrationOption()
        checkInformationOption()
        checkVolumeTTSOption()
        checkVolumeSoundsOption()
        checkDisplayScreenOption()
        initScreenTransitionTimer()
    }

    private fun initSoundManager(){
        soundManager.initSoundManager()

        soundManager.addSound(RawResources.loadingSound)
    }

    private fun initSplashScreen() {
        splashImage.setImage(
            DrawableResources.logoView,
            Settings.SCREEN_WIDTH / 2,
            (Settings.SCREEN_HEIGHT / 5).toInt(),
            R.dimen.screenSize
        )
    }

    private fun initScreenTransitionTimer() {
        if (languageSelection) {
            var timer = object : CountDownTimer(8500, 8500) {
                override fun onTick(millisUntilFinished: Long) {}
                override fun onFinish() {
                    LevelManager.changeLevel(LanguageScene(LanguageLevelFlowEnum.INTRODUCTION))
                }
            }
            timer.start()
        } else {
            var timer = object : CountDownTimer(8500, 8500) {
                override fun onTick(millisUntilFinished: Long) {}
                override fun onFinish() {
                    if (Settings.introduction) {
                        LevelManager.changeLevel(InformationScene())
                    } else {
                        LevelManager.changeLevel(MenuScene())
                    }
                }
            }
            timer.start()
        }
    }

    private fun readTTSTextFile() {
        texts.putAll(OpenerCSV.readData(RawResources.splash_TTS, LanguageTtsEnum.ENGLISH))
    }

    override fun initState() {
        TextToSpeechManager.speakNow(texts["SPLASH_LOGO"].toString())

    }

    override fun updateState() {
        waitIterator++
        if(!loading && waitIterator > waitTime){
            soundManager.playSound(RawResources.loadingSound)
            loading = true
        }
    }

    override fun destroyState() {
        isFocusable = false
    }

    override fun respondTouchState(event: MotionEvent) {
    }

    override fun redrawState(canvas: Canvas) {
        this.drawSplashScreen(canvas)
    }

    private fun checkLanguageOption() {
        var language = SharedPreferencesManager.loadConfiguration("language")
        if (language != null && language != "") {
            languageSelection = false
            Settings.languageTtsEnum = languageTypeData[language.toInt()]
            TextToSpeechManager.setLanguage(Settings.languageTtsEnum)
        }
    }

    private fun checkDisplayScreenOption() {
        var display = SharedPreferencesManager.loadConfiguration("display")
        if (display != null && display != "") {
            Settings.display = display == "1"
        }
    }

    private fun checkVibrationOption() {
        var vibrations = SharedPreferencesManager.loadConfiguration("vibrations")
        if (vibrations != null && vibrations != "") {
            Settings.vibrations = vibrations == "1"
        }
    }

    private fun checkInformationOption() {
        var introduction = SharedPreferencesManager.loadConfiguration("introduction")
        if (introduction != null && introduction != "") {
            Settings.introduction = introduction == "1"
        }
    }

    private fun checkVolumeSoundsOption() {
        var sounds = SharedPreferencesManager.loadConfiguration("sounds")
        if (sounds != null && sounds != "") {
            Settings.sounds = sounds.toInt()
        }
    }

    private fun checkVolumeTTSOption() {
        var reader = SharedPreferencesManager.loadConfiguration("reader")
        if(reader != null && reader != ""){
            Settings.reader = reader.toInt()
        }
    }

    private fun drawSplashScreen(canvas: Canvas) {
        splashImage.drawImage(canvas)
    }
}