package salicki.pawel.blindcarrally.scene

import android.graphics.Canvas
import android.os.CountDownTimer
import android.view.MotionEvent
import android.view.SurfaceView
import salicki.pawel.blindcarrally.*
import salicki.pawel.blindcarrally.scenemanager.ILevel
import salicki.pawel.blindcarrally.scenemanager.LevelManager
import java.util.*
import kotlin.collections.HashMap


class SplashLevel() : SurfaceView(Settings.CONTEXT), ILevel {

    private var texts: HashMap<String, String> = HashMap()
    private var languageTypeData: ArrayList<LanguageTTS> =
        arrayListOf(LanguageTTS.ENGLISH, LanguageTTS.POLISH)

    private var splashImage = OptionImage()

    private var languageSelection: Boolean = true

    init {
        isFocusable = true

        readTTSTextFile()

        initSplashScreen()
        checkLanguageOption()
        initScreenTransitionTimer()
    }

    private fun initSplashScreen(){
        splashImage.setImage(
            R.drawable.logo,
            Settings.SCREEN_WIDTH / 2,
            (Settings.SCREEN_HEIGHT / 5).toInt(),
            R.dimen.screenSize
        )
    }

    private fun initScreenTransitionTimer() {
        if (languageSelection) {
                 var timer = object : CountDownTimer(5000, 5000) {
                    override fun onTick(millisUntilFinished: Long) {}
                    override fun onFinish() {
                        LevelManager.changeLevel(LanguageLevel(LanguageLevelFlowEnum.INTRODUCTION))
                    }
                }
                timer.start()
        } else {
            var timer = object : CountDownTimer(5000, 5000) {
                override fun onTick(millisUntilFinished: Long) {}
                override fun onFinish() {
                    LevelManager.changeLevel(InformationLevel())
                }
            }
            timer.start()
        }
    }

    private fun readTTSTextFile() {
        texts.putAll(OpenerCSV.readData(R.raw.splash_tts, LanguageTTS.ENGLISH))
    }

    override fun initState() {
        TextToSpeechManager.speakNow(texts["SPLASH_LOGO"].toString())
    }

    override fun updateState(deltaTime: Int) {
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
            Settings.languageTTS = languageTypeData[language.toInt()]
            TextToSpeechManager.setLanguage(Settings.languageTTS)
        }
    }

    private fun drawSplashScreen(canvas: Canvas) {
        splashImage.drawImage(canvas)
    }
}