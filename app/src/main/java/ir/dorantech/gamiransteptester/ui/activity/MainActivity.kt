package ir.dorantech.gamiransteptester.ui.activity

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.ui.Modifier
import dagger.hilt.android.AndroidEntryPoint
import ir.dorantech.gamiransteptester.navigation.NavHostSetup
import ir.dorantech.gamiransteptester.ui.theme.GamiranStepTesterTheme
import ir.dorantech.gamiransteptester.viewmodel.StepTesterViewModel

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val vm : StepTesterViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        val registerForActivityResult = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                vm.onPermissionResult(true)
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_LONG).show()
            }
        }

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GamiranStepTesterTheme {
                Column {
                    NavHostSetup(
                        modifier = Modifier,
                        onRequestPermission = {
                            registerForActivityResult.launch(android.Manifest.permission.ACTIVITY_RECOGNITION)
                        }
                    )
                }
            }
        }
    }
}