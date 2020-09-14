package salicki.pawel.blindcarrally.scenemanager

import android.graphics.Canvas
import android.view.MotionEvent

interface IState {
    fun updateState()
    fun destroyState()
    fun respondTouchState(motionEvent: MotionEvent)
    fun redrawState(canvas: Canvas)
}