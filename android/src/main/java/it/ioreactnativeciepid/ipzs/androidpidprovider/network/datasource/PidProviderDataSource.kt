package it.ioreactnativeciepid.ipzs.androidpidprovider.network.datasource

import it.ioreactnativeciepid.ipzs.androidpidprovider.network.response.CredentialResponse
import it.ioreactnativeciepid.ipzs.androidpidprovider.network.response.ParResponse
import it.ioreactnativeciepid.ipzs.androidpidprovider.network.response.TokenResponse

internal interface PidProviderDataSource {

    suspend fun requestPar(
        responseType: String,
        clientId: String,
        codeChallenge: String,
        codeChallengeMethod: String,
        clientAssertionType: String,
        clientAssertion: String,
        request: String
    ): ParResponse?

    suspend fun requestToken(
        dPop: String,
        grantType: String,
        clientId: String,
        code: String,
        codeVerifier: String,
        clientAssertionType: String,
        clientAssertion: String,
        redirectUri: String
    ): TokenResponse?

    suspend fun requestCredential(
        dPop: String,
        authorization: String,
        credentialDefinition: String,
        format: String,
        proof: String?
    ): CredentialResponse?

}