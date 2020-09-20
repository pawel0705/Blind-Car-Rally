package salicki.pawel.blindcarrally

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.getSystemService

object VibratorManager {
    private var vibrator : Vibrator? =  (Settings.CONTEXT as MainActivity).vibratorService()

    fun vibrate(time: Long){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator?.vibrate(VibrationEffect.createOneShot(time, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            vibrator?.vibrate(time)
        }
    }

    fun stop(){
        vibrator?.cancel()
    }
}