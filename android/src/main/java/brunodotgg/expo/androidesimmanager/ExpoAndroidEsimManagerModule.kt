package brunodotgg.expo.androidesimmanager // Adjust package name if needed

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.telephony.euicc.DownloadableSubscription
import android.telephony.euicc.EuiccManager
import android.util.Log
import expo.modules.kotlin.Promise
import expo.modules.kotlin.modules.Module
import expo.modules.kotlin.modules.ModuleDefinition

// Define constants - IMPORTANT: Replace with your actual app package name
private const val YOUR_APP_PACKAGE_NAME = "sm.esim" // <-- CHANGE THIS
private const val BROADCAST_ACTION = "sm.esim.lpa.action.DOWNLOAD_SUBSCRIPTION"
private const val BROADCAST_PERMISSION = "sm.esim.lpa.permission.BROADCAST"

class ExpoAndroidEsimManagerModule : Module() {
    private var pendingPromise: Promise? = null
    private var installReceiver: BroadcastReceiver? = null

    override fun definition() = ModuleDefinition {
        Name("ExpoAndroidEsimManagerModule")

        // Expose isSupported function
        AsyncFunction("isSupported") { promise: Promise ->
            val euiccManager = getEuiccManager()
            if (euiccManager == null) {
                promise.resolve(false)
                return@AsyncFunction
            }
            promise.resolve(euiccManager.isEnabled)
        }

        // Expose install function
        AsyncFunction("install") { activationCode: String, promise: Promise ->
            val euiccManager = getEuiccManager()
            if (euiccManager == null || !euiccManager.isEnabled) {
                promise.reject(ERROR_CODE, "eSIM not supported or manager unavailable", null)
                return@AsyncFunction
            }

            // Ensure no previous operation is pending
            if (pendingPromise != null || installReceiver != null) {
                promise.reject(ERROR_CODE, "Another eSIM operation is already in progress.", null)
                return@AsyncFunction
            }
            pendingPromise = promise

            val context = appContext.reactContext ?: run {
                promise.reject(ERROR_CODE, "React context is unavailable", null)
                return@AsyncFunction
            }

            val explicitIntent = Intent(BROADCAST_ACTION).setPackage(context.packageName)
            val flags = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
            val callbackIntent = PendingIntent.getBroadcast(
                context,
                REQUESTED_CODE,
                explicitIntent,
                flags
            )

            installReceiver = object : BroadcastReceiver() {
                override fun onReceive(context: Context, intent: Intent) {
                    if (BROADCAST_ACTION != intent.action) {
                        Log.w(ERROR_CODE, "Received unexpected broadcast: ${intent.action}")
                        return // Ignore other broadcasts
                    }

                    val currentPromise = pendingPromise
                    pendingPromise = null // Consume the promise

                    when (resultCode) {
                        EuiccManager.EMBEDDED_SUBSCRIPTION_RESULT_OK -> {
                            Log.i(ERROR_CODE, "eSIM download successful")
                            currentPromise?.resolve("Success")
                        }
                        EuiccManager.EMBEDDED_SUBSCRIPTION_RESULT_RESOLVABLE_ERROR -> {
                            Log.i(ERROR_CODE, "eSIM download requires user resolution")
                            try {
                                // Re-arm the callback for the resolution activity result
                                pendingPromise = currentPromise
                                val resolutionIntent = intent // The intent received contains resolution data
                                euiccManager.startResolutionActivity(
                                    appContext.currentActivity, // Requires activity access
                                    REQUESTED_CODE,
                                    resolutionIntent,
                                    callbackIntent // Use the same callback intent
                                )
                                // The promise will be resolved/rejected when the broadcast is received *again* after resolution
                            } catch (e: Exception) {
                                Log.e(ERROR_CODE, "Failed to start resolution activity: ${e.message}", e)
                                pendingPromise = null // Clear promise if resolution fails to start
                                currentPromise?.reject(ERROR_CODE, "Failed to start resolution activity: ${e.message}", e)
                                unregisterReceiverSafely(this) // Clean up receiver
                                installReceiver = null
                            }
                        }
                        else -> {
                            Log.e(ERROR_CODE, "eSIM download failed with code: $resultCode")
                            currentPromise?.reject(ERROR_CODE, "eSIM download failed with code: $resultCode", null)
                            unregisterReceiverSafely(this) // Clean up receiver
                            installReceiver = null
                        }
                    }
                     // Only unregister if the flow is definitively finished (OK or unresolvable error)
                    if (resultCode != EuiccManager.EMBEDDED_SUBSCRIPTION_RESULT_RESOLVABLE_ERROR) {
                         unregisterReceiverSafely(this)
                         installReceiver = null
                    }
                }
            }

            try {
                // Register the receiver
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    context.registerReceiver(
                        installReceiver,
                        IntentFilter(BROADCAST_ACTION),
                        BROADCAST_PERMISSION, // Use the custom permission
                        null, // Scheduler handler
                        Context.RECEIVER_NOT_EXPORTED // Flag for non-exported receiver
                    )
                } else {
                    @Suppress("UnspecifiedRegisterReceiverFlag")
                    context.registerReceiver(
                        installReceiver,
                        IntentFilter(BROADCAST_ACTION),
                        BROADCAST_PERMISSION, // Use the custom permission
                        null // Scheduler handler
                    )
                }
                Log.d(ERROR_CODE, "Broadcast receiver registered for action: $BROADCAST_ACTION with permission: $BROADCAST_PERMISSION")

                // Start the download
                val subscription = DownloadableSubscription.forActivationCode(activationCode)
                euiccManager.downloadSubscription(subscription, true, callbackIntent)
                Log.i(ERROR_CODE, "eSIM download initiated for activation code: $activationCode")

            } catch (e: SecurityException) {
                 Log.e(ERROR_CODE, "Permission error: ${e.message}. Ensure BROADCAST_PERMISSION is declared and granted.", e)
                 promise.reject(ERROR_CODE, "Permission error: ${e.message}. Check Manifest declarations.", e)
                 cleanupFailedOperation()
            } catch (e: Exception) {
                Log.e(ERROR_CODE, "Failed to initiate eSIM download: ${e.message}", e)
                promise.reject(ERROR_CODE, "Failed to initiate eSIM download: ${e.message}", e)
                cleanupFailedOperation()
            }
        }

        // Clean up receiver when the module is destroyed
        OnDestroy {
            unregisterReceiverSafely(installReceiver)
            installReceiver = null
            pendingPromise = null // Clear any dangling promise
        }
    } // End of definition

    private fun getEuiccManager(): EuiccManager? {
        val context = appContext.reactContext ?: return null
        return context.getSystemService(Context.EUICC_SERVICE) as? EuiccManager
    }

     private fun unregisterReceiverSafely(receiver: BroadcastReceiver?) {
        if (receiver != null) {
            try {
                appContext.reactContext?.unregisterReceiver(receiver)
                 Log.d(ERROR_CODE, "Successfully unregistered receiver.")
            } catch (e: IllegalArgumentException) {
                // Receiver wasn't registered, ignore.
                Log.w(ERROR_CODE, "Receiver already unregistered or never registered.")
            } catch (e: Exception) {
                Log.e(ERROR_CODE, "Error unregistering receiver: ${e.message}", e)
            }
        }
    }

    private fun cleanupFailedOperation() {
        unregisterReceiverSafely(installReceiver)
        installReceiver = null
        pendingPromise = null // Ensure promise is cleared on failure
    }


    companion object {
        private const val ERROR_CODE = "ExpoAndroidEsimManagerModule"
        private const val REQUESTED_CODE = 1001 // Use a unique request code
    }
}
