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

    private var canCollide = false

    private var gearRatio = arrayOf(0.25, 0.50, 0.75, 1.0, 1.25, 1.50)

    init {
        initSoundManager();
        initCarView(rect)
        initCarHitbox()
        initCarDistanceSensors()
    }

    private fun initSoundManager(){
        soundManagerLeft.initSoundManager()
        soundManagerRight.initSoundManager()
        soundManagerEngine.initSoundManager()

        soundManagerLeft.addSound(R.raw.beep)
        soundManagerRight.addSound(R.raw.beep)
        soundManagerEngine.addSound(R.raw.engine_03)
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

        if(gear == 0){
            gearMinValue = 0F
        } else {
            gearMinValue = gearRatio[gear-1].toFloat()
        }
        gearMaxValue = gearRatio[gear].toFloat()

        var enginePitch = ((carParameters.speed - gearMaxValue)/(gearMaxValue-gearMinValue)) + 1 / 3F

     //   Log.d("SPED", carParameters.speed.toString())
     //   Log.d("PITCH", (carParameters.speed / carParameters.maxSpeed + 1).toString())

        if(engineIterator > 2){
            engineIterator = 0
  //          soundManagerEngine.playSound(R.raw.engine_03, 1F, 1F, 0, carParameters.speed / carParameters.maxSpeed + 1)
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

            val volume = 1 / (sensorDistance.leftLength * Settings.SCREEN_SCALE * 0.0001F + 1)



            if (sensorBeepIteratorLeft > 2 * 1 / volume) {
       //         Log.d("VOLUME LEFT", volume.toString())
                sensorBeepIteratorLeft = 0
                soundManagerLeft.playSound(R.raw.beep, 0F, volume)
            }
        }

        if (sensorDistance.right) {
            val volume = 1 / (sensorDistance.rightLength * Settings.SCREEN_SCALE * 0.0001F + 1)



            if (sensorBeepIteratorRight > 2 * 1 / volume) {
            //    Log.d("VOLUME RIGHT", volume.toString())
                sensorBeepIteratorRight = 0
                soundManagerRight.playSound(R.raw.beep, volume, 0F)
            }
        }
        return sensorDistance.right || sensorDistance.left
    }

    fun collisionCheck(x1: Float, y1: Float, x2: Float, y2: Float): Boolean {

        val collision = carHitbox.collisionCheck(x1, y1, x2, y2)

        if(collision){
            

            return true
        }

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
