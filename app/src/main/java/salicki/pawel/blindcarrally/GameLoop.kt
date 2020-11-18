package salicki.pawel.blindcarrally

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.PorterDuff
import android.util.Log
import android.view.SurfaceHolder
import salicki.pawel.blindcarrally.scenemanager.LevelManager
import java.lang.Exception
import java.util.logging.Level
import kotlin.system.measureTimeMillis

class GameLoop(surfaceHolder: SurfaceHolder) : Thread() {
    private val surfaceHolder: SurfaceHolder = surfaceHolder
    private var isRunning = false

    var averageUPS = 0.0
        private set

    var averageFPS = 0.0
        private set

    fun startLoop() {
        isRunning = true
        start()
    }

    override fun run() {
        super.run()




        // Declare time and cycle count variables
        var updateCount = 0
        var frameCount = 0
        var startTime: Long = 0
        var elapsedTime: Int = 0
        var sleepTime: Int = 0
        var canvas: Canvas? = null
        var timeMilis: Long = 0

        while (isRunning) {
            startTime = System.nanoTime()
            canvas = null

            try {
                canvas = this.surfaceHolder.lockCanvas()
                synchronized(surfaceHolder) {
                    LevelManager.updateState(0)
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
            sleepTime = (updateCount * UPS_PERIOD - elapsedTime).toLong().toInt()

            try{
                if(timeMilis > 0){
                    sleep(timeMilis.toLong())
                }
            } catch(e: Exception){
                e.printStackTrace()
            }
            elapsedTime += (System.nanoTime() - startTime).toInt()
            frameCount++

            if(frameCount >= 30){
                frameCount = 0
                elapsedTime = 0
            }


        }

        /*
        // Game loop
        var canvas: Canvas? = null
        startTime = System.currentTimeMillis().toInt()
        while (isRunning) {

            // Try to update and render game
            try {
                canvas = surfaceHolder.lockCanvas()
                synchronized(surfaceHolder) {
                    LevelManager.updateState(elapsedTime)
                    updateCount++

                    if (canvas != null) {
                        if (Settings.display) {
                            LevelManager.redrawState(canvas)
                        }
                    }
                }
            } catch (e: IllegalArgumentException) {
                e.printStackTrace()
            } finally {
                if (canvas != null) {
                    try {
                        surfaceHolder.unlockCanvasAndPost(canvas)
                        frameCount++
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }

            // Pause game loop to not exceed target UPS
            elapsedTime = (System.currentTimeMillis() - startTime).toInt()
            sleepTime = (updateCount * UPS_PERIOD - elapsedTime).toLong().toInt()
            if (sleepTime > 0) {
                try {
                    sleep(sleepTime.toLong())
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }

            // Skip frames to keep up with target UPS
            while (sleepTime < 0 && updateCount < MAX_UPS - 1) {
                LevelManager.updateState(elapsedTime)
                updateCount++
                elapsedTime = (System.currentTimeMillis() - startTime).toInt()
                sleepTime = (updateCount * UPS_PERIOD - elapsedTime).toLong().toInt()
            }

            // Calculate average UPS and FPS
            elapsedTime = (System.currentTimeMillis() - startTime).toInt()
            if (elapsedTime >= 1000) {
                averageUPS = updateCount / (1E-3 * elapsedTime)
                averageFPS = frameCount / (1E-3 * elapsedTime)
                updateCount = 0
                frameCount = 0
                startTime = System.currentTimeMillis().toInt()
            }

        //    Log.d("TIME", elapsedTime.toString())
        }
        */

    }

    fun stopLoop() {
        isRunning = false
        try {
            join()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    companion object {
        const val MAX_UPS = 30.0
        private const val UPS_PERIOD = 1E+3 / MAX_UPS
    }

}