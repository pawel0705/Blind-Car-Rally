package salicki.pawel.blindcarrally.scenemanager

import android.graphics.Canvas
import android.graphics.Paint
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

    init {
        activeScene.add(SplashLevel())
        initState()

        val surfaceHolder = holder
        surfaceHolder.addCallback(this)

        gameLoop = GameLoop(surfaceHolder)

        startTouchTimer()
    }

    fun stackLevel(level: ILevel){
        this.resetTouchTimer()
        this.startTouchTimer()

        activeScene.add(level)
        initState()
    }

    fun popLevel(){
        this.resetTouchTimer()
        this.startTouchTimer()

        activeScene.removeLast()
    }

    fun changeLevel(level: ILevel) {

        activeScene.last()?.destroyState()
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
        activeScene?.last().updateState(deltaTime)
    }

    fun destroyState() {
        activeScene?.last().destroyState()
    }

    private fun initState() {
        activeScene?.last().initState()
    }

    private fun respondTouchState(motionEvent: MotionEvent) {
        if (activateTouch) {
            activeScene?.last().respondTouchState(motionEvent)
        }
    }

    fun redrawState(canvas: Canvas) {
        super.draw(canvas)

        this.drawUPS(canvas)
        this.drawFPS(canvas)

        activeScene?.last().redrawState(canvas)
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