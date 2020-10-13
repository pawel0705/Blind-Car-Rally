package salicki.pawel.blindcarrally

import android.content.SharedPreferences
import androidx.preference.PreferenceManager

object SharedPreferencesManager {
    private var mPreferences: SharedPreferences =
        PreferenceManager.getDefaultSharedPreferences(Settings.CONTEXT)
    private var mEditor: SharedPreferences.Editor

    init {
        mEditor = mPreferences.edit()
    }

    fun saveConfiguration(key: String, value: String){
        mEditor.putString(key, value)
        mEditor.commit()
    }

    fun loadConfiguration(key: String) : String? {
        return mPreferences.getString(key, "")
    }
}