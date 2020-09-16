package salicki.pawel.blindcarrally.scenes

import android.content.Context
import android.graphics.*
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.MotionEvent
import android.view.SurfaceView
import salicki.pawel.blindcarrally.*
import salicki.pawel.blindcarrally.scenemanager.ILevel
import salicki.pawel.blindcarrally.scenemanager.LevelManager
import salicki.pawel.blindcarrally.scenemanager.LevelType
import java.util.*
import kotlin.collections.HashMap

class SplashLevel(context: Context, levelManager: LevelManager) : SurfaceView(context), ILevel {

    private var texts : HashMap<String, String> = HashMap()
    private var logoRectangle : Rect
    private var logoImage : Bitmap
    private val levelManager : LevelManager = levelManager

    private var test = 0;
    private var initTextLogo: Boolean = false

    init {
        isFocusable = true

        texts.putAll(OpenerCSV.readData(R.raw.splash_tts, LanguageTTS.ENGLISH))

        logoImage = BitmapFactory.decodeResource(context.resources, R.drawable.test)

        logoRectangle = Rect()
        logoRectangle.set( 0, 0, Settings.SCREEN_WIDTH, Settings.SCREEN_HEIGHT)

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

        TextToSpeechManager.setLanguage(LanguageTTS.ENGLISH)

        Timer().schedule(object : TimerTask() {
            override fun run() {
                levelManager.changeLevel(LevelType.GAME)
            }
        }, 5000)
    }

    override fun updateState() {

        if(!initTextLogo){
            TextToSpeechManager.speak(texts["SPLASH_LOGO"].toString())

            this.initTextLogo = true
        }

    }


    override fun destroyState() {
        isFocusable = false
    }

    override fun respondTouchState(event: MotionEvent) {

        Log.d("DSDS", "JESTEM")

        if (event != null) {
            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    TextToSpeechManager.speak(texts["SPLASH_LOGO"].toString())
                }
                MotionEvent.ACTION_MOVE -> {
                    Log.d("TE", "Action was MOVE")



                }
                MotionEvent.ACTION_UP -> {
                    Log.d("TE", "Action was UP")



                }
                MotionEvent.ACTION_CANCEL -> {
                    Log.d("TE", "Action was CANCEL")



                }
                MotionEvent.ACTION_OUTSIDE -> {
                    Log.d("TE", "Movement occurred outside bounds of current screen element")



                }
            }
        }

    }

    override fun redrawState(canvas: Canvas) {
        this.drawSplashScreen(canvas)
    }

    private fun drawSplashScreen(canvas: Canvas) {

        canvas.drawBitmap(logoImage, null, logoRectangle, Paint())
    }


}