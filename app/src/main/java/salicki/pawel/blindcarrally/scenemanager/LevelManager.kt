package salicki.pawel.blindcarrally.scenemanager

import android.graphics.Canvas
import android.graphics.Paint
import android.os.CountDownTimer
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import androidx.core.content.ContextCompat
import salicki.pawel.blindcarrally.GameLoop
import salicki.pawel.blindcarrally.R
import salicki.pawel.blindcarrally.Settings
import salicki.pawel.blindcarrally.TextToSpeechManager
import salicki.pawel.blindcarrally.scene.*
import java.util.*
import kotlin.collections.HashMap


object LevelManager : SurfaceView(Settings.CONTEXT), SurfaceHolder.Callback {

    private var activeScene: ILevel
    private var touchTimer = Timer()
    private var sceneTransitionTimer = Timer()
    private var activateTouch: Boolean = false
    private var gameLoop: GameLoop

    init {
        activeScene = SplashLevel()
        initState()

        val surfaceHolder = holder
        surfaceHolder.addCallback(this)

        gameLoop = GameLoop(surfaceHolder)

        startTouchTimer()
    }

    fun changeLevel(level: ILevel) {

        activeScene?.destroyState()

        this.resetTouchTimer()
        this.startTouchTimer()

        activeScene = level
        initState()
    }

    private fun startTouchTimer() {
        touchTimer.schedule(object : TimerTask() {
            override fun run() {
                activateTouch = true
            }
        }, 1000)
    }

    private fun resetTouchTimer() {
        activateTouch = false
    }

    fun updateState(deltaTime: Int) {
        activeScene?.updateState(deltaTime)
    }

    fun destroyState() {
        activeScene?.destroyState()
    }

    private fun initState() {
        activeScene?.initState()
    }

    private fun respondTouchState(motionEvent: MotionEvent) {
        if (activateTouch) {
            activeScene?.respondTouchState(motionEvent)
        }
    }


    fun redrawState(canvas: Canvas) {
        super.draw(canvas)

        this.drawUPS(canvas)
        this.drawFPS(canvas)

        activeScene?.redrawState(canvas)
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

    fun pause() {
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