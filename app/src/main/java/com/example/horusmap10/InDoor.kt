package com.example.horusmap10

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.example.horusmap10.R.*
import kotlinx.android.synthetic.main.activity_in_door.*
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.alert
import timber.log.Timber

private const val ENABLE_BLUETOOTH_REQUEST_CODE = 1
private const val LOCATION_PERMISSION_REQUEST_CODE = 2

class InDoor : AppCompatActivity() {

    /*******************************************
     * Properties
     *******************************************/

    private val bluetoothAdapter: BluetoothAdapter by lazy {
        val bluetoothManager = getSystemService(BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothManager.adapter
    }

    private val bleScanner by lazy {
        bluetoothAdapter.bluetoothLeScanner
    }

    private val scanSettings = ScanSettings.Builder()
        .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
        .build()

    /*private var isScanning = false
        set(value) {
            field = value
            runOnUiThread { logout_button2.text = if (value) "Stop Scan" else "Start Scan" }
        }*/

    private val isLocationPermissionGranted
        get() = hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)

    private val scanResults = mutableListOf<ScanResult>()
    private val scanResultAdapter: ScanResultAdapter by lazy {
        ScanResultAdapter(scanResults) {
            // TODO: Implement
        }
    }
    /*******************************************
     * Activity function overrides
     *******************************************/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout.activity_in_door)
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        //scan_button.setOnClickListener { if (isScanning) stopBleScan() else startBleScan() }
        logout_button2.setOnClickListener {
            val HOME = Intent(this, HomeActivity::class.java)
            startActivity(HOME)
            finish()
            stopBleScan()  }
        startBleScan()
        imageButton_back.setOnClickListener {
            val Routes = Intent(this, StartRouteActivity::class.java)
            startActivity(Routes)
            stopBleScan()
            finish()
        }
        setupRecyclerView()
    }

    override fun onResume() {
        super.onResume()
        if (!bluetoothAdapter.isEnabled) {
            promptEnableBluetooth()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            ENABLE_BLUETOOTH_REQUEST_CODE -> {
                if (resultCode != RESULT_OK) promptEnableBluetooth()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            LOCATION_PERMISSION_REQUEST_CODE -> {
                if (grantResults.firstOrNull() == PackageManager.PERMISSION_DENIED) {
                    requestLocationPermission()
                } else {
                    startBleScan()
                }
            }
        }
    }

    /*******************************************
     * Private functions
     *******************************************/

    private fun promptEnableBluetooth() {
        if (!bluetoothAdapter.isEnabled) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, ENABLE_BLUETOOTH_REQUEST_CODE)
        }
    }

    private fun startBleScan() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !isLocationPermissionGranted) {
            requestLocationPermission()
        } else {
            scanResults.clear()
            scanResultAdapter.notifyDataSetChanged()
            bleScanner.startScan(null, scanSettings, scanCallback)
        }
    }

    private fun stopBleScan() {
        bleScanner.stopScan(scanCallback)
        //isScanning = false
    }

    private fun requestLocationPermission() {
        if (isLocationPermissionGranted) {
            return
        }
        runOnUiThread {
            alert {
                title = "Location permission required"
                message = "Starting from Android M (6.0), the system requires apps to be granted " +
                        "location access in order to scan for BLE devices."
                isCancelable = false
                positiveButton(android.R.string.ok) {
                    requestPermission(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        LOCATION_PERMISSION_REQUEST_CODE
                    )
                }
            }.show()
        }
    }

    private fun setupRecyclerView() {
        scan_results_recycler_view.apply {
            adapter = scanResultAdapter
            layoutManager = LinearLayoutManager(
                this@InDoor,
                RecyclerView.VERTICAL,
                false
            )
            isNestedScrollingEnabled = false
        }

        val animator = scan_results_recycler_view.itemAnimator
        if (animator is SimpleItemAnimator) {
            animator.supportsChangeAnimations = false
        }
    }

    /*******************************************
     * Callback bodies
     *******************************************/

    private val scanCallback = object : ScanCallback() {
        @SuppressLint("LogNotTimber")
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            val indexQuery = scanResults.indexOfFirst { it.device.address == result.device.address }
            if (indexQuery != -1) { // A scan result already exists with the same address
                scanResults[indexQuery] = result
                scanResultAdapter.notifyItemChanged(indexQuery)
            } else {
                with(result.device) {
                    Log.i("ScanCallback", "Found BLE device! Name: ${name ?: "Unnamed"}, address: $address")
                }

                with(result.device){
                    if((address =="DC:0D:30:00:18:F2")||(address =="DC:0D:2F:AE:79:D5")){
                        scanResults.add(result)
                        scanResultAdapter.notifyItemInserted(scanResults.size - 1)
                        /*** MODIFICACIÓN*/
                        if(address =="DC:0D:30:00:18:F2"){
                            level_actual.text = getString(string.level3)
                            multi2.text = getString(string.inge3)
                            multi2.contentDescription =  getString(string.inge3)
                            Handler(Looper.getMainLooper()).postDelayed(
                                {

                                    startBleScan()
                                },
                                1000 // value in milliseconds
                            )

                        }else{
                            level_actual.text = getString(string.level4)
                            multi2.text = getString(string.inge4)
                            multi2.contentDescription = getString(string.inge4)
                            Handler(Looper.getMainLooper()).postDelayed(
                                {

                                    startBleScan()
                                },
                                1000 // value in milliseconds
                            )

                        }
                    }
                }

            }
        }

        @SuppressLint("LogNotTimber")
        override fun onScanFailed(errorCode: Int) {
            Log.e("ScanCallback", "onScanFailed: code $errorCode")
        }
    }

    /*******************************************
     * Extension functions
     *******************************************/

    private fun Context.hasPermission(permissionType: String): Boolean {
        return ContextCompat.checkSelfPermission(this, permissionType) ==
                PackageManager.PERMISSION_GRANTED
    }

    private fun Activity.requestPermission(permission: String, requestCode: Int) {
        ActivityCompat.requestPermissions(this, arrayOf(permission), requestCode)
    }
}
