package salicki.pawel.blindcarrally

import android.graphics.*
import android.util.Log
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

class Car(posX: Float, posY: Float, rect: RectF) : EnvironmentObject(posX, posY) {

    private var speed: Float = 0F
    private var angle: Float = 0F
    private val maxSpeed: Float = 1F
    private var turnSpeed: Float = 0.008F
    private val MAX_GEAR = 6
    private val MIN_GEAR = 0
    private var gear = 1

    private var rectangle: RectF = rect
    private var texture: Texture

    private var paint: Paint = Paint()
    private var carCoordinates: CarCoordinates = CarCoordinates()

    init {
        rectangle.set(
            (posX - rectangle.width() / 2),
            (posY - rectangle.height() / 2),
            (posX + rectangle.width() / 2),
            (posY + rectangle.height() / 2)
        )

        carCoordinates.posX1 = posX - rectangle.width() / 2
        carCoordinates.posY1 = posY - rectangle.height() / 2

        carCoordinates.posX2 = posX + rectangle.width() - rectangle.width() / 2
        carCoordinates.posY2 = posY - rectangle.height() / 2

        carCoordinates.posX3 = posX - rectangle.width() / 2
        carCoordinates.posY3 = posY + rectangle.height() - rectangle.height() / 2

        carCoordinates.posX4 = posX + rectangle.width() - rectangle.width() / 2
        carCoordinates.posY4 = posY + rectangle.height() - rectangle.height() / 2

        var bitmap: Bitmap = BitmapFactory.decodeResource(
            Settings.CONTEXT?.resources,
            R.drawable.car
        )
        texture = Texture(bitmap)
    }

    override fun draw(canvas: Canvas?, coordinateDisplayManager: CoordinateDisplayManager?) {

        if (coordinateDisplayManager != null && canvas != null) {
            rectangle.set(
                (coordinateDisplayManager.convertToEnvironmentX(posX) - rectangle.width() / 2),
                (coordinateDisplayManager.convertToEnvironmentY(posY) - rectangle.height() / 2),
                (coordinateDisplayManager.convertToEnvironmentX(posX) + rectangle.width() / 2),
                (coordinateDisplayManager.convertToEnvironmentY(posY) + rectangle.height() / 2)
            )

            canvas.save();
            canvas.rotate(
                angle * 180 / 3.141593F, coordinateDisplayManager.convertToEnvironmentX(posX),
                coordinateDisplayManager.convertToEnvironmentY(posY)
            );

            canvas.drawRect(rectangle, paint)
            texture.drawTexture(canvas, rectangle)
            canvas.restore();

            var paint2 = Paint()
            paint2.color = Color.GREEN

            canvas.drawLine(
                coordinateDisplayManager.convertToEnvironmentX(carCoordinates.posX01),
                coordinateDisplayManager.convertToEnvironmentY(carCoordinates.posY01),
                coordinateDisplayManager.convertToEnvironmentX(carCoordinates.posX02),
                coordinateDisplayManager.convertToEnvironmentY(carCoordinates.posY02),
                paint2
            )
            canvas.drawLine(
                coordinateDisplayManager.convertToEnvironmentX(carCoordinates.posX01),
                coordinateDisplayManager.convertToEnvironmentY(carCoordinates.posY01),
                coordinateDisplayManager.convertToEnvironmentX(carCoordinates.posX03),
                coordinateDisplayManager.convertToEnvironmentY(carCoordinates.posY03),
                paint2
            )
            canvas.drawLine(
                coordinateDisplayManager.convertToEnvironmentX(carCoordinates.posX02),
                coordinateDisplayManager.convertToEnvironmentY(carCoordinates.posY02),
                coordinateDisplayManager.convertToEnvironmentX(carCoordinates.posX04),
                coordinateDisplayManager.convertToEnvironmentY(carCoordinates.posY04),
                paint2
            )
            canvas.drawLine(
                coordinateDisplayManager.convertToEnvironmentX(carCoordinates.posX03),
                coordinateDisplayManager.convertToEnvironmentY(carCoordinates.posY03),
                coordinateDisplayManager.convertToEnvironmentX(carCoordinates.posX04),
                coordinateDisplayManager.convertToEnvironmentY(carCoordinates.posY04),
                paint2
            )






            canvas.drawLine(
                carCoordinates.posX01,
               carCoordinates.posY01,
                carCoordinates.posX02,
                carCoordinates.posY02,
                paint2
            )
            canvas.drawLine(
                carCoordinates.posX01,
               carCoordinates.posY01,
               carCoordinates.posX03,
              carCoordinates.posY03,
                paint2
            )
            canvas.drawLine(
              carCoordinates.posX02,
              carCoordinates.posY02,
               carCoordinates.posX04,
               carCoordinates.posY04,
                paint2
            )
            canvas.drawLine(
                carCoordinates.posX03,
             carCoordinates.posY03,
               carCoordinates.posX04,
              carCoordinates.posY04,
                paint2
            )
        }
    }

    fun collisionCheck(x1: Float, y1: Float, x2: Float, y2: Float): Boolean {
        val left: Boolean = lineCollision(
            x1, y1, x2, y2,
            carCoordinates.posX01, carCoordinates.posY01, carCoordinates.posX03, carCoordinates.posY03
        )
        val right: Boolean = lineCollision(
            x1, y1, x2, y2,
            carCoordinates.posX02, carCoordinates.posY02, carCoordinates.posX04, carCoordinates.posY04
        )
        val top: Boolean = lineCollision(
            x1, y1, x2, y2,
            carCoordinates.posX01, carCoordinates.posY01, carCoordinates.posX02, carCoordinates.posY02
        )
        var bottom: Boolean = lineCollision(
            x1, y1, x2, y2,
            carCoordinates.posX03, carCoordinates.posY03, carCoordinates.posX04, carCoordinates.posY04
        )

   //     Log.d("posX1", carCoordinates.posX01.toString())
    //    Log.d("posY1", carCoordinates.posY01.toString())

        if (left || right || top || bottom) {
            paint.color = Color.RED

            speed = 0F

            if(top){
                posY += 1
                Log.d("KOLIZJA", "TOP")
            }
            else if(bottom){
                posY -= 1
                Log.d("KOLIZJA", "BOTTOM")
            } else {
                if(left){
                    posX += 1
                    Log.d("KOLIZJA", "LEFT")
                }
               else if(right) {
                    posX -=1
                    Log.d("KOLIZJA", "RIGHT")
                }

            }



            return true
        }

        paint.color = Color.YELLOW
        return false
    }

    private fun lineCollision(
        x1: Float,
        y1: Float,
        x2: Float,
        y2: Float,
        x3: Float,
        y3: Float,
        x4: Float,
        y4: Float
    ): Boolean {
        val uA =
            (((x4 - x3) * (y1 - y3) - (y4 - y3) * (x1 - x3)) / ((y4 - y3) * (x2 - x1) - (x4 - x3) * (y2 - y1))).toFloat()
        val uB =
            (((x2 - x1) * (y1 - y3) - (y2 - y1) * (x1 - x3)) / ((y4 - y3) * (x2 - x1) - (x4 - x3) * (y2 - y1))).toFloat()

        if (uA in 0.0..1.0 && uB in 0.0..1.0) {
            return true
        }

        return false
    }

    fun higherGear(){
        if(gear < MAX_GEAR){
            gear++
        }
    }

    fun lowerGear(){
        if(gear > MIN_GEAR){
            gear--
        }
    }

    override fun update() {
        if (MovementManager != null) {
            if (MovementManager.getOrientation() != null && MovementManager.getStartOrientation() != null) {
                val pitch: Float =
                    MovementManager?.getOrientation()!![2] - MovementManager.getStartOrientation()!![2]
                val roll: Float =
                    MovementManager?.getOrientation()!![1] - MovementManager.getStartOrientation()!![1]

                velX = 2 * roll * Settings.SCREEN_WIDTH / 1000f
                velY = -pitch * Settings.SCREEN_HEIGHT / 1000f
            }
        }

        if (speed > 2 * gear) {
            speed = 2F * gear
        } else if (speed < -2) {
            speed = -2F
        }

        speed += velY
        angle += turnSpeed * speed / maxSpeed * velX

        posX += sin(angle) * speed
        posY -= cos(angle) * speed

        carCoordinates.posX1 = posX - rectangle.width() / 2
        carCoordinates.posY1 = posY - rectangle.height() / 2
        carCoordinates.posX01 =
            cos(angle) * (carCoordinates.posX1 - posX) - sin(angle) * (carCoordinates.posY1 - posY) + posX
        carCoordinates.posY01 =
            sin(angle) * (carCoordinates.posX1 - posX) + cos(angle) * (carCoordinates.posY1 - posY) + posY

        carCoordinates.posX2 = posX + rectangle.width() - rectangle.width() / 2
        carCoordinates.posY2 = posY - rectangle.height() / 2
        carCoordinates.posX02 =
            cos(angle) * (carCoordinates.posX2 - posX) - sin(angle) * (carCoordinates.posY2 - posY) + posX
        carCoordinates.posY02 =
            sin(angle) * (carCoordinates.posX2 - posX) + cos(angle) * (carCoordinates.posY2 - posY) + posY

        carCoordinates.posX3 = posX - rectangle.width() / 2
        carCoordinates.posY3 = posY + rectangle.height() - rectangle.height() / 2
        carCoordinates.posX03 =
            cos(angle) * (carCoordinates.posX3 - posX) - sin(angle) * (carCoordinates.posY3 - posY) + posX
        carCoordinates.posY03 =
            sin(angle) * (carCoordinates.posX3 - posX) + cos(angle) * (carCoordinates.posY3 - posY) + posY

        carCoordinates.posX4 = posX + rectangle.width() - rectangle.width() / 2
        carCoordinates.posY4 = posY + rectangle.height() - rectangle.height() / 2
        carCoordinates.posX04 =
            cos(angle) * (carCoordinates.posX4 - posX) - sin(angle) * (carCoordinates.posY4 - posY) + posX
        carCoordinates.posY04 =
            sin(angle) * (carCoordinates.posX4 - posX) + cos(angle) * (carCoordinates.posY4 - posY) + posY

        if (velX >= 0.01 || velX <= -0.01 || velY >= 0.01 || velY <= -0.01) {
            val distance: Float = sqrt((0 - velX).pow(2) + (0 - velY).pow(2))
            dirX = velX / distance
            dirY = velY / distance
        }
    }
}
