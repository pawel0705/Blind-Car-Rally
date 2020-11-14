package salicki.pawel.blindcarrally.scenemanager

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.os.CountDownTimer
import android.util.Log
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
import kotlin.collections.ArrayDeque
import kotlin.collections.HashMap


object LevelManager : SurfaceView(Settings.CONTEXT), SurfaceHolder.Callback {

    private var activeScene: ArrayDeque<ILevel> = ArrayDeque()
    private var touchTimer = Timer()
    private var sceneTransitionTimer = Timer()
    private var activateTouch: Boolean = false
    private var gameLoop: GameLoop
    private var canvasPaint = Paint()


    init {
        activeScene.add(SplashLevel())
        initState()

        val surfaceHolder = holder
        surfaceHolder.addCallback(this)

        gameLoop = GameLoop(surfaceHolder)
        canvasPaint.color = Color.BLACK

        startTouchTimer()
    }

    fun stackLevel(level: ILevel) {
        this.resetTouchTimer()
        this.startTouchTimer()

        activeScene.add(level)
        initState()
    }

    fun popLevel() {
        this.resetTouchTimer()
        this.startTouchTimer()

        if (!activeScene.isEmpty()) {
            activeScene.removeLast()
        }

    }

    fun changeLevel(level: ILevel) {

        if (!activeScene.isEmpty()) {
            activeScene.last()?.destroyState()
        }

        activeScene.removeLast()

        this.resetTouchTimer()
        this.startTouchTimer()

        activeScene.add(level)

        Log.d("STOS", activeScene.size.toString())

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
        if (!activeScene.isEmpty()) {
            activeScene?.last().updateState(deltaTime)
        }
    }

    fun destroyState() {
        if (!activeScene.isEmpty()) {
            activeScene?.last().destroyState()
        }
    }

    private fun initState() {
        if (!activeScene.isEmpty()) {
            activeScene?.last().initState()
        }
    }

    private fun respondTouchState(motionEvent: MotionEvent) {
        if (activateTouch && !activeScene.isEmpty()) {
            activeScene?.last().respondTouchState(motionEvent)
        }
    }

    fun redrawState(canvas: Canvas) {
        super.draw(canvas)

        canvas.drawRect(
            0F,
            0F,
            Settings.SCREEN_WIDTH.toFloat() + 1F,
            Settings.SCREEN_HEIGHT.toFloat() + 1F,
            canvasPaint
        )

        if(Settings.display){
            this.drawUPS(canvas)
            this.drawFPS(canvas)

            if (!activeScene.isEmpty()) {
                activeScene?.last().redrawState(canvas)
            }
        }
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