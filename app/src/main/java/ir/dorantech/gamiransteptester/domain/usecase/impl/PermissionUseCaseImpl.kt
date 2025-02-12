package ir.dorantech.gamiransteptester.domain.usecase.impl

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import ir.dorantech.gamiransteptester.core.model.Permission
import ir.dorantech.gamiransteptester.domain.usecase.PermissionUseCase
import javax.inject.Inject

class PermissionUseCaseImpl @Inject constructor(
    @ApplicationContext val context: Context
) : PermissionUseCase {
    override fun checkPermissionsGranted(permissions: Array<Permission>): Boolean {
        return permissions.all { permission ->
            ContextCompat.checkSelfPermission(context, permission.androidName) ==
                    PackageManager.PERMISSION_GRANTED
        }
    }
}