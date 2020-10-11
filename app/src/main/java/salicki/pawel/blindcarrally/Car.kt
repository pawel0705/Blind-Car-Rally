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
    private var carCoordinates: CarCoordinates = CarCoordinates()
    private var carPositionSensors: CarPositionSensors = CarPositionSensors()
    private var carView: CarView = CarView()

    private var canBeep: Boolean = true;

    private var iteratorUp = 0
    private var iteratorDown = 0
    private var iteratorLeft = 0
    private var iteratorRight = 0

    private var soundManagerLeft = SoundManager()
    private var soundManagerRight = SoundManager()

    private var canCollide = false

    init {
        soundManagerLeft.initSoundManager()
        soundManagerRight.initSoundManager()

        initCarView(rect)
        initCarHitbox()
        initCarDistanceSensors()
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
        carCoordinates.posX1 = posX - carView.carHalfWidth
        carCoordinates.posY1 = posY - carView.carHalfHeight

        carCoordinates.posX2 = posX + carView.carRectangle!!.width() - carView.carHalfWidth
        carCoordinates.posY2 = posY - carView.carHalfHeight

        carCoordinates.posX3 = posX - carView.carHalfWidth
        carCoordinates.posY3 = posY + carView.carRectangle!!.height() - carView.carHalfHeight

        carCoordinates.posX4 = posX + carView.carRectangle!!.width() - carView.carHalfWidth
        carCoordinates.posY4 = posY + carView.carRectangle!!.height() - carView.carHalfHeight
    }

    private fun initCarDistanceSensors() {
        carParameters.obstacleSensorLength = Settings.SCREEN_SCALE * 1

        carPositionSensors.sensorX1 = posX
        carPositionSensors.sensorY1 = posY - carView.carHalfHeight

        carPositionSensors.sensorX2 = posX
        carPositionSensors.sensorY2 =
            posY - carView.carHalfHeight - carParameters.obstacleSensorLength

        carPositionSensors.sensorX3 = posX
        carPositionSensors.sensorY3 = posY + carView.carHalfHeight

        carPositionSensors.sensorX4 = posX
        carPositionSensors.sensorY4 =
            posY + carView.carHalfHeight + carParameters.obstacleSensorLength

        carPositionSensors.sensorX5 = posX - carView.carHalfWidth
        carPositionSensors.sensorY5 = posY

        carPositionSensors.sensorX6 =
            posX - carView.carHalfWidth - carParameters.obstacleSensorLength
        carPositionSensors.sensorY6 = posY

        carPositionSensors.sensorX7 = posX + carView.carHalfWidth
        carPositionSensors.sensorY7 = posY

        carPositionSensors.sensorX8 =
            posX + carView.carHalfWidth + carParameters.obstacleSensorLength
        carPositionSensors.sensorY8 = posY
    }

    override fun draw(canvas: Canvas?, coordinateDisplayManager: CoordinateDisplayManager?) {

        if (coordinateDisplayManager != null && canvas != null) {

            drawCarHitbox(canvas, coordinateDisplayManager)
            drawCarRotation(canvas, coordinateDisplayManager)
            drawCarSensors(canvas, coordinateDisplayManager)
        }
    }

    private fun drawCarRotation(
        canvas: Canvas?,
        coordinateDisplayManager: CoordinateDisplayManager?
    ) {

        if (canvas != null && coordinateDisplayManager != null) {

            canvas.save();
            canvas.rotate(
                (carParameters.angle * 180 / PI).toFloat(),
                coordinateDisplayManager.convertToEnvironmentX(posX),
                coordinateDisplayManager.convertToEnvironmentY(posY)
            )

   //         carView.carRectangle?.let { canvas.drawRect(it, carView.carPaint) }
     //       carView.carRectangle?.let { carView.carTexture?.drawTexture(canvas, it) }
            canvas.restore();
        }
    }

    private fun drawCarSensors(
        canvas: Canvas?,
        coordinateDisplayManager: CoordinateDisplayManager?
    ) {
        if (canvas != null && coordinateDisplayManager != null) {

            var sensorColor = Paint()
            sensorColor.color = Color.BLUE

            canvas.drawLine(
                coordinateDisplayManager.convertToEnvironmentX(carPositionSensors.sensorX01),
                coordinateDisplayManager.convertToEnvironmentY(carPositionSensors.sensorY01),
                coordinateDisplayManager.convertToEnvironmentX(carPositionSensors.sensorX02),
                coordinateDisplayManager.convertToEnvironmentY(carPositionSensors.sensorY02),
                carView.carPaint
            )

            canvas.drawLine(
                coordinateDisplayManager.convertToEnvironmentX(carPositionSensors.sensorX03),
                coordinateDisplayManager.convertToEnvironmentY(carPositionSensors.sensorY03),
                coordinateDisplayManager.convertToEnvironmentX(carPositionSensors.sensorX04),
                coordinateDisplayManager.convertToEnvironmentY(carPositionSensors.sensorY04),
                sensorColor
            )

            canvas.drawLine(
                coordinateDisplayManager.convertToEnvironmentX(carPositionSensors.sensorX05),
                coordinateDisplayManager.convertToEnvironmentY(carPositionSensors.sensorY05),
                coordinateDisplayManager.convertToEnvironmentX(carPositionSensors.sensorX06),
                coordinateDisplayManager.convertToEnvironmentY(carPositionSensors.sensorY06),
                sensorColor
            )

            canvas.drawLine(
                coordinateDisplayManager.convertToEnvironmentX(carPositionSensors.sensorX07),
                coordinateDisplayManager.convertToEnvironmentY(carPositionSensors.sensorY07),
                coordinateDisplayManager.convertToEnvironmentX(carPositionSensors.sensorX08),
                coordinateDisplayManager.convertToEnvironmentY(carPositionSensors.sensorY08),
                sensorColor
            )



        }
    }

    private fun drawCarHitbox(
        canvas: Canvas?,
        coordinateDisplayManager: CoordinateDisplayManager?
    ) {
        if (canvas != null && coordinateDisplayManager != null) {

            var hitboxColor = Paint()
            hitboxColor.color = Color.GREEN

            canvas.drawLine(
                coordinateDisplayManager.convertToEnvironmentX(carCoordinates.posX01),
                coordinateDisplayManager.convertToEnvironmentY(carCoordinates.posY01),
                coordinateDisplayManager.convertToEnvironmentX(carCoordinates.posX02),
                coordinateDisplayManager.convertToEnvironmentY(carCoordinates.posY02),
                hitboxColor
            )
            canvas.drawLine(
                coordinateDisplayManager.convertToEnvironmentX(carCoordinates.posX01),
                coordinateDisplayManager.convertToEnvironmentY(carCoordinates.posY01),
                coordinateDisplayManager.convertToEnvironmentX(carCoordinates.posX03),
                coordinateDisplayManager.convertToEnvironmentY(carCoordinates.posY03),
                hitboxColor
            )
            canvas.drawLine(
                coordinateDisplayManager.convertToEnvironmentX(carCoordinates.posX02),
                coordinateDisplayManager.convertToEnvironmentY(carCoordinates.posY02),
                coordinateDisplayManager.convertToEnvironmentX(carCoordinates.posX04),
                coordinateDisplayManager.convertToEnvironmentY(carCoordinates.posY04),
                hitboxColor
            )
            canvas.drawLine(
                coordinateDisplayManager.convertToEnvironmentX(carCoordinates.posX03),
                coordinateDisplayManager.convertToEnvironmentY(carCoordinates.posY03),
                coordinateDisplayManager.convertToEnvironmentX(carCoordinates.posX04),
                coordinateDisplayManager.convertToEnvironmentY(carCoordinates.posY04),
                hitboxColor
            )
        }
    }


    fun sensorCheck(x1: Float, y1: Float, x2: Float, y2: Float, elapsedTime: Int): Boolean {

        var sensorUp: Boolean = false
        var sensorDown: Boolean = false
        var sensorLeft: Boolean = false
        var sensorRight: Boolean = false

        if (Mathematics.collisionLineToLine(
                x1, y1, x2, y2,
                carPositionSensors.sensorX01, carPositionSensors.sensorY01,
                carPositionSensors.sensorX02, carPositionSensors.sensorY02
            )
        ) {
            sensorUp = true

            carView.carPaint.color = Color.RED

            var distanceToObstacleUp = Mathematics.distancePointToLine(
                carPositionSensors.sensorX01,
                carPositionSensors.sensorY01,
                x1,
                y1,
                x2,
                y2
            )

           // Log.d("DYSTANS", distanceToObstacleUp.toString())

       //     val volume = 1 / (distanceToObstacleUp * Settings.SCREEN_SCALE * 0.0001F + 1).toFloat()

      //      iterator += elapsedTime

      //      if(iterator > 2 * 1/volume){
      //          iterator = 0
     //           SoundManager.playSound(R.raw.beep, volume, volume)
      //      }




        //    SoundManager.playSound(R.raw.beep, volume, volume)
        }

        if (Mathematics.collisionLineToLine(
                carPositionSensors.sensorX03, carPositionSensors.sensorY03,
                carPositionSensors.sensorX04, carPositionSensors.sensorY04, x1, y1, x2, y2
            )
        ) {
            sensorDown = true

            var distanceToObstacleDown = Mathematics.distancePointToLine(
                carPositionSensors.sensorX03,
                carPositionSensors.sensorY03,
                x1,
                y1,
                x2,
                y2
            )

        }

        if (Mathematics.collisionLineToLine(
                carPositionSensors.sensorX05, carPositionSensors.sensorY05,
                carPositionSensors.sensorX06, carPositionSensors.sensorY06, x1, y1, x2, y2
            )
        ) {
            sensorLeft = true

            var distanceToObstacleLeft = Mathematics.distancePointToLine(
                carPositionSensors.sensorX05,
                carPositionSensors.sensorY05,
                x1,
                y1,
                x2,
                y2
            )

                 val volume = 1 / (distanceToObstacleLeft * Settings.SCREEN_SCALE * 0.0001F + 1).toFloat()



                  if(iteratorLeft > 2 * 1/volume){
                      iteratorLeft = 0
                       soundManagerRight.playSound(R.raw.beep, 0F, volume)
                 }

        }

        if (Mathematics.collisionLineToLine(
                carPositionSensors.sensorX07, carPositionSensors.sensorY07,
                carPositionSensors.sensorX08, carPositionSensors.sensorY08, x1, y1, x2, y2
            )
        ) {
            sensorRight = true

            var distanceToObstacleRight = Mathematics.distancePointToLine(
                carPositionSensors.sensorX07,
                carPositionSensors.sensorY07,
                x1,
                y1,
                x2,
                y2
            )

            val volume = 1 / (distanceToObstacleRight * Settings.SCREEN_SCALE * 0.0001F + 1).toFloat()



            if(iteratorRight > 2 * 1/volume){
                iteratorRight = 0
                soundManagerLeft.playSound(R.raw.beep,volume, 0F)
            }


        }

        if(sensorUp || sensorDown || sensorLeft || sensorRight){
            return true
        }
        else {
            if(!canCollide){
                carView.carPaint.color = Color.BLUE
            }

        }

        return false
    }

    fun collisionCheck(x1: Float, y1: Float, x2: Float, y2: Float): Boolean {
        val left: Boolean = Mathematics.collisionLineToLine(
            x1,
            y1,
            x2,
            y2,
            carCoordinates.posX01,
            carCoordinates.posY01,
            carCoordinates.posX03,
            carCoordinates.posY03
        )
        val right: Boolean = Mathematics.collisionLineToLine(
            x1,
            y1,
            x2,
            y2,
            carCoordinates.posX02,
            carCoordinates.posY02,
            carCoordinates.posX04,
            carCoordinates.posY04
        )
        val top: Boolean = Mathematics.collisionLineToLine(
            x1,
            y1,
            x2,
            y2,
            carCoordinates.posX01,
            carCoordinates.posY01,
            carCoordinates.posX02,
            carCoordinates.posY02
        )
        var bottom: Boolean = Mathematics.collisionLineToLine(
            x1,
            y1,
            x2,
            y2,
            carCoordinates.posX03,
            carCoordinates.posY03,
            carCoordinates.posX04,
            carCoordinates.posY04
        )

        if (left || right || top || bottom) {
            carView.carPaint.color = Color.RED

            return true
        }

        carView.carPaint.color = Color.YELLOW
        return false
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

                velX = -2 * roll * Settings.SCREEN_WIDTH / 1000f
                velY = -pitch * Settings.SCREEN_HEIGHT / 1000f
            }
        }
    }

    private fun updateCarSpeed() {

        if (carParameters.speed > 2 * carParameters.gear) {
            carParameters.speed = 2F * carParameters.gear
        } else if (carParameters.speed < -2) {
            carParameters.speed = -2F
        }

        carParameters.speed += velY
        carParameters.angle += carParameters.turnSpeed * carParameters.speed / carParameters.maxSpeed * velX

        posX += sin(carParameters.angle) * carParameters.speed
        posY -= cos(carParameters.angle) * carParameters.speed
    }

    private fun updateCarSensorsDistancePosition() {
        carPositionSensors.sensorX1 = posX
        carPositionSensors.sensorY1 = posY - carView.carHalfHeight
        carPositionSensors.sensorX01 =
            cos(carParameters.angle) * (carPositionSensors.sensorX1 - posX) - sin(carParameters.angle) * (carPositionSensors.sensorY1 - posY) + posX
        carPositionSensors.sensorY01 =
            sin(carParameters.angle) * (carPositionSensors.sensorX1 - posX) + cos(carParameters.angle) * (carPositionSensors.sensorY1 - posY) + posY

        carPositionSensors.sensorX2 = posX
        carPositionSensors.sensorY2 = posY - carView.carHalfHeight - carParameters.obstacleSensorLength
        carPositionSensors.sensorX02 =
            cos(carParameters.angle) * (carPositionSensors.sensorX2 - posX) - sin(carParameters.angle) * (carPositionSensors.sensorY2 - posY) + posX
        carPositionSensors.sensorY02 =
            sin(carParameters.angle) * (carPositionSensors.sensorX2 - posX) + cos(carParameters.angle) * (carPositionSensors.sensorY2 - posY) + posY

        carPositionSensors.sensorX3 = posX
        carPositionSensors.sensorY3 = posY + carView.carHalfHeight
        carPositionSensors.sensorX03 =
            cos(carParameters.angle) * (carPositionSensors.sensorX3 - posX) - sin(carParameters.angle) * (carPositionSensors.sensorY3 - posY) + posX
        carPositionSensors.sensorY03 =
            sin(carParameters.angle) * (carPositionSensors.sensorX3 - posX) + cos(carParameters.angle) * (carPositionSensors.sensorY3 - posY) + posY

        carPositionSensors.sensorX4 = posX
        carPositionSensors.sensorY4 = posY + carView.carHalfHeight + carParameters.obstacleSensorLength
        carPositionSensors.sensorX04 =
            cos(carParameters.angle) * (carPositionSensors.sensorX4 - posX) - sin(carParameters.angle) * (carPositionSensors.sensorY4 - posY) + posX
        carPositionSensors.sensorY04 =
            sin(carParameters.angle) * (carPositionSensors.sensorX4 - posX) + cos(carParameters.angle) * (carPositionSensors.sensorY4 - posY) + posY

        carPositionSensors.sensorX5 = posX - carView.carHalfWidth
        carPositionSensors.sensorY5 = posY
        carPositionSensors.sensorX05 =
            cos(carParameters.angle) * (carPositionSensors.sensorX5 - posX) - sin(carParameters.angle) * (carPositionSensors.sensorY5 - posY) + posX
        carPositionSensors.sensorY05 =
            sin(carParameters.angle) * (carPositionSensors.sensorX5 - posX) + cos(carParameters.angle) * (carPositionSensors.sensorY5 - posY) + posY

        carPositionSensors.sensorX6 = posX - carView.carHalfWidth - carParameters.obstacleSensorLength
        carPositionSensors.sensorY6 = posY
        carPositionSensors.sensorX06 =
            cos(carParameters.angle) * (carPositionSensors.sensorX6 - posX) - sin(carParameters.angle) * (carPositionSensors.sensorY6 - posY) + posX
        carPositionSensors.sensorY06 =
            sin(carParameters.angle) * (carPositionSensors.sensorX6 - posX) + cos(carParameters.angle) * (carPositionSensors.sensorY6 - posY) + posY

        carPositionSensors.sensorX7 = posX + carView.carHalfWidth
        carPositionSensors.sensorY7 = posY
        carPositionSensors.sensorX07 =
            cos(carParameters.angle) * (carPositionSensors.sensorX7 - posX) - sin(carParameters.angle) * (carPositionSensors.sensorY7 - posY) + posX
        carPositionSensors.sensorY07 =
            sin(carParameters.angle) * (carPositionSensors.sensorX7 - posX) + cos(carParameters.angle) * (carPositionSensors.sensorY7 - posY) + posY

        carPositionSensors.sensorX8 = posX + carView.carHalfWidth + carParameters.obstacleSensorLength
        carPositionSensors.sensorY8 = posY
        carPositionSensors.sensorX08 =
            cos(carParameters.angle) * (carPositionSensors.sensorX8 - posX) - sin(carParameters.angle) * (carPositionSensors.sensorY8 - posY) + posX
        carPositionSensors.sensorY08 =
            sin(carParameters.angle) * (carPositionSensors.sensorX8 - posX) + cos(carParameters.angle) * (carPositionSensors.sensorY8 - posY) + posY
    }

    private fun updateCarHitboxPosition() {
        carCoordinates.posX1 = posX - carView.carHalfWidth
        carCoordinates.posY1 = posY - carView.carHalfHeight
        carCoordinates.posX01 =
            cos(carParameters.angle) * (carCoordinates.posX1 - posX) - sin(carParameters.angle) * (carCoordinates.posY1 - posY) + posX
        carCoordinates.posY01 =
            sin(carParameters.angle) * (carCoordinates.posX1 - posX) + cos(carParameters.angle) * (carCoordinates.posY1 - posY) + posY

        carCoordinates.posX2 = posX + carView.carRectangle!!.width() - carView.carHalfWidth
        carCoordinates.posY2 = posY - carView.carHalfHeight
        carCoordinates.posX02 =
            cos(carParameters.angle) * (carCoordinates.posX2 - posX) - sin(carParameters.angle) * (carCoordinates.posY2 - posY) + posX
        carCoordinates.posY02 =
            sin(carParameters.angle) * (carCoordinates.posX2 - posX) + cos(carParameters.angle) * (carCoordinates.posY2 - posY) + posY

        carCoordinates.posX3 = posX - carView.carHalfWidth
        carCoordinates.posY3 = posY + carView.carHalfHeight - carView.carHalfHeight
        carCoordinates.posX03 =
            cos(carParameters.angle) * (carCoordinates.posX3 - posX) - sin(carParameters.angle) * (carCoordinates.posY3 - posY) + posX
        carCoordinates.posY03 =
            sin(carParameters.angle) * (carCoordinates.posX3 - posX) + cos(carParameters.angle) * (carCoordinates.posY3 - posY) + posY

        carCoordinates.posX4 = posX + carView.carRectangle!!.width() - carView.carHalfWidth
        carCoordinates.posY4 = posY + carView.carHalfHeight - carView.carHalfHeight
        carCoordinates.posX04 =
            cos(carParameters.angle) * (carCoordinates.posX4 - posX) - sin(carParameters.angle) * (carCoordinates.posY4 - posY) + posX
        carCoordinates.posY04 =
            sin(carParameters.angle) * (carCoordinates.posX4 - posX) + cos(carParameters.angle) * (carCoordinates.posY4 - posY) + posY
    }

    private fun updateCarDirection() {
        if (velX >= 0.01 || velX <= -0.01 || velY >= 0.01 || velY <= -0.01) {
            val distance: Float = sqrt((0 - velX).pow(2) + (0 - velY).pow(2))
            dirX = velX / distance
            dirY = velY / distance
        }
    }

    override fun update(coordinateDisplayManager: CoordinateDisplayManager?) {

        iteratorUp++;
        iteratorDown++;
        iteratorLeft++
        iteratorRight++

        updateCarPosition(coordinateDisplayManager)
        updateCarMovement()
        updateCarSpeed()
        updateCarHitboxPosition()
        updateCarSensorsDistancePosition()
        updateCarDirection()
    }

     fun removeCollision(){
        canCollide = false
    }
}
