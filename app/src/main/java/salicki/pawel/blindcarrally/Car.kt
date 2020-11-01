package salicki.pawel.blindcarrally

import android.graphics.*
import android.util.Log
import salicki.pawel.blindcarrally.data.CarCoordinates
import salicki.pawel.blindcarrally.data.CarParameters
import salicki.pawel.blindcarrally.data.CarPositionSensors
import salicki.pawel.blindcarrally.data.CarView
import kotlin.math.*

class Car(posX: Float, posY: Float, rect: RectF) : EnvironmentObject(posX, posY) {

    private var carParameters = CarParameters()

    private var carSensors: CarSensors = CarSensors()
    private var carHitbox: CarHitbox = CarHitbox()
    private var carView: CarView = CarView()

    private var sensorBeepIteratorLeft: Int = 0
    private var sensorBeepIteratorRight: Int = 0

    private var canBeep: Boolean = true;

    private var soundManagerLeft = SoundManager()
    private var soundManagerRight = SoundManager()

    private var canCollide = false

    init {
        initSoundManager();
        initCarView(rect)
        initCarHitbox()
        initCarDistanceSensors()
    }

    private fun initSoundManager(){
        soundManagerLeft.initSoundManager()
        soundManagerRight.initSoundManager()

        soundManagerLeft.addSound(R.raw.beep)
        soundManagerRight.addSound(R.raw.beep)
    }

    private fun initCarView(rect: RectF) {
        carView.carRectangle = rect
        carView.carHalfWidth = carView.carRectangle!!.width() / 2
        carView.carHalfHeight = carView.carRectangle!!.height() / 2
        carView.carPaint.color = Color.YELLOW

        carView.carRectangle?.set(
            (posX - carView.carHalfWidth),
            (posY - carView.carHalfHeight),
            (posX + carView.carHalfWidth),
            (posY + carView.carHalfHeight)
        )

        val bitmap: Bitmap = BitmapFactory.decodeResource(
            Settings.CONTEXT?.resources,
            R.drawable.car
        )

        carView.carTexture = Texture(bitmap)
    }

    private fun initCarHitbox() {
        carHitbox.initHitbox(posX, posY, carView.carHalfWidth, carView.carHalfHeight)
    }

    private fun initCarDistanceSensors() {
        carParameters.obstacleSensorLength = Settings.SCREEN_SCALE * 1

        carSensors.initCarDistanceSensors(carParameters.obstacleSensorLength)
    }

    override fun draw(canvas: Canvas?, coordinateDisplayManager: CoordinateDisplayManager?) {

        if (coordinateDisplayManager != null && canvas != null) {

            drawCarHitbox(canvas, coordinateDisplayManager)
            drawCarSensors(canvas, coordinateDisplayManager)
        }
    }

    private fun drawCarSensors(
        canvas: Canvas?,
        coordinateDisplayManager: CoordinateDisplayManager?
    ) {
        if (canvas != null && coordinateDisplayManager != null) {
            carSensors.drawSensors(canvas, coordinateDisplayManager)
        }
    }

    private fun drawCarHitbox(
        canvas: Canvas?,
        coordinateDisplayManager: CoordinateDisplayManager?
    ) {
        if (canvas != null && coordinateDisplayManager != null) {
            carHitbox.drawHitbox(canvas, coordinateDisplayManager)
        }
    }


    fun sensorCheck(x1: Float, y1: Float, x2: Float, y2: Float, elapsedTime: Int): Boolean {

        var sensorDistance = carSensors.sensorDistance(x1, y1, x2, y2)

        if (sensorDistance.left) {

            val volume = 1 / (sensorDistance.leftLength * Settings.SCREEN_SCALE * 0.0001F + 1)



            if (sensorBeepIteratorLeft > 2 * 1 / volume) {
                Log.d("VOLUME LEFT", volume.toString())
                sensorBeepIteratorLeft = 0
                soundManagerLeft.playSound(R.raw.beep, 0F, volume)
            }
        }

        if (sensorDistance.right) {
            val volume = 1 / (sensorDistance.rightLength * Settings.SCREEN_SCALE * 0.0001F + 1)



            if (sensorBeepIteratorRight > 2 * 1 / volume) {
                Log.d("VOLUME RIGHT", volume.toString())
                sensorBeepIteratorRight = 0
                soundManagerRight.playSound(R.raw.beep, volume, 0F)
            }
        }
        return sensorDistance.right || sensorDistance.left
    }

    fun collisionCheck(x1: Float, y1: Float, x2: Float, y2: Float): Boolean {
        return carHitbox.collisionCheck(x1, y1, x2, y2)
    }

    fun higherGear() {
        if (carParameters.gear < carParameters.maxGear) {
            carParameters.gear++
        }
    }

    fun lowerGear() {
        if (carParameters.gear > carParameters.minGear) {
            carParameters.gear--
        }
    }

    private fun updateCarPosition(coordinateDisplayManager: CoordinateDisplayManager?) {
        if (coordinateDisplayManager != null) {
            carView.carRectangle?.set(
                (coordinateDisplayManager.convertToEnvironmentX(posX) - carView.carHalfWidth),
                (coordinateDisplayManager.convertToEnvironmentY(posY) - carView.carHalfHeight),
                (coordinateDisplayManager.convertToEnvironmentX(posX) + carView.carHalfWidth),
                (coordinateDisplayManager.convertToEnvironmentY(posY) + carView.carHalfHeight)
            )
        }
    }

    private fun updateCarMovement() {

        if (MovementManager != null) {
            if (MovementManager.getOrientation() != null && MovementManager.getStartOrientation() != null) {
                val pitch: Float =
                    MovementManager?.getOrientation()!![2] - MovementManager.getStartOrientation()!![2]
                val roll: Float =
                    MovementManager?.getOrientation()!![1] - MovementManager.getStartOrientation()!![1]

                velX = 2 * roll * Settings.SCREEN_WIDTH
                velY = -pitch * Settings.SCREEN_HEIGHT / 1000f

                //    Log.d("VELX", velX.toString())
            }
        }
    }

    private fun updateCarSpeed() {
/*
        if (carParameters.speed > 2 * carParameters.gear) {
            carParameters.speed = 2F * carParameters.gear
        } else if (carParameters.speed < -2) {
            carParameters.speed = -2F
        }
*/
        carParameters.speed += velY
        carParameters.angle += carParameters.turnSpeed * carParameters.speed / carParameters.maxSpeed * velX

        if (carParameters.speed < 0) {
            carParameters.speed = 0F
        } else if (carParameters.speed > carParameters.maxSpeed) {
            carParameters.speed = carParameters.maxSpeed
        }

        posY += carParameters.speed
        posX += carParameters.turnSpeed * carParameters.speed / carParameters.maxSpeed * velX




        //    Log.d("SPEEDD:", carParameters.speed.toString())
        //  Log.d("ANGLE:", carParameters.angle.toString())


    }

    private fun updateCarSensorsDistancePosition() {
        carSensors.updatePositionSensors(
            posX,
            posY,
            carView.carHalfWidth,
            carView.carHalfHeight,
            carParameters.obstacleSensorLength
        )
    }

    private fun updateCarHitboxPosition() {
        carHitbox.updateHitboxPosition(
            posX,
            posY,
            carView.carHalfWidth,
            carView.carHalfHeight,
            carView.carRectangle!!.width()
        )
    }

    private fun updateCarDirection() {
        if (velX >= 0.01 || velX <= -0.01 || velY >= 0.01 || velY <= -0.01) {
            val distance: Float = sqrt((0 - velX).pow(2) + (0 - velY).pow(2))
            dirX = velX / distance
            dirY = velY / distance
        }
    }

    override fun update(coordinateDisplayManager: CoordinateDisplayManager?) {

        sensorBeepIteratorLeft++
        sensorBeepIteratorRight++

        updateCarPosition(coordinateDisplayManager)
        updateCarMovement()
        updateCarSpeed()
        updateCarHitboxPosition()
        updateCarSensorsDistancePosition()
        updateCarDirection()
    }

    fun removeCollision() {
        canCollide = false
    }
}
