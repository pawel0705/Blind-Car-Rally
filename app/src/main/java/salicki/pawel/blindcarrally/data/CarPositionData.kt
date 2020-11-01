package salicki.pawel.blindcarrally.data

data class CarCoordinates
 (
 var posX01: Float = 0F,
 var posY01: Float = 0F,
 var posX02: Float = 0F,
 var posY02: Float = 0F,
 var posX03: Float = 0F,
 var posY03: Float = 0F,
 var posX04: Float = 0F,
 var posY04: Float = 0F
) {}

data class CarPositionSensors(

 var sensorX01: Float = 0F,
 var sensorY01: Float = 0F,
 var sensorX02: Float = 0F,
 var sensorY02: Float = 0F,

 var sensorX03: Float = 0F,
 var sensorY03: Float = 0F,
 var sensorX04: Float = 0F,
 var sensorY04: Float = 0F,

 var sensorX05: Float = 0F,
 var sensorY05: Float = 0F,
 var sensorX06: Float = 0F,
 var sensorY06: Float = 0F,

 var sensorX07: Float = 0F,
 var sensorY07: Float = 0F,
 var sensorX08: Float = 0F,
 var sensorY08: Float = 0F,

 var sensorX09: Float = 0F,
 var sensorY09: Float = 0F,
 var sensorX10: Float = 0F,
 var sensorY10: Float = 0F,

 var sensorX11: Float = 0F,
 var sensorY11: Float = 0F,
 var sensorX12: Float = 0F,
 var sensorY12: Float = 0F,
) {}

data class SensorDetect(
 var left: Boolean = false,
 var right: Boolean = false,
 var leftLength: Float = Float.MAX_VALUE,
 var rightLength: Float = Float.MAX_VALUE
){}