package it.ioreactnativeciepid.ipzs.androidpidprovider.external

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import it.ioreactnativeciepid.ipzs.androidpidprovider.exception.PIDProviderException
import it.ioreactnativeciepid.ipzs.androidpidprovider.facade.PidProviderFacade
import it.ioreactnativeciepid.ipzs.androidpidprovider.storage.PidProviderSDKShared
import it.ioreactnativeciepid.ipzs.androidpidprovider.utils.PidProviderSDKUtils
import it.ioreactnativeciepid.ipzs.androidpidprovider.utils.PidSdkStartCallbackManager
import it.ioreactnativeciepid.ipzs.androidpidprovider.utils.PidSdkCompleteCallbackManager
import it.ioreactnativeciepid.ipzs.cieidsdk.data.PidCieData

object PidProviderSdk {

    fun initialize(context: Context, pidProviderConfig: PidProviderConfig? = null) {
        pidProviderConfig?.let {
            PidProviderSDKUtils.configure(context,pidProviderConfig)
        }?: kotlin.run {
            throw PIDProviderException("pid provider config can't be null")
        }
    }

    fun initJwtForPar(context: Context): String? {
        return PidProviderFacade(context).generateJwtForPar()
    }

    fun startAuthFlow(activity: AppCompatActivity, signedJwtForPar: String, jwkForDPoP: String, pidSdkCallback: IPidSdkCallback<Boolean>){
        PidProviderFacade(activity).startAuthFlow(activity, signedJwtForPar, jwkForDPoP)
        PidSdkStartCallbackManager.setSDKCallback(pidSdkCallback)
    }

    fun getUnsignedJwtForProof(context: Context): String {
        return PidProviderSDKShared.getInstance(context).getUnsignedJWTProof()
    }

    fun completeAuthFlow(activity: AppCompatActivity, pidCieData: PidCieData?, signedJwtForProof: String, pidSdkCallback: IPidSdkCallback<PidCredential>){
        PidProviderSDKShared.getInstance(activity).saveSignedJWTProof(signedJwtForProof)
        PidProviderFacade(activity).getCredential(pidCieData)
        PidSdkCompleteCallbackManager.setSDKCallback(pidSdkCallback)
    }

}