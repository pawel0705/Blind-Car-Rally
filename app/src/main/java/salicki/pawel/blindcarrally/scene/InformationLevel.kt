package salicki.pawel.blindcarrally.scene

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.view.MotionEvent
import android.view.SurfaceView
import salicki.pawel.blindcarrally.*
import salicki.pawel.blindcarrally.scenemanager.ILevel
import salicki.pawel.blindcarrally.scenemanager.LevelManager

class InformationLevel : SurfaceView(Settings.CONTEXT), ILevel {

    private var texts: HashMap<String, String> = HashMap()

    private var infoTextPaint = TextPaint()
    private lateinit var textLayout: StaticLayout
    private var posX = 0F
    private var posY = 0F

    private var idleTime: Int = 0
    private var idleTimeSeconds: Int = 0

    init {
        isFocusable = true

        readTTSTextFile()
        initInformationText()
    }

    private fun initInformationText(){

        infoTextPaint.color = Color.WHITE
        infoTextPaint.textAlign = Paint.Align.CENTER

        posX = Settings.SCREEN_WIDTH / 2F
        posY = (0 - (infoTextPaint.descent() + infoTextPaint.ascent()) / 2)

        infoTextPaint.textSize = Settings.CONTEXT?.resources?.getDimensionPixelSize(R.dimen.informationSize)!!.toFloat()

        textLayout =  StaticLayout(texts["INTRODUCTION_TUTORIAL"].toString(),
            infoTextPaint, Settings.SCREEN_WIDTH, Layout.Alignment.ALIGN_NORMAL,
            1.0f, 0.0f, false);
    }

    private fun readTTSTextFile() {
        texts.putAll(OpenerCSV.readData(R.raw.introduction_tts, LanguageTTS.ENGLISH))
    }

    override fun initState() {
        TextToSpeechManager.speakNow(texts["INTRODUCTION_TUTORIAL"].toString())
    }

    override fun updateState(deltaTime: Int) {
        if(TextToSpeechManager.isSpeaking()){
            idleTime = 0
        }

        idleTime++

        if(idleTime % 30 == 0){
            idleTimeSeconds++
        }

        if(idleTimeSeconds > 10 && !TextToSpeechManager.isSpeaking()){
            TextToSpeechManager.speakNow(texts["INTRODUCTION_TUTORIAL"].toString())
            idleTimeSeconds = 0
        }
    }

    override fun destroyState() {
        isFocusable = false
    }

    override fun respondTouchState(event: MotionEvent) {
        when (GestureManager.doubleTapDetect(event)) {
            GestureType.DOUBLE_TAP -> {
                Settings.globalSounds.playSound(Resources.acceptSound)
                LevelManager.changeLevel(MenuLevel())
            }
        }
    }

    override fun redrawState(canvas: Canvas) {
        canvas.save()
        canvas.translate(posX.toFloat(), posY.toFloat())
        textLayout.draw(canvas);

        canvas.restore()
    }

}