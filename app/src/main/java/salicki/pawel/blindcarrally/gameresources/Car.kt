package salicki.pawel.blindcarrally.gameresources

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.RectF

import android.util.Log
import salicki.pawel.blindcarrally.*
import salicki.pawel.blindcarrally.datas.CarParametersData
import salicki.pawel.blindcarrally.datas.CarViewData
import salicki.pawel.blindcarrally.information.Settings
import salicki.pawel.blindcarrally.utils.SoundManager
import salicki.pawel.blindcarrally.utils.VibratorManager
import kotlin.math.pow
import kotlin.math.sqrt

class Car(posX: Float, posY: Float, rect: RectF) : EnvironmentObject(posX, posY) {

    private var carParameters = CarParametersData()
    private var gear = 0
    private var carSensors: CarSensors =
        CarSensors()
    private var carHitbox: CarHitbox =
        CarHitbox()
    private var carViewData: CarViewData = CarViewData()

    private var sensorBeepIteratorLeft: Int = 0
    private var sensorBeepIteratorRight: Int = 0
    private var engineIterator: Int = 0

    private var canBeep: Boolean = true;

    private var soundManagerLeft =
        SoundManager()
    private var soundManagerRight =
        SoundManager()
    private var soundManagerEngine =
        SoundManager()
    private var soundManagerGears =
        SoundManager()
    private var soundManagerCollition =
        SoundManager()

    private var canCollide = false

    private var gearRatio = arrayOf(0.1F, 0.2F, 0.3F, 0.4F, 0.5F, 0.6F)

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
        soundManagerCollition.initSoundManager()

        soundManagerLeft.addSound(R.raw.beep)
        soundManagerRight.addSound(R.raw.beep)
        soundManagerEngine.addSound(R.raw.engine_03)
        soundManagerCollition.addSound(R.raw.collision)

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

        if(carParameters.speed > 0.01){
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
        carViewData.carRectangle = rect
        carViewData.carHalfWidth = carViewData.carRectangle!!.width() / 2
        carViewData.carHalfHeight = carViewData.carRectangle!!.height() / 2
        carViewData.carPaint.color = Color.YELLOW

        carViewData.carRectangle?.set(
            (posX - carViewData.carHalfWidth),
            (posY - carViewData.carHalfHeight),
            (posX + carViewData.carHalfWidth),
            (posY + carViewData.carHalfHeight)
        )


    }

    private fun initCarHitbox() {
        carHitbox.initHitbox(posX, posY, carViewData.carHalfWidth, carViewData.carHalfHeight)
    }

    private fun initCarDistanceSensors() {
        carParameters.obstacleSensorLength = 20F // Settings.SCREEN_SCALE * 0.5F


        carSensors.initCarDistanceSensors(carParameters.obstacleSensorLength)
    }

    override fun draw(canvas: Canvas?, coordinateDisplayManager: CoordinateDisplayManager?) {

        if (coordinateDisplayManager != null && canvas != null) {

            drawCarHitbox(canvas, coordinateDisplayManager)
            drawCarSensors(canvas, coordinateDisplayManager)
        }
    }

    fun destroyCar(){
        soundManagerLeft.destroy()
        soundManagerRight.destroy()
        soundManagerEngine.destroy()
        soundManagerGears.destroy()
        soundManagerCollition.destroy()
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


    fun sensorCheck(x1: Float, y1: Float, x2: Float, y2: Float): Boolean {

        var sensorDistance = carSensors.sensorDistance(x1, y1, x2, y2)

        if (sensorDistance.left) {

            val volume = 1 / (sensorDistance.leftLength  + 1)

            Log.d("LEFT", sensorDistance.leftLength.toString())

            if (sensorBeepIteratorLeft > 2 * 1 / volume) {
                sensorBeepIteratorLeft = 0
                soundManagerLeft.playSound(R.raw.beep, volume, 0F)
            }
        }

        if (sensorDistance.right) {
            val volume = 1 / (sensorDistance.rightLength  + 1)

            if (sensorBeepIteratorRight > 2 * 1 / volume) {
                sensorBeepIteratorRight = 0
                soundManagerRight.playSound(R.raw.beep, 0F, volume)
            }
        }
        return sensorDistance.right || sensorDistance.left
    }

    fun pushCar(left: Float, right: Float){
        posX += left  //* Settings.SCREEN_SCALE * 0.0001F
        posX -= right // * Settings.SCREEN_SCALE * 0.0001F
    }

    fun collisionCheck(x1: Float, y1: Float, x2: Float, y2: Float, leftPath: Boolean): Boolean {

        val collision = carHitbox.collisionCheck(x1, y1, x2, y2)

        if(collision){
            this.carParameters.health -= 5
            if(leftPath){
                soundManagerCollition.playSound(R.raw.collision, 0.6F, 0F)
            }
            else {
                soundManagerCollition.playSound(R.raw.collision, 0F, 0.6F)
            }

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
            carViewData.carRectangle?.set(
                (coordinateDisplayManager.convertToEnvironmentX(posX) - carViewData.carHalfWidth),
                (coordinateDisplayManager.convertToEnvironmentY(posY) - carViewData.carHalfHeight),
                (coordinateDisplayManager.convertToEnvironmentX(posX) + carViewData.carHalfWidth),
                (coordinateDisplayManager.convertToEnvironmentY(posY) + carViewData.carHalfHeight)
            )
        }
    }

    private fun updateCarMovement() {


        if (MovementManager != null) {
            if (MovementManager.getOrientation() != null && MovementManager.getStartOrientation() != null) {
                var pitch: Float =
                    MovementManager.getOrientation()!![2] - MovementManager.getStartOrientation()!![2]
                var roll: Float =
                    MovementManager.getOrientation()!![1] - MovementManager.getStartOrientation()!![1]

                val phonePosition = MovementManager.getOrientation()!![0]

                if(phonePosition > -0.5F){
                    pitch *= -1
                } else {
                    roll *= -1
                }

                velX = 100 * roll // * Settings.SCREEN_SCALE

                velY = if(pitch<0){
                    pitch * carParameters.acceleration // * Settings.SCREEN_SCALE * 0.00005F *
                } else {
                    pitch * carParameters.acceleration // Settings.SCREEN_SCALE * 0.0005F *
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

        carParameters.maxSpeed = carParameters.gear.toFloat() * 0.1F

        if(carParameters.gear > carParameters.minGear){
            if(carParameters.speed < (carParameters.gear * 0.1 - 0.1) * 0.8F){
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
            carViewData.carHalfWidth,
            carViewData.carHalfHeight,
            carParameters.obstacleSensorLength
        )
    }

    private fun updateCarHitboxPosition() {
        carHitbox.updateHitboxPosition(
            posX,
            posY,
            carViewData.carHalfWidth,
            carViewData.carHalfHeight,
            carViewData.carRectangle!!.width()
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
