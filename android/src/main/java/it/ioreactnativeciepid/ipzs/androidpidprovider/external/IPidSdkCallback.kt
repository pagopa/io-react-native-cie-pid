package it.ioreactnativeciepid.ipzs.androidpidprovider.external

interface IPidSdkCallback<T> {

    fun onComplete(result: T?)

    fun onError(throwable: Throwable)

}

