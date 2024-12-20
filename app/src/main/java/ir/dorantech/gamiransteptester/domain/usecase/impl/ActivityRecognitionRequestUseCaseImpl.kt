package ir.dorantech.gamiransteptester.domain.usecase.impl

import android.app.PendingIntent
import android.util.Log
import com.google.android.gms.location.ActivityTransition
import com.google.android.gms.location.ActivityTransition.ACTIVITY_TRANSITION_ENTER
import com.google.android.gms.location.ActivityTransition.ACTIVITY_TRANSITION_EXIT
import com.google.android.gms.location.ActivityTransitionRequest
import com.google.android.gms.location.DetectedActivity.*
import ir.dorantech.gamiransteptester.domain.model.ActivityTransitionRequestModel
import ir.dorantech.gamiransteptester.domain.model.RecognitionResult
import ir.dorantech.gamiransteptester.domain.repository.ActivityRecognitionRequestRepository
import ir.dorantech.gamiransteptester.domain.usecase.ActivityRecognitionRequestUseCase
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class ActivityRecognitionRequestUseCaseImpl @Inject constructor(
    private val repository: ActivityRecognitionRequestRepository
) : ActivityRecognitionRequestUseCase {

    override operator fun invoke(
        pendingIntent: PendingIntent,
    ): Flow<RecognitionResult> {
        return callbackFlow {
            val task = repository.requestActivityTransitionUpdates(
                pendingIntent
            )
            task.addOnSuccessListener {
                trySend(RecognitionResult.Success)
            }.addOnFailureListener { e: Exception ->
                Log.e("ActivityRecognition", "Error occurred: ${e.message}")
                trySend(RecognitionResult.Error(e.message ?: "Unknown error"))
            }

            awaitClose {
            }
        }
    }

    private fun getActivityTransitionRequestList(): List<ActivityTransitionRequestModel> = listOf(
        ActivityTransitionRequestModel(
            detectedActivity = IN_VEHICLE,
            activityTransition = ACTIVITY_TRANSITION_ENTER
        ),
        ActivityTransitionRequestModel(
            detectedActivity = IN_VEHICLE,
            activityTransition = ACTIVITY_TRANSITION_EXIT
        ),
        ActivityTransitionRequestModel(
            detectedActivity = WALKING,
            activityTransition = ACTIVITY_TRANSITION_ENTER
        ),
        ActivityTransitionRequestModel(
            detectedActivity = WALKING,
            activityTransition = ACTIVITY_TRANSITION_EXIT
        ),
        ActivityTransitionRequestModel(
            detectedActivity = RUNNING,
            activityTransition = ACTIVITY_TRANSITION_ENTER
        ),
        ActivityTransitionRequestModel(
            detectedActivity = RUNNING,
            activityTransition = ACTIVITY_TRANSITION_EXIT
        ),
        ActivityTransitionRequestModel(
            detectedActivity = ON_FOOT,
            activityTransition = ACTIVITY_TRANSITION_ENTER
        ),
        ActivityTransitionRequestModel(
            detectedActivity = ON_FOOT,
            activityTransition = ACTIVITY_TRANSITION_EXIT
        ),
        ActivityTransitionRequestModel(
            detectedActivity = ON_BICYCLE,
            activityTransition = ACTIVITY_TRANSITION_ENTER
        ),
        ActivityTransitionRequestModel(
            detectedActivity = ON_BICYCLE,
            activityTransition = ACTIVITY_TRANSITION_EXIT
        ),
        ActivityTransitionRequestModel(
            detectedActivity = STILL,
            activityTransition = ACTIVITY_TRANSITION_ENTER
        ),
        ActivityTransitionRequestModel(
            detectedActivity = STILL,
            activityTransition = ACTIVITY_TRANSITION_EXIT
        ),
    )

    private fun getTransitions(): List<ActivityTransition> =
        getActivityTransitionRequestList().map { request ->
            ActivityTransition.Builder()
                .setActivityType(request.detectedActivity)
                .setActivityTransition(request.activityTransition)
                .build()
        }


    // TODO: Creating pendingIntent with request
    private fun createActivityTransitionRequest() = ActivityTransitionRequest(getTransitions())
}