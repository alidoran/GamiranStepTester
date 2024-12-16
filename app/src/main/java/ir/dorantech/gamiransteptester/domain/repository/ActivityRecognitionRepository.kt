package ir.dorantech.gamiransteptester.domain.repository

import ir.dorantech.gamiransteptester.domain.model.RecognitionEvent
import android.content.Intent
import kotlinx.coroutines.flow.StateFlow

interface ActivityRecognitionRepository {
    fun processRecognitionIntent(intent: Intent): StateFlow<RecognitionEvent?>
}
