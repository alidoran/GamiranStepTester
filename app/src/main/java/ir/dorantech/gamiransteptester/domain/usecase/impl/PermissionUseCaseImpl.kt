package ir.dorantech.gamiransteptester.domain.usecase.impl

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import ir.dorantech.gamiransteptester.domain.usecase.PermissionUseCase
import javax.inject.Inject

class PermissionUseCaseImpl @Inject constructor(
    @ApplicationContext val applicationContext: Context
) : PermissionUseCase {
    override fun checkPermissionsGranted(permissions: List<String>): Boolean {
        return permissions.all { permission ->
            ContextCompat.checkSelfPermission(applicationContext, permission) == PackageManager.PERMISSION_GRANTED
        }
    }
}