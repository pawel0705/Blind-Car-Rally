package salicki.pawel.blindcarrally.scenes

import android.R
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Build
import android.view.MotionEvent
import android.view.SurfaceView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import salicki.pawel.blindcarrally.*
import salicki.pawel.blindcarrally.scenemanager.ILevel

@RequiresApi(Build.VERSION_CODES.M)
class GameLevel() : SurfaceView(Settings.CONTEXT), ILevel {

    private val movementManager: MovementManager = MovementManager(context)

    private lateinit var car : Car
    private var coordinateDisplayManager: CoordinateDisplayManager

    init {
        movementManager.register()

        car = Car(500F, 500F, 30F)
        coordinateDisplayManager = CoordinateDisplayManager(car)

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
        XmlReadTest.read()
    }


    override fun updateState() {
        car.update()
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

        var tmp = XmlReadTest.read()

        for(test in tmp.left){
            canvas.drawLine(test.xStart *2, test.yStart *2, test.xEnd *2, test.yEnd *2, paint);
        }

        for(test in tmp.right){
            canvas.drawLine(test.xStart *2, test.yStart *2, test.xEnd *2, test.yEnd *2, paint);
        }

        /*
        var startX = 10F;
        var startY = 0F;
        var stopX = 10F;
        var stopY = 15F;

        canvas.drawLine(startX *2, startY*2, stopX*2, stopY*2, paint);

        var startX2 = 10F;
        var startY2 = 15F;
        var stopX2 = 20F;
        var stopY2 = 30F;

        canvas.drawLine(startX2*2, startY2*2, stopX2*2, stopY2*2, paint);

        var startX3 = 20F;
        var startY3 = 30F;
        var stopX3= 30F;
        var stopY3 = 50F;

        canvas.drawLine(startX3*2, startY3*2, stopX3*2, stopY3*2, paint);

        var startX4 = 30F;
        var startY4 = 50F;
        var stopX4= 40F;
        var stopY4 = 80F;

        canvas.drawLine(startX4 * 2, startY4 * 2, stopX4*2, stopY4*2, paint);
*/
    }
}