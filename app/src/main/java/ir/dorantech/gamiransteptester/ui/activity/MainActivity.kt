package ir.dorantech.gamiransteptester.ui.activity

import android.os.Build
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import dagger.hilt.android.AndroidEntryPoint
import ir.dorantech.gamiransteptester.core.logging.LogManager
import ir.dorantech.gamiransteptester.core.navigation.NavHostSetup
import ir.dorantech.gamiransteptester.ui.theme.GamiranStepTesterTheme
import ir.dorantech.gamiransteptester.ui.viewmodel.StepTesterViewModel

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val vm: StepTesterViewModel by viewModels()
    private val registerForActivityResult = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissionsResult ->
        val allPermissionsGranted = permissionsResult.all { it.value }
        if (allPermissionsGranted) {
            vm.addLogToList("All permissions granted")
            vm.setPermissionResult(true)
        } else {
            vm.addLogToList("Permission denied")
            Toast.makeText(this, "Permission denied", Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GamiranStepTesterTheme {
                vm.addLogToList("Start Application")
                val logs by remember { mutableStateOf(LogManager.logs) }
                Column {
                    NavHostSetup(
                        modifier = Modifier
                            .weight(3f)
                            .fillMaxSize(),
                        onRequestPermission = { chosenScreen, permissionList ->
                            val runningQOrLater =
                                Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
                            vm.setChosenScreen(chosenScreen)
                            for (permission in permissionList) {
                                vm.addLogToList(
                                    "Requested permission: ${permission.substringAfterLast('.')}"
                                )
                            }
                            if (runningQOrLater)
                                registerForActivityResult.launch(permissionList)
                            else {
                                vm.setPermissionResult(true)
                                vm.addLogToList("All permissions granted")
                            }
                        }
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
        }
    }
}