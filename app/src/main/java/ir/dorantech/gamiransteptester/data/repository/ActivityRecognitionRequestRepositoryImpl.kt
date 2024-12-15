package ir.dorantech.gamiransteptester.data.repository

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import com.google.android.gms.location.ActivityRecognition
import com.google.android.gms.location.ActivityTransitionRequest
import dagger.hilt.android.qualifiers.ApplicationContext
import ir.dorantech.gamiransteptester.domain.repository.ActivityRecognitionRequestRepository
import javax.inject.Inject

@SuppressLint("MissingPermission")
class ActivityRecognitionRequestRepositoryImpl @Inject constructor(
    @ApplicationContext val context: Context
) : ActivityRecognitionRequestRepository {
    override fun requestActivityTransitionUpdates(
        request: ActivityTransitionRequest,
        myPendingIntent: PendingIntent
    ) = ActivityRecognition
        .getClient(context)
        .requestActivityTransitionUpdates(request, myPendingIntent)
}
