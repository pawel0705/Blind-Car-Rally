package salicki.pawel.blindcarrally.scene

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.Log
import android.view.MotionEvent
import android.view.SurfaceView
import androidx.core.content.ContextCompat
import salicki.pawel.blindcarrally.*
import salicki.pawel.blindcarrally.data.TrackData
import salicki.pawel.blindcarrally.data.TrackRaceData
import salicki.pawel.blindcarrally.scenemanager.ILevel
import salicki.pawel.blindcarrally.scenemanager.LevelManager


class GameLevel() : SurfaceView(Settings.CONTEXT), ILevel {

    private var trackReader = TrackReader()
    private var trackIterator = 0

    private var pilotTexts: HashMap<String, String> = HashMap()

    private var soundManagerGame: SoundManager = SoundManager()

    private var speakerCountDown: HashMap<String, Boolean> = HashMap()

    private var stopGameplay: Boolean = false
    private var countDown: Boolean = false

    private var startCountTime: Long = 0
    private var durationCount: Long = 0
    private val MAX_DURATION_COUNT = 6000

    private var newRoadTile = false

    private var swipe = false

    private var pauseGame = false

    private var car: Car = Car(
        Settings.SCREEN_WIDTH / 2F,
        Settings.SCREEN_HEIGHT / 2F,
        RectF(
            Settings.SCREEN_WIDTH / 2F,
            Settings.SCREEN_HEIGHT / 2F,
            Settings.SCREEN_WIDTH / 2F + 0.2F * Settings.SCREEN_SCALE,
            Settings.SCREEN_HEIGHT / 2F + 0.4F * Settings.SCREEN_SCALE
        )
    )
    private var coordinateDisplayManager: CoordinateDisplayManager

    private var trackData : TrackData? = null

    init {
        trackData = trackReader.readRacingTrack("trackTest.xml")
        //   Log.d("SKALA", scale.toString())

        coordinateDisplayManager = CoordinateDisplayManager(car)

        isFocusable = true

        readTTSTextFile()
        initSpeakerSpecial()
        initSoundManager()
    }

    private fun initSoundManager(){
        soundManagerGame.initSoundManager()

        soundManagerGame.addSound(R.raw.countdown)
        soundManagerGame.addSound(R.raw.start)
    }

    private fun initSpeakerSpecial(){
        speakerCountDown["COUNTDOWN_1"] = false
        speakerCountDown["COUNTDOWN_2"] = false
        speakerCountDown["COUNTDOWN_3"] = false
        speakerCountDown["COUNTDOWN_4"] = false
        speakerCountDown["COUNTDOWN_5"] = false
    }

    private fun readTTSTextFile() {
        pilotTexts.putAll(
            OpenerCSV.readData(
                R.raw.spotter_tts,
                Settings.languageTTS
            )
        )
    }

    private fun drawCoordinates(canvas: Canvas) {
        if (MovementManager.getOrientation() != null && MovementManager.getStartOrientation() != null) {
            val test1 = MovementManager.getOrientation()!![0].toDouble().toString()
            val test2 = MovementManager.getOrientation()!![1].toDouble().toString()
            val test3 = MovementManager.getOrientation()!![2].toDouble().toString()
            val paint = Paint()
            val color = ContextCompat.getColor(context, R.color.colorPrimary)
            paint.color = color
            paint.textSize = 50F
            canvas.drawText("orientation 1: $test1", 100F, 300F, paint)
            canvas.drawText("orientation 2: $test2", 100F, 400F, paint)
            canvas.drawText("orientation 3: $test3", 100F, 500F, paint)
        }
    }

    override fun initState() {
       car.posX = trackData!!.roadList?.get(trackIterator)?.spawnX!!.toFloat()
       car.posY = trackData!!.roadList?.get(trackIterator)?.spawnY!!.toFloat()

        TextToSpeechManager.speakNow(pilotTexts["INSTRUCTION"].toString())
    }

    private fun checkCollistion(){
        if (trackData != null) {

            for(left in trackData!!.roadList?.get(trackIterator)?.leftPoints!!){
                if(car.collisionCheck(left.startX.toFloat(), left.startY.toFloat(), left.endX.toFloat(), left.endY.toFloat())){
                    car.posX += 50
                }
            }

            for(right in trackData!!.roadList?.get(trackIterator)?.rightPints!!){
                if(car.collisionCheck(right.startX.toFloat(), right.startY.toFloat(), right.endX.toFloat(), right.endY.toFloat()))
                {
                    car.posX -= 50
                }
            }
        }
    }

    private fun checkDistance(deltaTime: Int){
        if (trackData != null) {

            leftSensor@for(left in trackData!!.roadList?.get(trackIterator)?.leftPoints!!){
                if(car.sensorCheck(left.startX.toFloat(), left.startY.toFloat(), left.endX.toFloat(), left.endY.toFloat(), deltaTime)){
                    break@leftSensor
                }
            }

            rightSensor@for(right in trackData!!.roadList?.get(trackIterator)?.rightPints!!){
                if(car.sensorCheck(right.startX.toFloat(), right.startY.toFloat(), right.endX.toFloat(), right.endY.toFloat(), deltaTime))
                {
                    break@rightSensor
                }
            }
        }
    }

    private fun countDown(){
        durationCount += System.currentTimeMillis() - startCountTime - durationCount


        if(durationCount > 1000 && speakerCountDown["COUNTDOWN_5"] == false) {
            TextToSpeechManager.speakNow(pilotTexts["COUNTDOWN_5"].toString())
            soundManagerGame.playSound(R.raw.countdown)
            speakerCountDown["COUNTDOWN_5"] = true
        } else if(durationCount > 2000 && speakerCountDown["COUNTDOWN_4"] == false){
            TextToSpeechManager.speakNow(pilotTexts["COUNTDOWN_4"].toString())
            soundManagerGame.playSound(R.raw.countdown)
            speakerCountDown["COUNTDOWN_4"] = true
        } else if(durationCount > 3000 && speakerCountDown["COUNTDOWN_3"] == false){
            TextToSpeechManager.speakNow(pilotTexts["COUNTDOWN_3"].toString())
            soundManagerGame.playSound(R.raw.countdown)
            speakerCountDown["COUNTDOWN_3"] = true
        } else if(durationCount > 4000 && speakerCountDown["COUNTDOWN_2"] == false){
            TextToSpeechManager.speakNow(pilotTexts["COUNTDOWN_2"].toString())
            soundManagerGame.playSound(R.raw.countdown)
            speakerCountDown["COUNTDOWN_2"] = true
        } else if(durationCount > 5000 && speakerCountDown["COUNTDOWN_1"] == false){
            TextToSpeechManager.speakNow(pilotTexts["COUNTDOWN_1"].toString())
            soundManagerGame.playSound(R.raw.countdown)
            speakerCountDown["COUNTDOWN_1"] = true
        } else if(durationCount > MAX_DURATION_COUNT){
            TextToSpeechManager.speakNow(pilotTexts["START"].toString())
            soundManagerGame.playSound(R.raw.start)
            newRoadTile = true
        }

        if(durationCount > MAX_DURATION_COUNT){
            stopGameplay = true
            countDown = false
        }
    }

    override fun updateState(deltaTime: Int) {

        if(countDown){
            countDown()
        }

        if(!stopGameplay){
            return
        }

        car.update(coordinateDisplayManager)
        coordinateDisplayManager.updateEnvironmentCoordinates()

        checkCollistion()
        checkDistance(deltaTime)

        if (trackData != null) {
            if(trackData!!.roadList[trackIterator].finishY < car.posY){
                trackIterator++

                newRoadTile = true

                car.posX = trackData!!.roadList?.get(trackIterator)?.spawnX!!.toFloat()
                car.posY = trackData!!.roadList?.get(trackIterator)?.spawnY!!.toFloat()
            }

            if(newRoadTile){

                for(tts in trackData!!.roadList[trackIterator].speakerKeys){
                    TextToSpeechManager.speakQueue(pilotTexts[tts].toString())
                }

                newRoadTile = false
            }
        }
    }

    override fun destroyState() {

    }

    override fun respondTouchState(event: MotionEvent) {
        swipe = false

        when (GestureManager.swipeDetect(event)) {
            GestureType.SWIPE_RIGHT -> {


                swipe = true
            }
            GestureType.SWIPE_LEFT -> {

                swipe = true
            }
            GestureType.SWIPE_UP -> {
                Settings.globalSounds.playSound(Resources.swapSound)

                LevelManager.stackLevel(PauseLevel())

                swipe = true
            }
        }

        when(GestureManager.tapPositionDetect(event)){
            GestureType.TAP_RIGHT->car.higherGear()
            GestureType.TAP_LEFT->car.lowerGear()
        }

        when (GestureManager.doubleTapDetect(event)) {

            GestureType.DOUBLE_TAP -> {
                TextToSpeechManager.stop()
                Settings.globalSounds.playSound(Resources.acceptSound)
                startGame()
            }
        }
    }

    private fun startGame(){
        startCountTime = System.currentTimeMillis()
        countDown = true
    }

    override fun redrawState(canvas: Canvas) {
        canvas.translate(0F, canvas.height.toFloat());   // reset where 0,0 is located
        canvas.scale(1F,-1F);    // invert

        drawCoordinates(canvas)
        car.draw(canvas, coordinateDisplayManager)

        var paint = Paint()
        paint.strokeWidth = Settings.SCREEN_SCALE * 0.02F
        paint.color = Color.WHITE

        if (trackData != null) {

            for(left in trackData!!.roadList?.get(trackIterator)?.leftPoints!!){
                canvas.drawLine(
                    coordinateDisplayManager.convertToEnvironmentX(left.startX.toFloat()),
                    coordinateDisplayManager.convertToEnvironmentY(left.startY.toFloat()),
                    coordinateDisplayManager.convertToEnvironmentX(left.endX.toFloat()),
                    coordinateDisplayManager.convertToEnvironmentY(left.endY.toFloat()),
                    paint
                )
            }

            for(right in trackData!!.roadList?.get(trackIterator)?.rightPints!!){
                canvas.drawLine(
                    coordinateDisplayManager.convertToEnvironmentX(right.startX.toFloat()),
                    coordinateDisplayManager.convertToEnvironmentY(right.startY.toFloat()),
                    coordinateDisplayManager.convertToEnvironmentX(right.endX.toFloat()),
                    coordinateDisplayManager.convertToEnvironmentY(right.endY.toFloat()),
                    paint
                )
            }
        }

    }
}