package ir.dorantech.gamiransteptester.core.broadcast.model

sealed class DetectActivityResult(){
    data class InVehicle(val confidence: Int): DetectActivityResult()
    data class OnBicycle(val confidence: Int): DetectActivityResult()
    data class OnFoot(val confidence: Int): DetectActivityResult()
    data class Running(val confidence: Int): DetectActivityResult()
    data class Walking(val confidence: Int): DetectActivityResult()
    data class Still(val confidence: Int): DetectActivityResult()
    data class Tilting(val confidence: Int): DetectActivityResult()
    data class Unknown(val confidence: Int): DetectActivityResult()
}
