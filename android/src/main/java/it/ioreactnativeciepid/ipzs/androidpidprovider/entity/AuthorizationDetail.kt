package it.ioreactnativeciepid.ipzs.androidpidprovider.entity


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import it.ioreactnativeciepid.ipzs.androidpidprovider.utils.PKCEConstant.JWT_FORMAT_VALUE
import it.ioreactnativeciepid.ipzs.androidpidprovider.utils.PKCEConstant.JWT_TYPE_VALUE
import java.io.Serializable

internal class AuthorizationDetail: Serializable {

    @SerializedName("type")
    @Expose
    val type: String = JWT_TYPE_VALUE

    @SerializedName("format")
    @Expose
    val format: String = JWT_FORMAT_VALUE

    @SerializedName("credential_definition")
    @Expose
    val credentialDefinition: CredentialDefinition = CredentialDefinition()
}