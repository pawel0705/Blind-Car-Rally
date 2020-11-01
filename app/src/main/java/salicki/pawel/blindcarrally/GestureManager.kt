package salicki.pawel.blindcarrally

import android.util.Log
import android.view.MotionEvent
import kotlin.math.abs

object GestureManager {

    private var swipe: Boolean = false

    // swipe
    private var x1 = 0f
    private var x2 = 0f
    private var y1 = 0f
    private var y2 = 0f
    private const val MIN_DISTANCE = 150
    private const val MAX_SWIPE_DURATION = 500;
    private var swipeStartTime: Long = 0
    private var swipeDuration: Long = 0

    // double tap
    private var clickCount = 0
    private var startTime: Long = 0
    private var duration: Long = 0
    private const val MAX_DURATION = 200

    //hold
    private var holdStartTime: Long = 0
    private var holdDuration: Long = 0
    private const val HOLD_MAX_DURATION = 500

    fun swipeDetect(event: MotionEvent): GestureType {
        swipe = false

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                x1 = event.x
                y1 = event.y

                swipeStartTime = System.currentTimeMillis()
            }
            MotionEvent.ACTION_UP -> {
                x2 = event.x
                y2 = event.y

                val deltaX = x2 - x1
                val deltaY = y2 - y1

                val time: Long = System.currentTimeMillis() - swipeStartTime
                swipeDuration += time

                Log.d("SWIPE", swipeDuration.toString())

                if (swipeDuration < MAX_SWIPE_DURATION) {
                    when {
                        abs(deltaX) > MIN_DISTANCE -> {
                            swipe = true

                            return if (x2 > x1) {
                                GestureType.SWIPE_RIGHT
                            } else {
                                GestureType.SWIPE_LEFT
                            }

                            duration = 0
                        }
                        abs(deltaY) > MIN_DISTANCE -> {
                            swipe = true

                            return if (y2 > y1) {
                                GestureType.SWIPE_DOWN
                            } else {
                                GestureType.SWIPE_UP
                            }
                        }
                        else -> {
                            swipe = false
                            swipeDuration = 0
                        }
                    }
                } else {
                    swipe = false
                    swipeDuration = 0
                }
            }
        }



        return GestureType.NONE
    }

    fun doubleTapDetect(event: MotionEvent): GestureType {

        when (event.action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_DOWN -> {
                startTime = System.currentTimeMillis()
                clickCount++

            }
            MotionEvent.ACTION_UP -> {
                val time: Long = System.currentTimeMillis() - startTime
                duration += time

                if (clickCount >= 2) {
                    if (duration <= MAX_DURATION) {
                        return GestureType.DOUBLE_TAP
                    }
                    clickCount = 0
                    duration = 0
                }
            }
        }

        return GestureType.NONE
    }


    fun tapPositionDetect(event: MotionEvent): GestureType {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {

                x1 = event.x
            }
            MotionEvent.ACTION_UP -> {
                return if (x1 > Settings.SCREEN_WIDTH / 2) {
                    GestureType.TAP_RIGHT
                } else {
                    GestureType.TAP_LEFT
                }
            }
        }

        return GestureType.NONE
    }

    fun holdPositionDetect(event: MotionEvent): Pair<Float, Float> {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                x1 = event.x

                holdStartTime = System.currentTimeMillis()
            }
            MotionEvent.ACTION_MOVE -> {

                holdDuration = System.currentTimeMillis() - holdStartTime


                Log.d("HOLD", holdDuration.toString())

                return if(holdDuration > HOLD_MAX_DURATION){
                    Pair(event.x, event.y)
                } else {
                    Pair(-1F, -1F)
                }
            }
            MotionEvent.ACTION_UP -> {
                holdDuration = 0
            }
        }

        return Pair(-1F, -1F);
    }
}