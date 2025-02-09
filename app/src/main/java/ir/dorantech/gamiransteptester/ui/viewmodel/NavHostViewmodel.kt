package ir.dorantech.gamiransteptester.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import ir.dorantech.gamiransteptester.domain.usecase.PermissionUseCase
import javax.inject.Inject

@HiltViewModel
class NavHostViewmodel @Inject constructor(
    private val permissionUseCase: PermissionUseCase,
    @ApplicationContext val context: Context,
): ViewModel() {
    fun checkPermissionsGranted(permissions: Array<String>): Boolean {
        return permissionUseCase.checkPermissionsGranted(permissions)
    }
}