package salicki.pawel.blindcarrally.scene

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.Log
import android.view.MotionEvent
import android.view.SurfaceView
import androidx.core.content.ContextCompat
import salicki.pawel.blindcarrally.*
import salicki.pawel.blindcarrally.data.TrackRaceData
import salicki.pawel.blindcarrally.scenemanager.ILevel


class GameLevel() : SurfaceView(Settings.CONTEXT), ILevel {

    private var car: Car = Car(
        Settings.SCREEN_WIDTH / 2F,
        Settings.SCREEN_HEIGHT / 2F,
        RectF(
            Settings.SCREEN_WIDTH / 2F,
            Settings.SCREEN_HEIGHT / 2F,
            Settings.SCREEN_WIDTH / 2F + 0.2F * Settings.SCREEN_SCALE,
            Settings.SCREEN_HEIGHT / 2F + 0.4F * Settings.SCREEN_SCALE
        )
    )
    private var coordinateDisplayManager: CoordinateDisplayManager

    private var trackData : TrackRaceData? = null

    init {

        //   Log.d("SKALA", scale.toString())

        coordinateDisplayManager = CoordinateDisplayManager(car)

        isFocusable = true
    }

    private fun drawCoordinates(canvas: Canvas) {
        if (MovementManager.getOrientation() != null && MovementManager.getStartOrientation() != null) {
            val test1 = MovementManager.getOrientation()!![0].toDouble().toString()
            val test2 = MovementManager.getOrientation()!![1].toDouble().toString()
            val test3 = MovementManager.getOrientation()!![2].toDouble().toString()
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
        trackData = XMLParser.read("trackTest.xml")
        car.posX = trackData!!.startPointX.toFloat()
        car.posY = trackData!!.startPointY.toFloat()
    }

    private fun checkCollistion(){
        if (trackData != null) {
            for (test1 in trackData!!.trackRoad!!) {
                for (test in test1.left) {
                    if (car.collisionCheck(test.xStart, test.yStart, test.xEnd, test.yEnd)) {

                        return
                    }

                }
                for (test in test1.right) {
                    if (car.collisionCheck(
                            test.xStart,
                            test.yStart,
                            test.xEnd,
                            test.yEnd
                        )
                    ) {

                        return
                    }
                }
            }
        }
    }

    private fun checkDistance(deltaTime: Int){
        if (trackData != null) {
            for (test1 in trackData!!.trackRoad!!) {
                for (test in test1.left) {
                    if (car.sensorCheck(test.xStart, test.yStart, test.xEnd, test.yEnd, deltaTime)) {


                    }

                }
                for (test in test1.right) {
                    if (car.sensorCheck(test.xStart, test.yStart, test.xEnd, test.yEnd, deltaTime)) {


                    }
                }
            }
        }
    }

    override fun updateState(deltaTime: Int) {
        car.update(coordinateDisplayManager)
        coordinateDisplayManager.updateEnvironmentCoordinates()

        checkCollistion()
        checkDistance(deltaTime)

    }

    override fun destroyState() {

    }

    override fun respondTouchState(motionEvent: MotionEvent) {
        when(GestureManager.tapPositionDetect(motionEvent)){
            GestureType.TAP_RIGHT->car.higherGear()
            GestureType.TAP_LEFT->car.lowerGear()
        }
    }

    override fun redrawState(canvas: Canvas) {
        canvas.translate(0F, canvas.height.toFloat());   // reset where 0,0 is located
        canvas.scale(1F,-1F);    // invert

        drawCoordinates(canvas)
        car.draw(canvas, coordinateDisplayManager)

        var paint = Paint()
        paint.strokeWidth = Settings.SCREEN_SCALE * 0.02F
        paint.color = Color.WHITE


        /*
        canvas.drawLine(
            coordinateDisplayManager.convertToEnvironmentX(300F),
            coordinateDisplayManager.convertToEnvironmentY(300F),
            coordinateDisplayManager.convertToEnvironmentX(600F),
            coordinateDisplayManager.convertToEnvironmentY(600F), paint
        )



        canvas.drawLine(300F, 300F, 600F, 600F, paint)
        car.collisionCheck(300F, 300F, 600F, 600F)
*/

        if (trackData != null) {
            for (test1 in trackData!!.trackRoad!!) {
                for (test in test1.left) {
                    canvas.drawLine(
                        coordinateDisplayManager.convertToEnvironmentX(test.xStart),
                        coordinateDisplayManager.convertToEnvironmentY(test.yStart),
                        coordinateDisplayManager.convertToEnvironmentX(test.xEnd),
                        coordinateDisplayManager.convertToEnvironmentY(test.yEnd),
                        paint
                    );
                }
                for (test in test1.right) {
                    canvas.drawLine(
                        coordinateDisplayManager.convertToEnvironmentX(test.xStart),
                        coordinateDisplayManager.convertToEnvironmentY(test.yStart),
                        coordinateDisplayManager.convertToEnvironmentX(test.xEnd),
                        coordinateDisplayManager.convertToEnvironmentY(test.yEnd),
                        paint
                    );
                }
            }
        }

    }
}