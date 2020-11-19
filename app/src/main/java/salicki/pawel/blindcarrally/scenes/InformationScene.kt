package salicki.pawel.blindcarrally.scenes

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.view.MotionEvent
import android.view.SurfaceView
import androidx.core.content.res.ResourcesCompat
import salicki.pawel.blindcarrally.*
import salicki.pawel.blindcarrally.enums.GestureTypeEnum
import salicki.pawel.blindcarrally.gameresources.TextToSpeechManager
import salicki.pawel.blindcarrally.information.Settings
import salicki.pawel.blindcarrally.resources.RawResources
import salicki.pawel.blindcarrally.scenemanager.ILevel
import salicki.pawel.blindcarrally.scenemanager.LevelManager
import salicki.pawel.blindcarrally.utils.GestureManager
import salicki.pawel.blindcarrally.utils.OpenerCSV

class InformationScene : SurfaceView(Settings.CONTEXT), ILevel {

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

        infoTextPaint.color = Color.GRAY
        infoTextPaint.textAlign = Paint.Align.CENTER

        val customTypeface = Settings.CONTEXT?.let { ResourcesCompat.getFont(it, R.font.montserrat) }

        infoTextPaint.typeface = customTypeface
        infoTextPaint.isAntiAlias = true
        infoTextPaint.isFilterBitmap = true

        posX = Settings.SCREEN_WIDTH / 2F
        posY = (Settings.SCREEN_HEIGHT / 8F - (infoTextPaint.descent() + infoTextPaint.ascent()) / 2)

        infoTextPaint.textSize = Settings.CONTEXT?.resources?.getDimensionPixelSize(R.dimen.informationSize)!!.toFloat()

        textLayout =  StaticLayout(texts["INTRODUCTION_TUTORIAL"].toString(),
            infoTextPaint, (Settings.SCREEN_WIDTH * 0.8F).toInt(), Layout.Alignment.ALIGN_NORMAL,
            1.0f, 0.0f, false);
    }

    private fun readTTSTextFile() {
        texts.putAll(OpenerCSV.readData(R.raw.introduction_tts, Settings.languageTtsEnum))
    }

    override fun initState() {
        TextToSpeechManager.speakNow(texts["INTRODUCTION_TUTORIAL"].toString())
    }

    override fun updateState() {
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
            GestureTypeEnum.DOUBLE_TAP -> {
                Settings.globalSounds.playSound(RawResources.acceptSound)
                LevelManager.changeLevel(MenuScene())
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