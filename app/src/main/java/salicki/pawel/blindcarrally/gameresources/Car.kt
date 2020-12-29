package salicki.pawel.blindcarrally.gameresources

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.RectF
import androidx.annotation.RawRes
import salicki.pawel.blindcarrally.MainActivity
import salicki.pawel.blindcarrally.R
import salicki.pawel.blindcarrally.datas.CarParametersData
import salicki.pawel.blindcarrally.datas.CarViewData
import salicki.pawel.blindcarrally.information.Settings
import salicki.pawel.blindcarrally.resources.RawResources
import salicki.pawel.blindcarrally.utils.SoundManager
import salicki.pawel.blindcarrally.utils.VibratorManager
import kotlin.math.pow
import kotlin.math.sqrt


class Car(posX: Float, posY: Float, rect: RectF) : EnvironmentObject(posX, posY) {
    private var carParameters = CarParametersData()
    private var carSensors: CarSensors =
        CarSensors()
    private var carHitbox: CarHitbox =
        CarHitbox()
    private var carViewData: CarViewData = CarViewData()

    private var soundManagerLeft =
        SoundManager()
    private var soundManagerRight =
        SoundManager()
    private var soundManagerEngine =
        SoundManager()
    private var soundManagerGears =
        SoundManager()
    private var soundManagerCollision =
        SoundManager()

    private var engineLoop: Boolean = false

    private var sensorBeepIteratorLeft: Int = 0
    private var sensorBeepIteratorRight: Int = 0
    private var engineIterator: Int = 0
    private var engineLoopIterator: Int = 0

    init {
        initSoundManager();
        initCarView(rect)
        initCarHitbox()
        initCarDistanceSensors()
    }

    private fun initSoundManager() {
        soundManagerLeft.initSoundManager(1)
        soundManagerRight.initSoundManager(1)
        soundManagerEngine.initSoundManager(1)
        soundManagerGears.initSoundManager(3)
        soundManagerCollision.initSoundManager(1)

        soundManagerLeft.addSound(RawResources.beepSound)
        soundManagerRight.addSound(RawResources.beepSound)
        soundManagerEngine.addSound(RawResources.engineSound)
        soundManagerCollision.addSound(RawResources.collisionSound)

        soundManagerGears.addSound(RawResources.gearUpSound)
        soundManagerGears.addSound(RawResources.gearDownSound)
        soundManagerGears.addSound(RawResources.gearBlockedSound)
    }

    private fun carEngine() {
        if(engineLoop){
            engineLoopIterator++

            if(engineLoopIterator >= Settings.FPS * 2){
                engineLoopIterator = 0
                engineLoop = false
            }
        }

        if (carParameters.speed > 0.01) {
            if (engineIterator > 2) {
                engineIterator = 0
                engineLoop = false
                soundManagerEngine.playSound(
                    RawResources.engineSound,
                    0.35F,
                    0.35F,
                    0,
                    carParameters.speed / carParameters.maxSpeed + 0.9F + carParameters.gear * 0.05F
                )
            }
        } else {
            if (!engineLoop) {
                soundManagerEngine.playSound(RawResources.engineSound, 0.5F, 0.5F, 0)
                engineLoop = true
            }
        }
    }

    private fun initCarView(rect: RectF) {
        carViewData.carRectangle = rect
        carViewData.carHalfWidth = carViewData.carRectangle!!.width() / 2
        carViewData.carHalfHeight = carViewData.carRectangle!!.height() / 2

        carViewData.carRectangle?.set(
            (posX - carViewData.carHalfWidth),
            (posY - carViewData.carHalfHeight),
            (posX + carViewData.carHalfWidth),
            (posY + carViewData.carHalfHeight)
        )
    }

    private fun initCarHitbox() {
        carHitbox.initHitbox()
    }

    private fun initCarDistanceSensors() {
        carParameters.obstacleSensorLength = 20F

        carSensors.initCarDistanceSensors(carParameters.obstacleSensorLength)
    }

    override fun draw(canvas: Canvas?, coordinateDisplayManager: CoordinateDisplayManager?) {
    }

    fun destroyCar() {
        soundManagerLeft.destroy()
        soundManagerRight.destroy()
        soundManagerEngine.destroy()
        soundManagerGears.destroy()
        soundManagerCollision.destroy()
    }

    /*
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
*/

    fun sensorCheck(x1: Float, y1: Float, x2: Float, y2: Float): Boolean {
        var sensorDistance = carSensors.sensorDistance(x1, y1, x2, y2)

        if (sensorDistance.left) {
            val volume = 1 / (sensorDistance.leftLength + 1)
            if (sensorBeepIteratorLeft > 2 * 1 / volume) {
                sensorBeepIteratorLeft = 0
                soundManagerLeft.playSound(RawResources.beepSound, volume, 0F)
            }
        }

        if (sensorDistance.right) {
            val volume = 1 / (sensorDistance.rightLength + 1)
            if (sensorBeepIteratorRight > 2 * 1 / volume) {
                sensorBeepIteratorRight = 0
                soundManagerRight.playSound(RawResources.beepSound, 0F, volume)
            }
        }
        return sensorDistance.right || sensorDistance.left
    }

    fun pushCar(left: Float, right: Float) {
        posX += left
        posX -= right
    }

    fun collisionCheck(x1: Float, y1: Float, x2: Float, y2: Float, leftPath: Boolean): Boolean {
        val collision = carHitbox.collisionCheck(x1, y1, x2, y2)
        if (collision) {
            this.carParameters.health -= 5
            if (leftPath) {
                soundManagerCollision.playSound(RawResources.collisionSound, 0.6F, 0F)
            } else {
                soundManagerCollision.playSound(RawResources.collisionSound, 0F, 0.6F)
            }
            VibratorManager.vibrate(500)

            return true
        }

        return false
    }

    fun getCarHealth(): Int {
        return this.carParameters.health
    }

    fun higherGear() {
        if (carParameters.speed > 0.8 * carParameters.maxSpeed) {
            if (carParameters.gear < carParameters.maxGear) {
                carParameters.gear++
                soundManagerGears.playSound(RawResources.gearUpSound)
            }
        } else {
            soundManagerGears.playSound(RawResources.gearBlockedSound)
        }
    }

    fun lowerGear() {
        if (carParameters.gear > carParameters.minGear) {
            carParameters.gear--
            soundManagerGears.playSound(RawResources.gearDownSound)
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

                if ((Settings.CONTEXT as MainActivity).getOrientation() == 3) {
                    pitch *= -1
                }

                if ((Settings.CONTEXT as MainActivity).getOrientation() == 1) {
                    roll *= -1
                }

                velX = 100 * roll
                velY = if (pitch < 0) {
                    pitch * carParameters.acceleration
                } else {
                    pitch * carParameters.acceleration
                }
            }
        }
    }

    private fun updateCarSpeed() {
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

        if (carParameters.gear > carParameters.minGear) {
            if (carParameters.speed < (carParameters.gear * 0.1 - 0.1) * 0.8F) {
                carParameters.gear--
                soundManagerGears.playSound(RawResources.gearDownSound)
            }
        }
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

    override fun update(coordinateDisplayManager: CoordinateDisplayManager?) {
        sensorBeepIteratorLeft++
        sensorBeepIteratorRight++
        engineIterator++
        engineLoopIterator++

        updateCarPosition(coordinateDisplayManager)
        updateCarMovement()
        updateCarSpeed()
        updateCarHitboxPosition()
        updateCarSensorsDistancePosition()
        carEngine()
    }
}
