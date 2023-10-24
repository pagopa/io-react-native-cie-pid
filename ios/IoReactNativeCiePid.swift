import React
import iociesdkios

let eventChannel = "onEvent"
let errorChannel = "onError"
let successChannel = "onSuccess"

@objc(IoReactNativeCiePid)
@available(iOS 13.0, *)
class IoReactNativeCiePid: RCTEventEmitter {
    
    private var attemptsLeft: Int = 0
    private var PIN: String?
    private var url: String?
    private var cieSDK: CIEIDSdk?
        
    override init() {
        super.init()
        self.cieSDK = CIEIDSdk()
        
    }
    
    override func supportedEvents() -> [String]! {
        return ["onSuccess", "onEvent", "onError"]
    }
    
    @objc func isNFCEnabled(_ callback: @escaping RCTResponseSenderBlock) {
        callback([true])
    }
    
    @objc func hasNFCFeature(_ callback: @escaping RCTResponseSenderBlock) {
        let value = cieSDK?.hasNFCFeature() ?? false
        callback([NSNumber(value: value)])
    }
    
    @objc func setPin(_ pin: String) {
        self.PIN = pin
    }
    
    func getPin() -> String? {
        return self.PIN
    }
    
    @objc func setAuthenticationUrl(_ url: String) {
        self.url = url
    }
    
    @objc func setAlertMessage(_ key: String, withValue value: String) {
        cieSDK?.setAlertMessage(key: key, value: value)
    }
    
    func getAuthenticationUrl() -> String? {
        return self.url
    }
    
    private func post(callback: @escaping (String?, String?) -> Void) {
        DispatchQueue.global().async {
            self.cieSDK?.post(url: "https://interno.gov.it", pin: self.PIN ?? "") { error, response in
                callback(error, response)
            }
        }
    }
    
    @objc func start(_ callback: RCTResponseSenderBlock) {
        post { error, response in
            if error == nil {
                self.sendCieEvent(channel: successChannel, eventValue: response ?? "")
            } else {
                self.sendCieEvent(channel: errorChannel, eventValue: error ?? "")
            }
        }
        callback([])
    }
    
    private func sendCieEvent(channel: String, eventValue: String) {
        self.sendEvent(withName: channel, body: ["event": eventValue, "attemptsLeft": attemptsLeft] as [String : Any])
    }
    
    @objc func launchCieID(_ callback: @escaping RCTResponseSenderBlock) {
        // TODO: Implement CIE ID
        callback([])
    }
    
    // The following methods are not available on iOS
    // Implement them if needed for Android or other platforms
    
    @objc func startListeningNFC(_ callback: @escaping RCTResponseSenderBlock) {
        callback([])
    }
    
    @objc func stopListeningNFC(_ callback: @escaping RCTResponseSenderBlock) {
        callback([])
    }
    
    @objc func openNFCSettings(_ callback: @escaping RCTResponseSenderBlock) {
        callback([])
    }
    
    @objc func hasApiLevelSupport(_ callback: @escaping RCTResponseSenderBlock) {
        callback([])
    }
    
    /*@objc(multiply:withB:withResolver:withRejecter:)
     func multiply(a: Float, b: Float, resolve:RCTPromiseResolveBlock,reject:RCTPromiseRejectBlock) -> Void {
     resolve(a*b)
     } */
    /* @objc(initSDK:withRejecter:)
     func initSDK(resolve:RCTPromiseResolveBlock,reject:RCTPromiseRejectBlock) -> Void {
     
     resolve(true)
     } else {
     reject("0", "init sdk failed", nil)
     }
     } */
}
