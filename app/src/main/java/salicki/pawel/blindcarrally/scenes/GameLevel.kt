package salicki.pawel.blindcarrally.scenes

import android.R
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.view.MotionEvent
import android.view.SurfaceView
import androidx.core.content.ContextCompat
import salicki.pawel.blindcarrally.MovementManager
import salicki.pawel.blindcarrally.Settings
import salicki.pawel.blindcarrally.TextToSpeechManager
import salicki.pawel.blindcarrally.scenemanager.ILevel

class GameLevel() : SurfaceView(Settings.CONTEXT), ILevel {

    private val movementManager: MovementManager = MovementManager(context)

    init {
        movementManager.register()

        isFocusable = true


    }

    private fun drawCoordinates(canvas: Canvas){
        if(this.movementManager.getOrientation() != null && this.movementManager.getStartOrientation() != null){
            val test1 = movementManager.getOrientation()!![0].toDouble().toString()
            val test2 = movementManager.getOrientation()!![1].toDouble().toString()
            val test3 = movementManager.getOrientation()!![2].toDouble().toString()
            val paint = Paint()
            val color = ContextCompat.getColor(context, R.color.darker_gray)
            paint.setColor(color)
            paint.setTextSize(50F)
            canvas.drawText("orientation 1: $test1", 100F, 300F, paint)
            canvas.drawText("orientation 2: $test2", 100F, 400F, paint)
            canvas.drawText("orientation 3: $test3", 100F, 500F, paint)
        }
    }

    override fun initState() {

    }


    override fun updateState() {

    }

    override fun destroyState() {

    }

    override fun respondTouchState(motionEvent: MotionEvent) {

    }

    override fun redrawState(canvas: Canvas) {
        drawCoordinates(canvas)
    }
}