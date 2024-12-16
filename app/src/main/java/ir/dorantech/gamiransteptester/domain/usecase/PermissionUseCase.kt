package ir.dorantech.gamiransteptester.domain.usecase

interface PermissionUseCase {
    fun checkPermissionsGranted(permissions: List<String>): Boolean
}
