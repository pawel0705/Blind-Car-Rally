package salicki.pawel.blindcarrally

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.RectF

import android.util.Log
import android.view.Display
import android.view.Surface
import salicki.pawel.blindcarrally.data.CarParameters
import salicki.pawel.blindcarrally.data.CarView
import kotlin.math.pow
import kotlin.math.sqrt

class Car(posX: Float, posY: Float, rect: RectF) : EnvironmentObject(posX, posY) {

    private var carParameters = CarParameters()
    private var gear = 0
    private var carSensors: CarSensors = CarSensors()
    private var carHitbox: CarHitbox = CarHitbox()
    private var carView: CarView = CarView()

    private var sensorBeepIteratorLeft: Int = 0
    private var sensorBeepIteratorRight: Int = 0
    private var engineIterator: Int = 0

    private var canBeep: Boolean = true;

    private var soundManagerLeft = SoundManager()
    private var soundManagerRight = SoundManager()
    private var soundManagerEngine = SoundManager()
    private var soundManagerGears = SoundManager()

    private var canCollide = false

    private var gearRatio = arrayOf(1F, 2F, 3F, 4F, 5F, 6F)

    private var engineLoop: Boolean = false

    init {
        initSoundManager();
        initCarView(rect)
        initCarHitbox()
        initCarDistanceSensors()

        // test


    }

    private fun initSoundManager(){
        soundManagerLeft.initSoundManager()
        soundManagerRight.initSoundManager()
        soundManagerEngine.initSoundManager()
        soundManagerGears.initSoundManager()

        soundManagerLeft.addSound(R.raw.beep)
        soundManagerRight.addSound(R.raw.beep)
        soundManagerEngine.addSound(R.raw.engine_03)

        soundManagerGears.addSound(R.raw.gear_up)
        soundManagerGears.addSound(R.raw.gear_down)
        soundManagerGears.addSound(R.raw.gear_blocked)
    }

    private fun carEngine(){
        var i = 0
        loop@while(i < gearRatio.size){
            if(i > carParameters.speed){
                gear = i
                break@loop
            }

            i++
        }

        var gearMinValue : Float = 0.0F
        var gearMaxValue : Float = 0.0F

        gearMinValue = if(gear == 0){
            0F
        } else {
            gearRatio[gear-1].toFloat()
        }
        gearMaxValue = gearRatio[gear].toFloat()

        var enginePitch = ((carParameters.speed - gearMaxValue)/(gearMaxValue-gearMinValue)) + 1 / 3F

     //   Log.d("SPED", carParameters.speed.toString())
     //   Log.d("PITCH", (carParameters.speed / carParameters.maxSpeed + 1).toString())

        if(carParameters.speed > 0.1){
            if(engineIterator > 2){
                engineIterator = 0
                engineLoop = false
                soundManagerEngine.playSound(R.raw.engine_03, 0.2F, 0.2F, 0, carParameters.speed / carParameters.maxSpeed + 1)
            }
        } else {
            if(!engineLoop){
                soundManagerEngine.playSound(R.raw.engine_03, 0.5F, 0.5F, -1)
                engineLoop = true
            }
        }



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


    }

    private fun initCarHitbox() {
        carHitbox.initHitbox(posX, posY, carView.carHalfWidth, carView.carHalfHeight)
    }

    private fun initCarDistanceSensors() {
        carParameters.obstacleSensorLength = Settings.SCREEN_SCALE * 0.5F


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

            val volume = 1 / (sensorDistance.leftLength * 0.1F + 1)

            if (sensorBeepIteratorLeft > 2 * 1 / volume) {
                sensorBeepIteratorLeft = 0
                soundManagerLeft.playSound(R.raw.beep, volume, 0F)
            }
        }

        if (sensorDistance.right) {
            val volume = 1 / (sensorDistance.rightLength * 0.1F + 1)

            if (sensorBeepIteratorRight > 2 * 1 / volume) {
                sensorBeepIteratorRight = 0
                soundManagerRight.playSound(R.raw.beep, 0F, volume)
            }
        }
        return sensorDistance.right || sensorDistance.left
    }

    fun pushCar(left: Float, right: Float){
        posX += left
        posX -= right
    }

    fun collisionCheck(x1: Float, y1: Float, x2: Float, y2: Float): Boolean {

        val collision = carHitbox.collisionCheck(x1, y1, x2, y2)

        if(collision){
            this.carParameters.health -= 5
            VibratorManager.vibrate(500)

            return true
        }

        return false
    }

    fun getCarHealth() :Int{
        return this.carParameters.health
    }

    fun higherGear() {
        if(carParameters.speed > 0.8 * carParameters.maxSpeed) {
            if (carParameters.gear < carParameters.maxGear) {
                carParameters.gear++
                soundManagerGears.playSound(R.raw.gear_up)
            }
        }
        else {
            soundManagerGears.playSound(R.raw.gear_blocked)
        }
    }

    fun lowerGear() {
        if (carParameters.gear > carParameters.minGear) {
            carParameters.gear--
            soundManagerGears.playSound(R.raw.gear_down)
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
                var pitch: Float =
                    MovementManager?.getOrientation()!![2] - MovementManager.getStartOrientation()!![2]
                var roll: Float =
                    MovementManager?.getOrientation()!![1] - MovementManager.getStartOrientation()!![1]

                val phonePosition = MovementManager?.getOrientation()!![0]
                Log.d("pos", phonePosition.toString())
                if(phonePosition > -0.5F){
                    pitch *= -1
                } else {
                    roll *= -1
                }

                velX = 2 * roll * Settings.SCREEN_SCALE

                velY = if(pitch<0){
                    pitch * Settings.SCREEN_SCALE * 0.00005F * carParameters.acceleration
                } else {
                    pitch * Settings.SCREEN_SCALE * 0.0005F * carParameters.acceleration
                }

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

        // test


        carParameters.speed += velY
        carParameters.angle += carParameters.turnSpeed * carParameters.speed / carParameters.maxSpeed * velX

        if (carParameters.speed < 0) {
            carParameters.speed = 0F
        } else if (carParameters.speed > carParameters.maxSpeed) {
            carParameters.speed = carParameters.maxSpeed
        }

        posY += carParameters.speed
        posX += carParameters.turnSpeed * carParameters.speed / carParameters.maxSpeed * velX

        carParameters.maxSpeed = carParameters.gear.toFloat()

        if(carParameters.gear > carParameters.minGear){
            if(carParameters.speed < (carParameters.gear - 1) * 0.8F){
                carParameters.gear--
                soundManagerGears.playSound(R.raw.gear_down)
            }
        }


    }

    fun getCarSpeed() : Float{
        return this.carParameters.speed
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
        engineIterator++

        updateCarPosition(coordinateDisplayManager)
        updateCarMovement()
        updateCarSpeed()
        updateCarHitboxPosition()
        updateCarSensorsDistancePosition()
        updateCarDirection()
        carEngine()
    }

    fun removeCollision() {
        canCollide = false
    }
}
