package salicki.pawel.blindcarrally

import android.graphics.Canvas
import androidx.core.content.ContextCompat
import kotlin.math.pow
import kotlin.math.sqrt

class Car(posX: Float, posY: Float, radius: Float) : Circle(
    Settings.CONTEXT?.let { ContextCompat.getColor(it, R.color.colorPrimary) },
    posX,
    posY,
    radius
) {

    private val movementManager: MovementManager? = Settings.CONTEXT?.let { MovementManager(it) }

    init {
        movementManager?.register()
    }

    override fun update() {

        if (movementManager != null) {
            if (movementManager.getOrientation() != null && movementManager.getStartOrientation() != null) {
                val pitch: Float =
                    movementManager?.getOrientation()!![2] - movementManager.getStartOrientation()!![2]
                val roll: Float =
                    movementManager?.getOrientation()!![1] - movementManager.getStartOrientation()!![1]


                velX = 2 * roll * Settings.SCREEN_WIDTH / 1000f
                velY = pitch * Settings.SCREEN_HEIGHT / 1000f

                /*
                playerPoint.x += if (Math.abs(xSpeed * elapsedTime) > 5) xSpeed * elapsedTime else 0
                playerPoint.y -= if (Math.abs(ySpeed * elapsedTime) > 5) ySpeed * elapsedTime else 0

                 */
            }
        }


/*
        if (movementManager != null) {
            velX = movementManager.getOrientation()!![0]
        }

        if (movementManager != null) {
            velY = movementManager.getOrientation()!![1]
        }
*/
        posX += velX
        posY+= velY

        // Update direction

        // Update direction
        if (velX >= 0.01 || velX <= -0.01 || velY >= 0.01 || velY <= -0.01) {
            // Normalize velocity to get direction (unit vector of velocity)
            val distance: Float = sqrt((0 - velX).pow(2) + (0 - velY).pow(2))
            dirX = velX / distance
            dirY = velY / distance
        }
    }

    override fun draw(canvas: Canvas?, coordinateDisplayManager: CoordinateDisplayManager?) {
        super.draw(canvas, coordinateDisplayManager)
    }
}