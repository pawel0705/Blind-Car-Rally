package salicki.pawel.blindcarrally.gameresources

import salicki.pawel.blindcarrally.gameresources.EnvironmentObject
import salicki.pawel.blindcarrally.information.Settings

class CoordinateDisplayManager(environmentObject: EnvironmentObject) {

    private var environmentOffsetX: Float = 0.0F
    private var environmentOffsetY: Float = 0.0F

    private var environmentCenterX: Float = 0.0F
    private var environmentCenterY: Float = 0.0F

    private val environmentObject: EnvironmentObject = environmentObject

    private var screenCenterX: Float = Settings.SCREEN_WIDTH / 2.0F
    private var screenCenterY: Float = Settings.SCREEN_HEIGHT / 2.0F

    init {
        updateEnvironmentCoordinates()
    }

    fun updateEnvironmentCoordinates() {
        environmentCenterX = environmentObject.posX
        environmentCenterY = environmentObject.posY

        environmentOffsetX = screenCenterX - environmentCenterX
        environmentOffsetY = screenCenterY - environmentCenterY
    }

    fun convertToEnvironmentX(x: Float): Float {
        return x + environmentOffsetX;
    }

    fun convertToEnvironmentY(y: Float): Float {
        return y + environmentOffsetY;
    }
}