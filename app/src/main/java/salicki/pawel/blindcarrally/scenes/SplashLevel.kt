package salicki.pawel.blindcarrally.scenes

import android.graphics.*
import android.view.MotionEvent
import android.view.SurfaceView
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

    private var logoTimer = Timer()

    // double tap
    private var clickCount = 0
    private var startTime: Long = 0
    private var duration: Long = 0
    private val MAX_DURATION = 200

    init {
        isFocusable = true

        texts.putAll(OpenerCSV.readData(R.raw.splash_tts, LanguageTTS.ENGLISH))
        logoImage = BitmapFactory.decodeResource(context.resources, R.drawable.logo)

       // TextToSpeechManager.setLanguage(LanguageTTS.ENGLISH)

        logoTimer.schedule(object : TimerTask() {
            override fun run() {
                LevelManager.changeLevel(LevelType.LANGUAGE)
            }
        }, 15000)
    }

    override fun initState() {
        TextToSpeechManager.speakQueue(texts["SPLASH_LOGO"].toString())
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
                        logoTimer.cancel()
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

         var point = Point(Settings.SCREEN_WIDTH / 2, Settings.SCREEN_HEIGHT / 2)

        logoRectangle.set(0 ,0, resources.getDimensionPixelSize(R.dimen.screenSize), resources.getDimensionPixelSize(R.dimen.screenSize))

        logoRectangle.set(
            point.x - logoRectangle.width() / 2,
            point.y - logoRectangle.height() / 2,
            point.x + logoRectangle.width() / 2,
            point.y + logoRectangle.height() / 2
        )

        //logoRectangle.set(0, 0, Settings.SCREEN_WIDTH, Settings.SCREEN_HEIGHT / 2)

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