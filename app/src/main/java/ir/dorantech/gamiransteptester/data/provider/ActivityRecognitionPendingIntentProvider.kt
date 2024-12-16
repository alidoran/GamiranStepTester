package ir.dorantech.gamiransteptester.data.provider

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import dagger.hilt.android.qualifiers.ApplicationContext
import ir.dorantech.gamiransteptester.core.broadcast.ActivityRecognitionReceiver
import ir.dorantech.gamiransteptester.domain.usecase.ActivityRecognitionUseCase
import javax.inject.Inject

class ActivityRecognitionPendingIntentProvider @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    fun getPendingIntent(): PendingIntent {
        val intent = Intent(context, ActivityRecognitionReceiver::class.java).apply {
            action = ActivityRecognitionUseCase.INTENT_ACTION
        }
        return PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}