package salicki.pawel.blindcarrally.scenemanager

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import salicki.pawel.blindcarrally.information.Settings
import salicki.pawel.blindcarrally.scenes.*
import java.util.*
import kotlin.collections.ArrayDeque


object LevelManager : SurfaceView(Settings.CONTEXT), SurfaceHolder.Callback {

    private var activeScene: ArrayDeque<ILevel> = ArrayDeque()
    private var touchTimer = Timer()
    private var canvasPaint = Paint()
    private var gameLoop: GameLoop

    private var activateTouch: Boolean = false

    init {
        activeScene.add(SplashScene())
        initState()

        val surfaceHolder = holder
        surfaceHolder.addCallback(this)

        gameLoop =
            GameLoop(surfaceHolder)
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
        while (!activeScene.isEmpty()) {
            activeScene.last()?.destroyState()
            activeScene.removeLast()
        }

        this.resetTouchTimer()
        this.startTouchTimer()

        activeScene.add(level)

        initState()
    }

    fun updateState() {
        if (!activeScene.isEmpty()) {
            activeScene?.last().updateState()
        }
    }

    fun destroyState() {
        if (!activeScene.isEmpty()) {
            activeScene?.last().destroyState()
        }
    }

    fun redrawState(canvas: Canvas) {
        super.draw(canvas)
        clearCanvas(canvas)

        if (Settings.display) {
            if (!activeScene.isEmpty()) {
                activeScene?.last().redrawState(canvas)
            }
        }
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

    private fun startTouchTimer() {
        touchTimer.schedule(object : TimerTask() {
            override fun run() {
                activateTouch = true
            }
        }, 1000)
    }

    private fun clearCanvas(canvas: Canvas){
        canvas.drawRect(
            0F,
            0F,
            Settings.SCREEN_WIDTH.toFloat() + 1F,
            Settings.SCREEN_HEIGHT.toFloat() + 200F,
            canvasPaint
        )
    }

    private fun resetTouchTimer() {
        activateTouch = false
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
}