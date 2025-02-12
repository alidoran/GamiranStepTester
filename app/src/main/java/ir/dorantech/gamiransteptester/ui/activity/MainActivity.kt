package ir.dorantech.gamiransteptester.ui.activity

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import dagger.hilt.android.AndroidEntryPoint
import ir.dorantech.gamiransteptester.core.logging.LogManager
import ir.dorantech.gamiransteptester.core.model.Permission
import ir.dorantech.gamiransteptester.core.navigation.NavHostSetup
import ir.dorantech.gamiransteptester.domain.usecase.ActivityRecognitionUseCase
import ir.dorantech.gamiransteptester.ui.theme.GamiranStepTesterTheme
import ir.dorantech.gamiransteptester.ui.viewmodel.MainActivityViewModel
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var logManager: LogManager

    private val mainActivityViewModel: MainActivityViewModel by viewModels()
    private val registerForActivityResult = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissionsResult ->
        val allPermissionsGranted = permissionsResult.all { it.value }
        if (allPermissionsGranted) {
            mainActivityViewModel.addLogToList("All permissions granted")
        } else {
            mainActivityViewModel.addLogToList("Permission denied")
            Toast.makeText(this, "Permission denied", Toast.LENGTH_LONG).show()
        }
    }

    @Inject
    lateinit var recognitionUseCase: ActivityRecognitionUseCase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GamiranStepTesterTheme {
                InitView()
            }
        }
    }

    @Composable
    fun InitView() {
        mainActivityViewModel.addLogToList("Start Application")
        val logs by remember { mutableStateOf(LogManager.logs) }

        Column {
            NavHostSetup(
                modifier = Modifier
                    .weight(3f)
                    .fillMaxSize(),
                onRequestPermission = { onRequestPermissions(it) },
            )

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
            ) {
                items(logs) { log ->
                    Text(text = log, fontSize = 12.sp)
                }
            }
        }
    }

    private fun onRequestPermissions(permissionList: Array<Permission>) {
        val permissions = permissionList.map { it.androidName }.toTypedArray()
        registerForActivityResult.launch(permissions)
    }
}