package ir.dorantech.gamiransteptester.domain.usecase

import ir.dorantech.gamiransteptester.core.model.Permission

interface PermissionUseCase {
    fun checkPermissionsGranted(permissions: Array<Permission>): Boolean
}
