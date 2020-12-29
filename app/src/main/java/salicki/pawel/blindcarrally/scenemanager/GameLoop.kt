package salicki.pawel.blindcarrally.scenemanager

import android.graphics.Canvas
import android.view.SurfaceHolder
import salicki.pawel.blindcarrally.information.Settings
import java.lang.Exception

class GameLoop(surfaceHolder: SurfaceHolder) : Thread() {
    private val surfaceHolder: SurfaceHolder = surfaceHolder
    private var isRunning = false

    fun startLoop() {
        isRunning = true
        start()
    }

    override fun run() {
        super.run()

        var frameCount = 0
        var startTime: Long = 0
        var elapsedTime: Int = 0
        var canvas: Canvas? = null
        var timeMilis: Long = 0

        while (isRunning) {
            startTime = System.nanoTime()
            canvas = null

            try {
                canvas = this.surfaceHolder.lockCanvas()
                synchronized(surfaceHolder) {
                    LevelManager.updateState()
                    LevelManager.redrawState(canvas)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                if (canvas != null) {
                    try {
                        surfaceHolder.unlockCanvasAndPost(canvas)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
            timeMilis = (System.currentTimeMillis() - startTime)

            try {
                if (timeMilis > 0) {
                    sleep(timeMilis)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            elapsedTime += (System.nanoTime() - startTime).toInt()
            frameCount++

            if (frameCount >= Settings.FPS) {
                frameCount = 0
                elapsedTime = 0
            }
        }
    }

    fun stopLoop() {
        isRunning = false
        try {
            join()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }
}