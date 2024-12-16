package ir.dorantech.gamiransteptester.data.provider

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
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
        val flags =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
                PendingIntent.FLAG_ALLOW_UNSAFE_IMPLICIT_INTENT or PendingIntent.FLAG_MUTABLE
            else PendingIntent.FLAG_MUTABLE


        return PendingIntent.getBroadcast(
            context,
            0,
            intent,
            flags
        )
    }
}