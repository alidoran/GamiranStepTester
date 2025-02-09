package ir.dorantech.gamiransteptester.domain.usecase

interface PermissionUseCase {
    fun checkPermissionsGranted(permissions: Array<String>): Boolean
}
