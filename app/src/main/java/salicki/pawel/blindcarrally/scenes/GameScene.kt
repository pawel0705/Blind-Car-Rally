package salicki.pawel.blindcarrally.scenes

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.os.Looper
import android.util.Log
import android.view.MotionEvent
import android.view.SurfaceView
import androidx.core.content.ContextCompat
import org.w3c.dom.Text
import salicki.pawel.blindcarrally.*
import salicki.pawel.blindcarrally.datas.StageResultData
import salicki.pawel.blindcarrally.datas.TrackData
import salicki.pawel.blindcarrally.enums.*
import salicki.pawel.blindcarrally.gameresources.*
import salicki.pawel.blindcarrally.information.GameOptions
import salicki.pawel.blindcarrally.information.Settings
import salicki.pawel.blindcarrally.resources.RawResources
import salicki.pawel.blindcarrally.resources.StagesResources
import salicki.pawel.blindcarrally.scenemanager.ILevel
import salicki.pawel.blindcarrally.scenemanager.LevelManager
import salicki.pawel.blindcarrally.utils.*


class GameScene() : SurfaceView(Settings.CONTEXT), ILevel {

    private var trackReader =
        TrackReader()
    private var instructionText: TextObject = TextObject()
    private var trackIterator = 0

    private var weather: WeatherEnum = WeatherEnum.SUN
    private var pilotQuoteText: String = ""
    private var raceTime = 0
    private var raceTimeSeconds = 0

    private var pilotTexts: HashMap<String, String> = HashMap()
    private var soundManagerGame: SoundManager =
        SoundManager()

    private var speakerCountDown: HashMap<String, Boolean> = HashMap()

    private var stopGameplay: Boolean = false
    private var countDown: Boolean = false
    private var MAX_SCORE: Int = 1000000;
    private var startCountTime: Long = 0
    private var durationCount: Long = 0
    private val MAX_DURATION_COUNT = 6000
    private var stageResult: StageResultData = StageResultData()
    private var newRoadTile = false
    private var countDownTextObject: TextObject = TextObject()
    private var speekerTextObject: TextObject = TextObject()
    private var swipe = false

    private var pauseGame = false

    private var idleTime: Int = 0
    private var idleTimeSeconds: Int = 0
    private var countDownText: String = ""
    private var windLeft: Float = 0F
    private var windFasterLeft: Boolean = true
    private var windRight: Float = 0F
    private var windFasterRight: Boolean = true
    private var disableTap: Boolean = false

    private var leftRadar: TextObject = TextObject()
    private var rightRadar: TextObject = TextObject()
    private var leftAlertRadar: Boolean = false
    private var rightAlertRadar: Boolean = false

    private var car: Car =
        Car(
            Settings.SCREEN_WIDTH / 2F,
            Settings.SCREEN_HEIGHT / 2F,
            RectF(
                Settings.SCREEN_WIDTH / 2F,
                Settings.SCREEN_HEIGHT / 2F,
                Settings.SCREEN_WIDTH / 2F + 10F, //+ 0.2F * Settings.SCREEN_SCALE,
                Settings.SCREEN_HEIGHT / 2F + 5F // + 0.4F * Settings.SCREEN_SCALE
            )
        )
    private var coordinateDisplayManager: CoordinateDisplayManager

    private var trackData: TrackData? = null

    init {
        trackData = if (GameOptions.gamemode == RacingModeEnum.TOURNAMENT_MODE) {
            trackReader.readRacingTrack(StagesResources.stageList[GameOptions.stageNumber].stageFileName)
        } else {
            trackReader.readRacingTrack(getStageFileName())
        }

        coordinateDisplayManager =
            CoordinateDisplayManager(
                car
            )

        isFocusable = true


        setWeather()
        readTTSTextFile()
        initSpeakerSpecial()
        initSoundManager()

        if(weather == WeatherEnum.WIND){
            MediaPlayerManager.initMediaPlayer(R.raw.hurricane)
            MediaPlayerManager.changeVolume(0.1F, 0.1F)
            MediaPlayerManager.loopSound()
            MediaPlayerManager.startSound()
        }
        else if(weather == WeatherEnum.RAIN){
            MediaPlayerManager.initMediaPlayer(R.raw.rain)
            MediaPlayerManager.changeVolume(0.3F, 0.3F)
            MediaPlayerManager.loopSound()
            MediaPlayerManager.startSound()
        }


        instructionText.initMultiLineText(
            R.font.montserrat, R.dimen.informationSize,
            Settings.SCREEN_WIDTH / 2F,
            Settings.SCREEN_HEIGHT / 8F,
            pilotTexts["INSTRUCTION"].toString()
        )

        countDownTextObject.initText(R.font.hemi, Settings.SCREEN_WIDTH / 2F, Settings.SCREEN_HEIGHT / 3F)
        speekerTextObject.initText(R.font.montserrat, Settings.SCREEN_WIDTH / 2F, Settings.SCREEN_HEIGHT / 3F)

        leftRadar.initText(R.font.hemi, Settings.SCREEN_WIDTH / 10F, Settings.SCREEN_HEIGHT / 1.5F, R.dimen.radarSize)
        rightRadar.initText(R.font.hemi, Settings.SCREEN_WIDTH / 1.15F, Settings.SCREEN_HEIGHT / 1.5F, R.dimen.radarSize)
    }

    private fun setWeather() {
        weather = when (GameOptions.stage) {
            StageEnum.STAGE_1 -> {
                WeatherEnum.SUN
            }
            StageEnum.STAGE_2 -> {
                WeatherEnum.RAIN
            }
            StageEnum.STAGE_3 -> {
                WeatherEnum.WIND
            }
        }
    }

    private fun getStageFileName(): String {
        when (GameOptions.nation) {
            NationEnum.ARGENTINA -> {
                return when (GameOptions.stage) {
                    StageEnum.STAGE_1 -> {
                        StagesResources.argentina_1
                    }
                    StageEnum.STAGE_2 -> {
                        StagesResources.argentina_2
                    }
                    StageEnum.STAGE_3 -> {
                        StagesResources.argentina_3
                    }
                }
            }
            NationEnum.AUSTRALIA -> {
                return when (GameOptions.stage) {
                    StageEnum.STAGE_1 -> {
                        StagesResources.australia_1
                    }
                    StageEnum.STAGE_2 -> {
                        StagesResources.australia_2
                    }
                    StageEnum.STAGE_3 -> {
                        StagesResources.australia_3
                    }
                }
            }
            NationEnum.POLAND -> {
                return when (GameOptions.stage) {
                    StageEnum.STAGE_1 -> {
                        StagesResources.poland_1
                    }
                    StageEnum.STAGE_2 -> {
                        StagesResources.poland_2
                    }
                    StageEnum.STAGE_3 -> {
                        StagesResources.poland_3
                    }
                }
            }
            NationEnum.SPAIN -> {
                return when (GameOptions.stage) {
                    StageEnum.STAGE_1 -> {
                        StagesResources.spain_1
                    }
                    StageEnum.STAGE_2 -> {
                        StagesResources.spain_2
                    }
                    StageEnum.STAGE_3 -> {
                        StagesResources.spain_3
                    }
                }
            }
            NationEnum.NEW_ZEALAND -> {
                return when (GameOptions.stage) {
                    StageEnum.STAGE_1 -> {
                        StagesResources.zealand_1
                    }
                    StageEnum.STAGE_2 -> {
                        StagesResources.zealand_2
                    }
                    StageEnum.STAGE_3 -> {
                        StagesResources.zealand_3
                    }
                }
            }
        }

        return ""
    }

    private fun initSoundManager() {
        soundManagerGame.initSoundManager()

        soundManagerGame.addSound(R.raw.countdown)
        soundManagerGame.addSound(R.raw.start)
    }

    private fun initSpeakerSpecial() {
        speakerCountDown["COUNTDOWN_1"] = false
        speakerCountDown["COUNTDOWN_2"] = false
        speakerCountDown["COUNTDOWN_3"] = false
        speakerCountDown["COUNTDOWN_4"] = false
        speakerCountDown["COUNTDOWN_5"] = false
        speakerCountDown["START"] = false
    }

    private fun readTTSTextFile() {
        pilotTexts.putAll(
            OpenerCSV.readData(
                R.raw.spotter_tts,
                Settings.languageTtsEnum
            )
        )
    }

    private fun drawCoordinates(canvas: Canvas) {
        if (MovementManager.getOrientation() != null && MovementManager.getStartOrientation() != null) {
            val test1 =
                MovementManager.getOrientation()!![0].toDouble() - MovementManager.getStartOrientation()!![0].toDouble()
            val test2 =
                MovementManager.getOrientation()!![1].toDouble() - MovementManager.getStartOrientation()!![1].toDouble()
            val test3 =
                MovementManager.getOrientation()!![2].toDouble() - MovementManager.getStartOrientation()!![2].toDouble()
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

        if(weather == WeatherEnum.SUN){
            TextToSpeechManager.speakNow(pilotTexts["WEATHER_SUNNY"].toString())
        }
        else if(weather == WeatherEnum.RAIN){
            TextToSpeechManager.speakNow(pilotTexts["WEATHER_RAIN"].toString())
        }
        else if(weather == WeatherEnum.WIND){
            TextToSpeechManager.speakNow(pilotTexts["WEATHER_WIND"].toString())
        }

        TextToSpeechManager.speakQueue(pilotTexts["INSTRUCTION"].toString())
    }

    private fun checkCollistion() {
        if (trackData != null) {

            for (left in trackData!!.roadList?.get(trackIterator)?.leftPoints!!) {
                if (car.collisionCheck(
                        left.startX.toFloat(),
                        left.startY.toFloat(),
                        left.endX.toFloat(),
                        left.endY.toFloat(),
                        true
                    )
                ) {
                    car.posX += 10

                    if(car.getCarHealth() % 10 == 0){
                        TextToSpeechManager.speakNow(pilotTexts["CAR_DEMAGED"].toString() + " " + car.getCarHealth() + "%")
                    }
                }
            }

            for (right in trackData!!.roadList?.get(trackIterator)?.rightPints!!) {
                if (car.collisionCheck(
                        right.startX.toFloat(),
                        right.startY.toFloat(),
                        right.endX.toFloat(),
                        right.endY.toFloat(),
                        false
                    )
                ) {
                    car.posX -= 10

                    if(car.getCarHealth() % 10 == 0){
                        TextToSpeechManager.speakNow(pilotTexts["CAR_DEMAGED"].toString() + " " + car.getCarHealth() + "%")
                    }
                }
            }
        }
    }

    private fun checkDistance() {
        if (trackData != null) {

            leftAlertRadar = false
            rightAlertRadar = false

            leftSensor@ for (left in trackData!!.roadList?.get(trackIterator)?.leftPoints!!) {
                if (car.sensorCheck(
                        left.startX.toFloat(),
                        left.startY.toFloat(),
                        left.endX.toFloat(),
                        left.endY.toFloat(),
                    )
                ) {
                    leftAlertRadar = true
                    break@leftSensor
                }
            }

            rightSensor@ for (right in trackData!!.roadList?.get(trackIterator)?.rightPints!!) {
                if (car.sensorCheck(
                        right.startX.toFloat(),
                        right.startY.toFloat(),
                        right.endX.toFloat(),
                        right.endY.toFloat(),
                    )
                ) {
                    rightAlertRadar = true
                    break@rightSensor
                }
            }
        }
    }

    private fun countDown() {
        durationCount += System.currentTimeMillis() - startCountTime - durationCount

        disableTap = true

        if (durationCount > 1000 && speakerCountDown["COUNTDOWN_5"] == false) {
            TextToSpeechManager.speakNow(pilotTexts["COUNTDOWN_5"].toString())
            soundManagerGame.playSound(R.raw.countdown, 0.6F, 0.6F)
            speakerCountDown["COUNTDOWN_5"] = true
            countDownText="5"


        } else if (durationCount > 2000 && speakerCountDown["COUNTDOWN_4"] == false) {
            TextToSpeechManager.speakNow(pilotTexts["COUNTDOWN_4"].toString())
            soundManagerGame.playSound(R.raw.countdown, 0.6F, 0.6F)
            speakerCountDown["COUNTDOWN_4"] = true
            countDownText="4"
        } else if (durationCount > 3000 && speakerCountDown["COUNTDOWN_3"] == false) {
            TextToSpeechManager.speakNow(pilotTexts["COUNTDOWN_3"].toString())
            soundManagerGame.playSound(R.raw.countdown, 0.6F, 0.6F)
            speakerCountDown["COUNTDOWN_3"] = true
            countDownText="3"
        } else if (durationCount > 4000 && speakerCountDown["COUNTDOWN_2"] == false) {
            TextToSpeechManager.speakNow(pilotTexts["COUNTDOWN_2"].toString())
            soundManagerGame.playSound(R.raw.countdown, 0.6F, 0.6F)
            speakerCountDown["COUNTDOWN_2"] = true
            countDownText="2"
        } else if (durationCount > 5000 && speakerCountDown["COUNTDOWN_1"] == false) {
            TextToSpeechManager.speakNow(pilotTexts["COUNTDOWN_1"].toString())
            soundManagerGame.playSound(R.raw.countdown, 0.6F, 0.6F)
            speakerCountDown["COUNTDOWN_1"] = true
            countDownText="1"
        } else if (durationCount > MAX_DURATION_COUNT && speakerCountDown["START"] == false) {
            TextToSpeechManager.speakNow(pilotTexts["START"].toString())
            soundManagerGame.playSound(R.raw.start, 0.6F, 0.6F)
            newRoadTile = true
            speakerCountDown["START"] = true
            countDownText="Start!"
        }

        if (durationCount > MAX_DURATION_COUNT + 1000) {
            stopGameplay = true
            countDown = false
        }
    }

    private fun updateStageResult() {
        stageResult.carDamage = car.getCarHealth()
        stageResult.time = raceTimeSeconds
        stageResult.score = (MAX_SCORE * car.getCarHealth() * 0.01F / raceTimeSeconds).toInt()
    }

    override fun updateState() {

        if (countDown) {
            countDown()
        }

        if (!stopGameplay) {

            if (!TextToSpeechManager.isSpeaking()) {
                idleTime++

                if (idleTime % 30 == 0) {
                    idleTimeSeconds++
                }
            }

            if (idleTimeSeconds > 10) {

                TextToSpeechManager.speakNow(pilotTexts["INSTRUCTION"].toString())

                idleTimeSeconds = 0
            }

            return
        }

        raceTime++
        if (raceTime % 60 == 0) {
            raceTimeSeconds++
            updateStageResult()
        }

        car.update(coordinateDisplayManager)
        coordinateDisplayManager.updateEnvironmentCoordinates()

        checkCollistion()
        checkDistance()

        if(car.getCarHealth() <= 0){
            TextToSpeechManager.stop()
            car.destroyCar()
            if (Looper.myLooper() == null) {
                Looper.prepare()
            }

            LevelManager.changeLevel(CarDestroyedScene())
        }

        if (trackData != null) {

            if (trackIterator >= trackData!!.roadList.size - 1) {

                if (Looper.myLooper() == null) {
                    Looper.prepare()
                }
                car.destroyCar()
                if (GameOptions.gamemode == RacingModeEnum.TOURNAMENT_MODE) {

                    LevelManager.changeLevel(TournamentStageOverScene(stageResult))
                } else {
                    LevelManager.changeLevel(RaceOverScene(stageResult))
                }
            }

            if (trackData!!.roadList[trackIterator].finishY < car.posY) {
                trackIterator++

                newRoadTile = true

                car.posX = trackData!!.roadList?.get(trackIterator)?.spawnX!!.toFloat()
                car.posY = trackData!!.roadList?.get(trackIterator)?.spawnY!!.toFloat()
            }

            if (newRoadTile) {
                pilotQuoteText = ""
                for (tts in trackData!!.roadList[trackIterator].speakerKeys) {
                    TextToSpeechManager.speakQueue(pilotTexts[tts].toString())
                    pilotQuoteText = pilotQuoteText + pilotTexts[tts].toString() + " "
                }
                newRoadTile = false
            }
        }


        if(weather == WeatherEnum.WIND){
            if (Mathematics.randInt(0, 500) == 1) {
                windFasterRight = !windFasterRight
            }

            if (windFasterRight) {

                windRight = windRight - 0.05F + Mathematics.randFloat(0.0F, 0.1F);

                if (windRight > 1F) {
                    windRight = 1F
                }

                if (windRight < 0F) {
                    windRight = 0F
                }

            } else {

                windLeft = windLeft - 0.05F + Mathematics.randFloat(0.0F, 0.1F);

                if (windLeft > 1F) {
                    windLeft = 1F
                }

                if (windLeft < 0F) {
                    windLeft = 0F
                }

            }


            if (windFasterRight) {
                car.pushCar(
                    0F,
                    windRight * 0.05F //* Settings.SCREEN_SCALE * 0.004F
                )

                MediaPlayerManager.changeVolume(0F, windRight)
            } else {
                car.pushCar(
                    windLeft * 0.05F, //* Settings.SCREEN_SCALE * 0.004F,
                    0F
                )
                MediaPlayerManager.changeVolume(windLeft, 0F)
            }
        }



    }

    override fun destroyState() {
        isFocusable = false

        car.destroyCar()
        soundManagerGame.destroy()
        MediaPlayerManager.stopSound()
    }

    override fun respondTouchState(event: MotionEvent) {
        swipe = false

        when (GestureManager.swipeDetect(event)) {
            GestureTypeEnum.SWIPE_UP -> {
                Settings.globalSounds.playSound(RawResources.swapSound)
                pauseGame = true
                LevelManager.stackLevel(PauseScene())
                swipe = true
            }
        }

        if (stopGameplay) {
            when (GestureManager.tapPositionDetect(event)) {
                GestureTypeEnum.TAP_RIGHT -> car.higherGear()
                GestureTypeEnum.TAP_LEFT -> car.lowerGear()
            }

        }

        if (!disableTap) {
            when (GestureManager.doubleTapDetect(event)) {
                GestureTypeEnum.DOUBLE_TAP -> {
                    TextToSpeechManager.stop()
                    Settings.globalSounds.playSound(RawResources.acceptSound)
                    startGame()
                }
            }
        }
    }

    private fun startGame() {
        startCountTime = System.currentTimeMillis()
        countDown = true
    }

    override fun redrawState(canvas: Canvas) {

        if(!stopGameplay && !countDown){
            instructionText.drawMultilineText(canvas)
        }

        if(countDown){
            countDownTextObject.drawText(canvas, countDownText)
        }

        if(stopGameplay){
            speekerTextObject.drawText(canvas, pilotQuoteText)

            if(leftAlertRadar){
                leftRadar.drawText(canvas, "!")
            }

            if(rightAlertRadar){
                rightRadar.drawText(canvas, "!")
            }
        }



        /*
        canvas.translate(0F, canvas.height.toFloat());   // reset where 0,0 is located
        canvas.scale(1F, -1F);    // invert

        drawCoordinates(canvas)
        car.draw(canvas, coordinateDisplayManager)

        var paint = Paint()
        paint.strokeWidth =  1F
        paint.color = Color.WHITE

        if (trackData != null) {

            for (left in trackData!!.roadList?.get(trackIterator)?.leftPoints!!) {
                canvas.drawLine(
                    coordinateDisplayManager.convertToEnvironmentX(left.startX.toFloat()),
                    coordinateDisplayManager.convertToEnvironmentY(left.startY.toFloat()),
                    coordinateDisplayManager.convertToEnvironmentX(left.endX.toFloat()),
                    coordinateDisplayManager.convertToEnvironmentY(left.endY.toFloat()),
                    paint
                )
            }

            for (right in trackData!!.roadList?.get(trackIterator)?.rightPints!!) {
                canvas.drawLine(
                    coordinateDisplayManager.convertToEnvironmentX(right.startX.toFloat()),
                    coordinateDisplayManager.convertToEnvironmentY(right.startY.toFloat()),
                    coordinateDisplayManager.convertToEnvironmentX(right.endX.toFloat()),
                    coordinateDisplayManager.convertToEnvironmentY(right.endY.toFloat()),
                    paint
                )
            }
        }

        paint.textSize = 50F
        canvas.drawText(car.getCarSpeed().toString(), 500F, 500F, paint)

        canvas.drawText(this.raceTimeSeconds.toString(), 500F, 800F, paint)

        canvas.drawText(windLeft.toString(), 500F, 900F, paint)

        canvas.drawText(windRight.toString(), 900F, 900F, paint)

         */
    }
}