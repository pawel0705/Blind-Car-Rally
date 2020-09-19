package salicki.pawel.blindcarrally.scenemanager

import android.graphics.Canvas
import android.graphics.Paint
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import androidx.core.content.ContextCompat
import salicki.pawel.blindcarrally.GameLoop
import salicki.pawel.blindcarrally.R
import salicki.pawel.blindcarrally.Settings
import salicki.pawel.blindcarrally.scenes.*


object LevelManager : SurfaceView(Settings.CONTEXT), SurfaceHolder.Callback {

    private val scenes : HashMap<LevelType, ILevel> = HashMap()
    private var activeLevelType : LevelType = LevelType.SPLASH

    private var gameLoop: GameLoop

    init {
        scenes[LevelType.SPLASH] = SplashLevel()
        scenes[LevelType.LANGUAGE] = LanguageLevel()
        scenes[LevelType.MENU] = MenuLevel()
        scenes[LevelType.QUIT] = QuitLevel()
        scenes[LevelType.GAME] = GameLevel()
        scenes[LevelType.SETTINGS] = SettingsLevel()
        scenes[LevelType.CREDITS] = CreditsLevel()

        val surfaceHolder = holder
        surfaceHolder.addCallback(this)

        gameLoop = GameLoop( surfaceHolder)
    }

    fun changeLevel(level: LevelType){
        this.activeLevelType = level
        this.scenes[activeLevelType]?.initState()

    }


    fun updateState() {
        this.scenes[activeLevelType]?.updateState()
    }

    fun destroyState() {
        this.scenes[activeLevelType]?.destroyState()
    }

    private fun initState(){
        this.scenes[activeLevelType]?.initState()
    }

    private fun respondTouchState(motionEvent: MotionEvent) {
        this.scenes[activeLevelType]?.respondTouchState(motionEvent)
    }

    fun redrawState(canvas: Canvas) {
        super.draw(canvas)

        this.drawUPS(canvas)
        this.drawFPS(canvas)

        this.scenes[activeLevelType]?.redrawState(canvas)
    }

    private fun drawUPS(canvas: Canvas) {
        val averageUPS = gameLoop.averageUPS.toString()
        val paint = Paint()
        val color = ContextCompat.getColor(context, R.color.colorPrimary)
        paint.color = color
        paint.textSize = 50F
        canvas.drawText("UPS: $averageUPS", 100F, 100F, paint)
    }

    private fun drawFPS(canvas: Canvas) {
        val averageFPS = gameLoop.averageFPS.toString()
        val paint = Paint()
        val color = ContextCompat.getColor(context, R.color.colorPrimary)
        paint.color = color
        paint.textSize = resources.getDimensionPixelSize(R.dimen.fontSize).toFloat();
        canvas.drawText("FPS: $averageFPS", 100F, 200F, paint)
    }

    fun pause(){
        gameLoop.stopLoop()
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        if (gameLoop.state == Thread.State.TERMINATED) {
            gameLoop = GameLoop(holder)
        }
        gameLoop.startLoop()
    }

    override fun surfaceChanged(p0: SurfaceHolder, p1: Int, p2: Int, p3: Int) {}

    override fun surfaceDestroyed(p0: SurfaceHolder) {}

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event != null) {
            this.respondTouchState(event)
        }

        return true
    }
}