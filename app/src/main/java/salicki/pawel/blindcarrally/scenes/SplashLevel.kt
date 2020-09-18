package salicki.pawel.blindcarrally.scenes

import android.graphics.*
import android.view.MotionEvent
import android.view.SurfaceView
import android.widget.Toast
import salicki.pawel.blindcarrally.*
import salicki.pawel.blindcarrally.scenemanager.ILevel
import salicki.pawel.blindcarrally.scenemanager.LevelManager
import salicki.pawel.blindcarrally.scenemanager.LevelType
import java.util.*
import kotlin.collections.HashMap


class SplashLevel() : SurfaceView(Settings.CONTEXT), ILevel {

    private var texts : HashMap<String, String> = HashMap()
    private lateinit var logoRectangle : Rect
    private var logoImage : Bitmap

    // double tap
    private var clickCount = 0
    private var startTime: Long = 0
    private var duration: Long = 0
    private val MAX_DURATION = 200

    init {
        isFocusable = true

        texts.putAll(OpenerCSV.readData(R.raw.splash_tts, LanguageTTS.ENGLISH))
        logoImage = BitmapFactory.decodeResource(context.resources, R.drawable.test)

        TextToSpeechManager.setLanguage(LanguageTTS.ENGLISH)

        Timer().schedule(object : TimerTask() {
            override fun run() {
                LevelManager.changeLevel(LevelType.LANGUAGE)
            }
        }, 5000)


        TextToSpeechManager.speakQueue(texts["SPLASH_LOGO"].toString())
    }

    override fun initState() {

    }

    override fun updateState() {
    }

    override fun destroyState() {
        isFocusable = false
    }

    override fun respondTouchState(event: MotionEvent) {
        when (event.action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_DOWN -> {
                startTime = System.currentTimeMillis()
                clickCount++
            }
            MotionEvent.ACTION_UP -> {
                val time: Long = System.currentTimeMillis() - startTime
                duration += time
                if (clickCount === 2) {
                    if (duration <= MAX_DURATION) {
                        LevelManager.changeLevel(LevelType.LANGUAGE)
                    }
                    clickCount = 0
                    duration = 0
                }
            }
        }
    }

    override fun redrawState(canvas: Canvas) {
        this.drawSplashScreen(canvas)
    }

    private fun drawSplashScreen(canvas: Canvas) {

        logoRectangle = Rect()
        logoRectangle.set(0, 0, Settings.SCREEN_WIDTH, Settings.SCREEN_HEIGHT)

        val whRatio: Float =
            logoImage.width.toFloat()  / logoImage.height
        if (logoRectangle.width() > logoRectangle.height())
        {
            logoRectangle.left =
                logoRectangle.right - ((logoRectangle.height() * whRatio)).toInt()
        }
        else{
            logoRectangle.top =
                logoRectangle.bottom - ((logoRectangle.width() * (1 / whRatio))).toInt()
        }

        canvas.drawBitmap(logoImage, null, logoRectangle, Paint())
    }
}