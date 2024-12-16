package ir.dorantech.gamiransteptester.ui.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.dorantech.gamiransteptester.domain.usecase.PermissionUseCase
import javax.inject.Inject

@HiltViewModel
class NavHostViewmodel @Inject constructor(
    private val permissionUseCase: PermissionUseCase,
): ViewModel() {
    fun checkPermissionsGranted(permissions: List<String>): Boolean {
        return permissionUseCase.checkPermissionsGranted(permissions)
    }
}