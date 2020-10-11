package salicki.pawel.blindcarrally

import android.content.res.Configuration
import android.os.Bundle
import android.os.Vibrator
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import salicki.pawel.blindcarrally.scenemanager.LevelManager
import kotlin.system.exitProcess


class MainActivity : AppCompatActivity() {

    private var SoundManager: SoundManager = SoundManager()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val displayMetrics = resources.displayMetrics
        Settings.SCREEN_HEIGHT = displayMetrics.heightPixels
        Settings.SCREEN_WIDTH = displayMetrics.widthPixels
        Settings.CONTEXT = this

        Settings.SCREEN_SCALE = displayMetrics.densityDpi.toFloat()

        TextToSpeechManager.initTextToSpeech()
        SoundManager.initSoundManager()

        setContentView(LevelManager);
    }

    fun exit() {
        finishAffinity()
        exitProcess(0)
    }

    fun vibratorService(): Vibrator {
        return getSystemService(VIBRATOR_SERVICE) as Vibrator
    }

    override fun onPause() {
        LevelManager.pause()

        super.onPause()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) hideSystemUI()
    }

    private fun hideSystemUI() {
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

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