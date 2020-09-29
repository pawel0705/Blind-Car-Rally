package salicki.pawel.blindcarrally

import android.graphics.*
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

class Car(posX: Float, posY: Float, rect: RectF) : EnvironmentObject(posX, posY) {

    private var speed: Float = 0F
    private var angle: Float = 0F
    private val maxSpeed: Float = 1F
    private var turnSpeed: Float = 0.008F

    private var rectangle: RectF = rect
    private var texture: Texture
    private val movementManager: MovementManager? = Settings.CONTEXT?.let { MovementManager(it) }

    private var paint: Paint = Paint()
    private var carCoordinates: CarCoordinates = CarCoordinates()

    init {
        movementManager?.register()

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
            carCoordinates.posX1, carCoordinates.posY1, carCoordinates.posX3, carCoordinates.posY3
        )
        val right: Boolean = lineCollision(
            x1, y1, x2, y2,
            carCoordinates.posX2, carCoordinates.posY2, carCoordinates.posX4, carCoordinates.posY4
        )
        val top: Boolean = lineCollision(
            x1, y1, x2, y2,
            carCoordinates.posX1, carCoordinates.posY1, carCoordinates.posX2, carCoordinates.posY2
        )
        var bottom: Boolean = lineCollision(
            x1, y1, x2, y2,
            carCoordinates.posX3, carCoordinates.posY3, carCoordinates.posX4, carCoordinates.posY4
        )

        if (left || right || top || bottom) {
            paint.color = Color.RED

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

    override fun update() {
        if (movementManager != null) {
            if (movementManager.getOrientation() != null && movementManager.getStartOrientation() != null) {
                val pitch: Float =
                    movementManager?.getOrientation()!![2] - movementManager.getStartOrientation()!![2]
                val roll: Float =
                    movementManager?.getOrientation()!![1] - movementManager.getStartOrientation()!![1]

                velX = 2 * roll * Settings.SCREEN_WIDTH / 1000f
                velY = -pitch * Settings.SCREEN_HEIGHT / 1000f
            }
        }

        if (speed > 2) {
            speed = 2F
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
