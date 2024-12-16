package ir.dorantech.gamiransteptester.domain.usecase

import android.content.Intent
import ir.dorantech.gamiransteptester.domain.model.RecognitionEvent
import ir.dorantech.gamiransteptester.domain.repository.ActivityRecognitionRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class ActivityRecognitionUseCase @Inject constructor(
    private val repository: ActivityRecognitionRepository,
) {
    private var _eventsFlow = MutableStateFlow<RecognitionEvent?>(null)
    val eventsFlow: MutableSharedFlow<RecognitionEvent?> = _eventsFlow

    companion object {
        const val INTENT_ACTION =
            "ir.dorantech.gamiransteptester.ACTION_PROCESS_ACTIVITY_TRANSITIONS"
    }

    fun handleActivityRecognitionEvent(intentData: Intent) {
        CoroutineScope(Dispatchers.IO).launch{
            repository.processRecognitionIntent(intentData).collect{
                _eventsFlow.value = it
            }
        }
    }
}
