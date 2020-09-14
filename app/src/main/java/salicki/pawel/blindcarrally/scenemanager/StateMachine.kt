package salicki.pawel.blindcarrally.scenemanager

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import androidx.core.content.ContextCompat
import salicki.pawel.blindcarrally.GameScene
import salicki.pawel.blindcarrally.GameLoop
import salicki.pawel.blindcarrally.R

class StateMachine(context: Context) : SurfaceView(context), SurfaceHolder.Callback, IState {

    private val scenes : HashMap<Scene, IState> = HashMap()
    private var activeScene : Scene = Scene.GAME

    private val gameLoop: GameLoop

    init {
        scenes[Scene.GAME] = GameScene(context)

        val surfaceHolder = holder
        surfaceHolder.addCallback(this)

        gameLoop = GameLoop(this, surfaceHolder)
    }

    override fun updateState() {
        this.scenes[activeScene]?.updateState()
    }

    override fun destroyState() {
        this.scenes[activeScene]?.destroyState()
    }

    override fun respondTouchState(motionEvent: MotionEvent) {
        this.scenes[activeScene]?.respondTouchState(motionEvent)
    }

    override fun redrawState(canvas: Canvas) {
        super.draw(canvas)

        this.drawUPS(canvas)
        this.drawFPS(canvas)

        this.scenes[activeScene]?.redrawState(canvas)
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
        paint.textSize = 50F
        canvas.drawText("FPS: $averageFPS", 100F, 200F, paint)
    }

    override fun surfaceCreated(p0: SurfaceHolder) {
        gameLoop.startLoop()
    }

    override fun surfaceChanged(p0: SurfaceHolder, p1: Int, p2: Int, p3: Int) {}

    override fun surfaceDestroyed(p0: SurfaceHolder) {}
}