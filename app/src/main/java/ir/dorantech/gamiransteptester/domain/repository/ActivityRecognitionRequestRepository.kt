package ir.dorantech.gamiransteptester.domain.repository

import android.app.PendingIntent
import com.google.android.gms.location.ActivityTransitionRequest
import com.google.android.gms.tasks.Task

interface ActivityRecognitionRequestRepository {
    fun requestActivityTransitionUpdates(
        request: ActivityTransitionRequest,
        myPendingIntent: PendingIntent): Task<Void>
}
