package salicki.pawel.blindcarrally.scenes

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.os.Looper
import android.util.Log
import android.view.MotionEvent
import android.view.SurfaceView
import androidx.annotation.RawRes
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
    private var pilotTexts: HashMap<String, String> = HashMap()
    private var speakerCountDown: HashMap<String, Boolean> = HashMap()

    private var instructionText: TextObject = TextObject()
    private var countDownTextObject: TextObject = TextObject()
    private var speekerTextObject: TextObject = TextObject()
    private var leftRadar: TextObject = TextObject()
    private var rightRadar: TextObject = TextObject()
    private var idleSpeak: IdleSpeakManager = IdleSpeakManager()
    private var soundManagerGame: SoundManager =
        SoundManager()
    private var stageResult: StageResultData = StageResultData()
    private var trackReader =
        TrackReader()

    private var weather: WeatherEnum = WeatherEnum.SUN

    private var raceTime = 0
    private var raceTimeSeconds = 0
    private var trackIterator = 0
    private var startCountTime: Long = 0
    private var durationCount: Long = 0
    private val MAX_DURATION_COUNT = 6000

    private var windLeft: Float = 0F
    private var windRight: Float = 0F

    private var pilotQuoteText: String = ""
    private var countDownText: String = ""

    private var stopGameplay: Boolean = false
    private var countDown: Boolean = false
    private var newRoadTile = false
    private var swipe = false
    private var pauseGame = false
    private var leftAlertRadar: Boolean = false
    private var rightAlertRadar: Boolean = false
    private var windFasterRight: Boolean = true
    private var disableTap: Boolean = false

    private var coordinateDisplayManager: CoordinateDisplayManager
    private var trackData: TrackData? = null

    private var car: Car =
        Car(
            Settings.SCREEN_WIDTH / 2F,
            Settings.SCREEN_HEIGHT / 2F,
            RectF(
                Settings.SCREEN_WIDTH / 2F,
                Settings.SCREEN_HEIGHT / 2F,
                Settings.SCREEN_WIDTH / 2F + 10F,
                Settings.SCREEN_HEIGHT / 2F + 5F
            )
        )

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
            MediaPlayerManager.initMediaPlayer(RawResources.hurricaneSound)
            MediaPlayerManager.changeVolume(0.1F, 0.1F)
            MediaPlayerManager.loopSound()
            MediaPlayerManager.startSound()
        }
        else if(weather == WeatherEnum.RAIN){
            MediaPlayerManager.initMediaPlayer(RawResources.rainSound)
            MediaPlayerManager.changeVolume(0.3F, 0.3F)
            MediaPlayerManager.loopSound()
            MediaPlayerManager.startSound()
        }

        instructionText.initMultiLineText(
            R.font.montserrat, R.dimen.informationSizeSmall,
            Settings.SCREEN_WIDTH / 2F,
            Settings.SCREEN_HEIGHT / 20F,
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

        soundManagerGame.addSound(RawResources.countdownSound)
        soundManagerGame.addSound(RawResources.startSound)
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
              RawResources.spotter_TSS,
                Settings.languageTtsEnum
            )
        )
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

        idleSpeak.initIdleString(pilotTexts["INSTRUCTION"].toString())
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
                    car.posX += 15

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
                    car.posX -= 15

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
            soundManagerGame.playSound(RawResources.countdownSound, 0.6F, 0.6F)
            speakerCountDown["COUNTDOWN_5"] = true
            countDownText="5"


        } else if (durationCount > 2000 && speakerCountDown["COUNTDOWN_4"] == false) {
            TextToSpeechManager.speakNow(pilotTexts["COUNTDOWN_4"].toString())
            soundManagerGame.playSound(RawResources.countdownSound, 0.6F, 0.6F)
            speakerCountDown["COUNTDOWN_4"] = true
            countDownText="4"
        } else if (durationCount > 3000 && speakerCountDown["COUNTDOWN_3"] == false) {
            TextToSpeechManager.speakNow(pilotTexts["COUNTDOWN_3"].toString())
            soundManagerGame.playSound(RawResources.countdownSound, 0.6F, 0.6F)
            speakerCountDown["COUNTDOWN_3"] = true
            countDownText="3"
        } else if (durationCount > 4000 && speakerCountDown["COUNTDOWN_2"] == false) {
            TextToSpeechManager.speakNow(pilotTexts["COUNTDOWN_2"].toString())
            soundManagerGame.playSound(RawResources.countdownSound, 0.6F, 0.6F)
            speakerCountDown["COUNTDOWN_2"] = true
            countDownText="2"
        } else if (durationCount > 5000 && speakerCountDown["COUNTDOWN_1"] == false) {
            TextToSpeechManager.speakNow(pilotTexts["COUNTDOWN_1"].toString())
            soundManagerGame.playSound(RawResources.countdownSound, 0.6F, 0.6F)
            speakerCountDown["COUNTDOWN_1"] = true
            countDownText="1"
        } else if (durationCount > MAX_DURATION_COUNT && speakerCountDown["START"] == false) {
            TextToSpeechManager.speakNow(pilotTexts["START"].toString())
            soundManagerGame.playSound(RawResources.countdownSound, 0.6F, 0.6F)
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
    }

    override fun updateState() {
        if(!MediaPlayerManager.isPlaying()){
            MediaPlayerManager.startSound()
        }

        if (countDown) {
            countDown()
        }

        if (!stopGameplay) {

            idleSpeak.updateIdleStatus()

            return
        }

        raceTime++
        if (raceTime % (Settings.FPS * 2) == 0) {
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
                    windRight * 0.075F //* Settings.SCREEN_SCALE * 0.004F
                )

                MediaPlayerManager.changeVolume(0F, windRight)
            } else {
                car.pushCar(
                    windLeft * 0.075F, //* Settings.SCREEN_SCALE * 0.004F,
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
                MediaPlayerManager.stopSound()
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
    }
}