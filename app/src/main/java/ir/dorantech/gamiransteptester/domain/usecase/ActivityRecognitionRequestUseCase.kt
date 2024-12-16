package ir.dorantech.gamiransteptester.domain.usecase

import android.app.PendingIntent
import ir.dorantech.gamiransteptester.domain.model.RecognitionResult
import kotlinx.coroutines.flow.Flow

interface ActivityRecognitionRequestUseCase {
    operator fun invoke(
        pendingIntent: PendingIntent,
    ): Flow<RecognitionResult>
}