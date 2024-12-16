package ir.dorantech.gamiransteptester.core.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import dagger.hilt.android.EntryPointAccessors
import ir.dorantech.gamiransteptester.domain.di.ActivityRecognitionReceiverEntryPoint

class ActivityRecognitionReceiver: BroadcastReceiver(){
    override fun onReceive(context: Context, intent: Intent) {
        val entryPoint = EntryPointAccessors.fromApplication(
            context.applicationContext,
            ActivityRecognitionReceiverEntryPoint::class.java
        )
        val useCase = entryPoint.getActivityRecognitionUseCase()
        useCase.handleActivityRecognitionEvent(intent)
    }
}