package com.ioreactnativeciepid

import com.facebook.react.bridge.Arguments.createMap
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.WritableMap
import com.facebook.react.modules.core.RCTNativeAppEventEmitter
import it.ipzs.androidpidprovider.external.PidProviderConfig
import it.ipzs.androidpidprovider.external.PidProviderSdk
import it.ipzs.cieidsdk.common.Callback
import it.ipzs.cieidsdk.common.CieIDSdk
import it.ipzs.cieidsdk.event.Event
import android.content.Intent
import android.app.Activity
import android.net.Uri
import it.ipzs.cieidsdk.data.PidCieData
import org.json.JSONObject

class IoReactNativeCiePidModule(reactContext: ReactApplicationContext) :
  ReactContextBaseJavaModule(reactContext), Callback {

  private var ciePinAttemptsLeft: Int = 0

  override fun getName(): String {
    return NAME
  }

  /**
   * onSuccess is called when the CIE authentication is successfully completed.
   * @param[url] the form consent url
   */
  override fun onSuccess(url: String, pinCieData: PidCieData?) {
    val cieDataString = pinCieData?.toString() ?: ""
    val eventData = JSONObject()
    eventData.put("url", url)
    eventData.put("cieData", cieDataString)
    this.sendEvent(successChannel, eventData.toString())
  }

  /**
   * onError is called if some errors occurred during CIE authentication
   * @param[error] the error occurred
   */
  override fun onError(error: Throwable) {
    this.sendEvent(errorChannel, error.message ?: "generic error")
  }

  /**
   * onEvent is called if an event occurs
   */
  override fun onEvent(event: Event) {
    ciePinAttemptsLeft = event.attempts ?: ciePinAttemptsLeft
    this.sendEvent(eventChannel,event.event.toString())
  }

  private fun getWritableMap(eventValue: String): WritableMap {
    val writableMap = createMap()
    writableMap.putString("event", eventValue)
    writableMap.putInt("attemptsLeft", ciePinAttemptsLeft)
    return writableMap
  }

  private fun sendEvent(channel: String, eventValue: String) {
    reactApplicationContext
      .getJSModule(RCTNativeAppEventEmitter::class.java)
      .emit(channel, getWritableMap(eventValue))
  }


  @ReactMethod
  fun start(callback: com.facebook.react.bridge.Callback) {
    try {
      CieIDSdk.start(getCurrentActivity()!!, this)
      callback.invoke()
    } catch (e: RuntimeException) {
      callback.invoke(e.message)
    }
  }

  @ReactMethod
  fun isNFCEnabled(callback: com.facebook.react.bridge.Callback) {

    callback.invoke(CieIDSdk.isNFCEnabled(reactApplicationContext))
  }

  @ReactMethod
  fun hasNFCFeature(callback: com.facebook.react.bridge.Callback) {
    callback.invoke(CieIDSdk.hasFeatureNFC(reactApplicationContext))
  }

  @ReactMethod
  fun setPin(pin: String, callback: com.facebook.react.bridge.Callback) {
    try {
      CieIDSdk.pin = pin
      callback.invoke()
    } catch (e: IllegalArgumentException) {
      callback.invoke(e.message)
    }
  }

  @ReactMethod
  fun setAuthenticationUrl(url: String) {
    CieIDSdk.setUrl(url)
  }

  @ReactMethod
  fun startListeningNFC(callback: com.facebook.react.bridge.Callback) {
    try {
      CieIDSdk.startNFCListening(getCurrentActivity()!!)
      callback.invoke()
    } catch (e: RuntimeException) {
      callback.invoke(e.message)
    }
  }

  @ReactMethod
  fun stopListeningNFC(callback: com.facebook.react.bridge.Callback) {
    try {
      CieIDSdk.stopNFCListening(getCurrentActivity()!!)
      callback.invoke()
    } catch (e: RuntimeException) {
      callback.invoke(e.message)
    }
  }

  companion object {
    const val eventChannel: String = "onEvent"
    const val errorChannel: String = "onError"
    const val successChannel: String = "onSuccess"
    const val NAME = "IoReactNativeCiePid"
  }

  @ReactMethod
  fun openNFCSettings(callback: com.facebook.react.bridge.Callback) {
    val currentActivity = getCurrentActivity()!!
    if (currentActivity == null) {
      callback.invoke("fail to get current activity");
    } else {
      CieIDSdk.openNFCSettings(currentActivity)
      callback.invoke()
    }
  }

  @ReactMethod
  fun hasApiLevelSupport(callback: com.facebook.react.bridge.Callback) {
    callback.invoke(CieIDSdk.hasApiLevelSupport())
  }

  @ReactMethod
  fun launchCieID(callback: com.facebook.react.bridge.Callback) {
    val currentActivity = getCurrentActivity()!!
    if (currentActivity == null) {
      callback.invoke("fail to get current activity")
    } else {
      this.launchCieID(currentActivity)
      callback.invoke()
    }
  }

  /**
   * Open CieID application
   * Use this method when receive CieIDEvent.Card.ON_CARD_PIN_LOCKED. User needs to unlock CIE after 3 pin error.
   */
  fun launchCieID(activity: Activity){
    val cieIdPackage = "it.ipzs.cieid"
    val launchIntent = activity.getPackageManager().getLaunchIntentForPackage(cieIdPackage);
    if(launchIntent == null){
      activity.startActivity(Intent(Intent.ACTION_VIEW).setData(Uri.parse("https://play.google.com/store/apps/details?id=$cieIdPackage")))
    }
    else{
      activity.startActivity(launchIntent)
    }
  }

  @ReactMethod
  fun initializeSDK(promise: Promise) {
    try {
      val walletInstance =
        "eyJhbGciOiJFUzI1NiIsImtpZCI6IjV0NVlZcEJoTi1FZ0lFRUk1aVV6cjZyME1SMDJMblZRME9tZWttTktjalkiLCJ0cnVzdF9jaGFpbiI6WyJleUpoYkdjaU9pSkZVei4uLjZTMEEiLCJleUpoYkdjaU9pSkZVei4uLmpKTEEiLCJleUpoYkdjaU9pSkZVei4uLkg5Z3ciXSwidHlwIjoidmErand0IiwieDVjIjpbIk1JSUJqRENDIC4uLiBYRmVoZ0tRQT09Il19.eyJpc3MiOiJodHRwczovL3dhbGxldC1wcm92aWRlci5leGFtcGxlLm9yZyIsInN1YiI6InZiZVhKa3NNNDV4cGh0QU5uQ2lHNm1DeXVVNGpmR056b3BHdUt2b2dnOWMiLCJ0eXBlIjoiV2FsbGV0SW5zdGFuY2VBdHRlc3RhdGlvbiIsInBvbGljeV91cmkiOiJodHRwczovL3dhbGxldC1wcm92aWRlci5leGFtcGxlLm9yZy9wcml2YWN5X3BvbGljeSIsInRvc191cmkiOiJodHRwczovL3dhbGxldC1wcm92aWRlci5leGFtcGxlLm9yZy9pbmZvX3BvbGljeSIsImxvZ29fdXJpIjoiaHR0cHM6Ly93YWxsZXQtcHJvdmlkZXIuZXhhbXBsZS5vcmcvbG9nby5zdmciLCJhc2MiOiJodHRwczovL3dhbGxldC1wcm92aWRlci5leGFtcGxlLm9yZy9Mb0EvYmFzaWMiLCJjbmYiOnsiandrIjp7ImNydiI6IlAtMjU2Iiwia3R5IjoiRUMiLCJ4IjoiNEhOcHRJLXhyMnBqeVJKS0dNbno0V21kblFEX3VKU3E0Ujk1Tmo5OGI0NCIsInkiOiJMSVpuU0IzOXZGSmhZZ1MzazdqWEU0cjMtQ29HRlF3WnRQQklScXBObHJnIiwia2lkIjoidmJlWEprc000NXhwaHRBTm5DaUc2bUN5dVU0amZHTnpvcEd1S3ZvZ2c5YyJ9fSwiYXV0aG9yaXphdGlvbl9lbmRwb2ludCI6ImV1ZGl3OiIsInJlc3BvbnNlX3R5cGVzX3N1cHBvcnRlZCI6WyJ2cF90b2tlbiJdLCJ2cF9mb3JtYXRzX3N1cHBvcnRlZCI6eyJqd3RfdnBfanNvbiI6eyJhbGdfdmFsdWVzX3N1cHBvcnRlZCI6WyJFUzI1NiJdfSwiand0X3ZjX2pzb24iOnsiYWxnX3ZhbHVlc19zdXBwb3J0ZWQiOlsiRVMyNTYiXX19LCJyZXF1ZXN0X29iamVjdF9zaWduaW5nX2FsZ192YWx1ZXNfc3VwcG9ydGVkIjpbIkVTMjU2Il0sInByZXNlbnRhdGlvbl9kZWZpbml0aW9uX3VyaV9zdXBwb3J0ZWQiOmZhbHNlLCJpYXQiOjE2ODcyODExOTUsImV4cCI6MTY4NzI4ODM5NX0.OTuPik6p3o9j6VOx-uCyxRvHwoh1pDiiZcBQFNQt2uE3dK-8izGNflJVETi_uhGSZOf25Enkq-UvEin9NrbJNw"

      val pidProviderConfig = PidProviderConfig
        .Builder()
        .baseUrl("https://api.wakala.it/it-pid-provider/") // TODO: Update base url
        .walletInstance(walletInstance)
        .walletUri("https://www.google.com")
        .logEnabled(true)
        .build()

      PidProviderSdk.initialize(
        reactApplicationContext,
        pidProviderConfig = pidProviderConfig
      )
      promise.resolve("Initialization completed")
    } catch(e: Error) {
      promise.reject("Problem occurs during initialization")
    }

  }

}
