package salicki.pawel.blindcarrally.scene

import android.graphics.*
import android.view.MotionEvent
import android.view.SurfaceView
import androidx.core.content.ContextCompat
import salicki.pawel.blindcarrally.*
import salicki.pawel.blindcarrally.data.LeftRightDate
import salicki.pawel.blindcarrally.scenemanager.ILevel

class GameLevel() : SurfaceView(Settings.CONTEXT), ILevel {

    private val movementManager: MovementManager = MovementManager(context)

    private var car: Car
    private var coordinateDisplayManager: CoordinateDisplayManager

    private var tmp: MutableList<LeftRightDate>? = null

    init {
        movementManager.register()

        car = Car(500F, 500F, RectF(500F, 500F, 600F, 700F))
        coordinateDisplayManager = CoordinateDisplayManager(car)

        isFocusable = true
    }

    private fun drawCoordinates(canvas: Canvas) {
        if (this.movementManager.getOrientation() != null && this.movementManager.getStartOrientation() != null) {
            val test1 = movementManager.getOrientation()!![0].toDouble().toString()
            val test2 = movementManager.getOrientation()!![1].toDouble().toString()
            val test3 = movementManager.getOrientation()!![2].toDouble().toString()
            val paint = Paint()
            val color = ContextCompat.getColor(context, R.color.colorPrimary)
            paint.color = color
            paint.textSize = 50F
            canvas.drawText("orientation 1: $test1", 100F, 300F, paint)
            canvas.drawText("orientation 2: $test2", 100F, 400F, paint)
            canvas.drawText("orientation 3: $test3", 100F, 500F, paint)
        }
    }

    override fun initState() {
        tmp = XMLParser.read("trackTest.xml")
    }

    override fun updateState() {
        car.update()

        coordinateDisplayManager.updateEnvironmentCoordinates()
    }

    override fun destroyState() {

    }

    override fun respondTouchState(motionEvent: MotionEvent) {

    }

    override fun redrawState(canvas: Canvas) {
        drawCoordinates(canvas)
        car.draw(canvas, coordinateDisplayManager)

        var paint = Paint()
        paint.strokeWidth = 10F
        paint.color = Color.WHITE

        if(tmp != null){
            for (test1 in tmp!!) {
                for (test in test1.left) {
                    canvas.drawLine(
                        coordinateDisplayManager.convertToEnvironmentX(test.xStart),
                        coordinateDisplayManager.convertToEnvironmentY(test.yStart),
                        coordinateDisplayManager.convertToEnvironmentX(test.xEnd),
                        coordinateDisplayManager.convertToEnvironmentY(test.yEnd),
                        paint
                    );

                    car.collisionCheck(test.xStart, test.yStart, test.xEnd, test.yEnd)
                }
                for (test in test1.right) {
                    canvas.drawLine(
                        coordinateDisplayManager.convertToEnvironmentX(test.xStart),
                        coordinateDisplayManager.convertToEnvironmentY(test.yStart),
                        coordinateDisplayManager.convertToEnvironmentX(test.xEnd),
                        coordinateDisplayManager.convertToEnvironmentY(test.yEnd),
                        paint
                    );

                    car.collisionCheck(test.xStart, test.yStart, test.xEnd, test.yEnd)
                }
            }
        }

    }
}