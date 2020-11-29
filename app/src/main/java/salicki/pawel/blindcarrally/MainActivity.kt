package salicki.pawel.blindcarrally

import android.content.Context
import android.os.Bundle
import android.os.Vibrator
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import salicki.pawel.blindcarrally.gameresources.TextToSpeechManager
import salicki.pawel.blindcarrally.information.Settings
import salicki.pawel.blindcarrally.resources.RawResources
import salicki.pawel.blindcarrally.scenemanager.LevelManager
import kotlin.system.exitProcess


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION

        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                exit()
            }
        }

        val displayMetrics = resources.displayMetrics
        Settings.SCREEN_HEIGHT = displayMetrics.heightPixels
        Settings.SCREEN_WIDTH = displayMetrics.widthPixels
        Settings.CONTEXT = this

        Settings.SCREEN_SCALE = displayMetrics.densityDpi.toFloat()
        Settings.globalSounds.addSound(RawResources.acceptSound)
        Settings.globalSounds.addSound(RawResources.swapSound)

        TextToSpeechManager.initTextToSpeech()

        setContentView(LevelManager)

        window.decorView.invalidate()

        this.getOrientation()
    }


    fun exit() {
        TextToSpeechManager.destroy()

        finishAffinity()
        LevelManager.destroyState()
        exitProcess(0)
        finish()
        moveTaskToBack(true);
    }

    fun vibratorService(): Vibrator {
        return getSystemService(VIBRATOR_SERVICE) as Vibrator
    }

    fun getOrientation(): Int{
        val display =
            (getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay

        return display.orientation
    }

    override fun onPause() {
        TextToSpeechManager.stop()
        LevelManager.pause()

        super.onPause()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            if (hasFocus) {
                window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
            }
        }
    }
}