package it.ioreactnativeciepid.ipzs.androidpidprovider.entity

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import it.ioreactnativeciepid.ipzs.androidpidprovider.utils.PKCEConstant.JWT_CLAIM_VALUE
import java.io.Serializable

internal class CredentialDefinition: Serializable {
    @SerializedName("type")
    @Expose
    val type: List<String?> = listOf(JWT_CLAIM_VALUE)
}