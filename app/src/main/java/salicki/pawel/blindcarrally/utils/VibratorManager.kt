package salicki.pawel.blindcarrally.utils

import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import salicki.pawel.blindcarrally.MainActivity
import salicki.pawel.blindcarrally.information.Settings

object VibratorManager {
    private var vibrator: Vibrator? = (Settings.CONTEXT as MainActivity).vibratorService()

    fun vibrate(time: Long) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator?.vibrate(
                VibrationEffect.createOneShot(
                    time,
                    VibrationEffect.DEFAULT_AMPLITUDE
                )
            )
        } else {
            vibrator?.vibrate(time)
        }
    }
}