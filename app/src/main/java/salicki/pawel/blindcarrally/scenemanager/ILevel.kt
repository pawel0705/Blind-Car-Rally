package salicki.pawel.blindcarrally.scenemanager

import android.graphics.Canvas
import android.view.MotionEvent

interface ILevel {
    fun initState()
    fun updateState(deltaTime: Int)
    fun destroyState()
    fun respondTouchState(motionEvent: MotionEvent)
    fun redrawState(canvas: Canvas)
}