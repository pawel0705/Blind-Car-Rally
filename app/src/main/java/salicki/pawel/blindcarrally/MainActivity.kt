package salicki.pawel.blindcarrally

import android.content.res.Configuration
import android.hardware.SensorManager
import android.os.Bundle
import android.os.Vibrator
import android.util.Log
import android.view.OrientationEventListener
import android.view.Surface
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import salicki.pawel.blindcarrally.scenemanager.LevelManager
import kotlin.system.exitProcess


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val displayMetrics = resources.displayMetrics
        Settings.SCREEN_HEIGHT = displayMetrics.heightPixels
        Settings.SCREEN_WIDTH = displayMetrics.widthPixels
        Settings.CONTEXT = this

        Settings.SCREEN_SCALE = displayMetrics.densityDpi.toFloat()
        Settings.globalSounds.addSound(Resources.acceptSound)
        Settings.globalSounds.addSound(Resources.swapSound)

        TextToSpeechManager.initTextToSpeech()

        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        WindowManager.LayoutParams.FLAG_FULLSCREEN)

        setContentView(LevelManager);


    }



    fun exit() {
        TextToSpeechManager.destroy()

        finishAffinity()
        exitProcess(0)
    }

    fun vibratorService(): Vibrator {
        return getSystemService(VIBRATOR_SERVICE) as Vibrator
    }

    override fun onPause() {
        TextToSpeechManager.stop()
        LevelManager.pause()

        super.onPause()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        Log.d("ROT", "TOR")

        val rotation = windowManager.defaultDisplay.rotation
        Toast.makeText(this, "TOAS", Toast.LENGTH_SHORT).show()
        when (rotation) {
            Surface.ROTATION_90 -> {
                Toast.makeText(this, "90", Toast.LENGTH_SHORT).show()
            }
            Surface.ROTATION_180 -> {
                Toast.makeText(this, "180", Toast.LENGTH_SHORT).show()

            }
            Surface.ROTATION_270 -> {

                Toast.makeText(this, "270", Toast.LENGTH_SHORT).show()
            }
            else -> {

                Toast.makeText(this, "0", Toast.LENGTH_SHORT).show()
            }
        }

        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            val displayMetrics = resources.displayMetrics
            Settings.SCREEN_HEIGHT = displayMetrics.heightPixels
            Settings.SCREEN_WIDTH = displayMetrics.widthPixels

            Toast.makeText(this, "landscape", Toast.LENGTH_SHORT).show()
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {

            val displayMetrics = resources.displayMetrics
            Settings.SCREEN_HEIGHT = displayMetrics.heightPixels
            Settings.SCREEN_WIDTH = displayMetrics.widthPixels
            Toast.makeText(this, "portrait", Toast.LENGTH_SHORT).show()
        }
    }
}