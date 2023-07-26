import iociesdkios

let eventChannel = "onEvent"
let errorChannel = "onError"
let successChannel = "onSuccess"

@objc(IoReactNativeCiePid)
@available(iOS 13.0, *)
class IoReactNativeCiePid: NSObject {
    
    var attemptsLeft: Int = 0
    var PIN: String?
    var url: String?
    
    var cieSDK: CIEIDSdk?
    
    override init() {
        super.init()
        self.cieSDK = CIEIDSdk()
        
    }
    
    @objc func isNFCEnabled(_ callback: RCTResponseSenderBlock) {
        callback([true])
    }
    
    @objc func hasNFCFeature(_ callback: RCTResponseSenderBlock) {
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
    
    func post(callback: @escaping (String?, String?) -> Void) {
        DispatchQueue.global().async {
            self.cieSDK?.post(url: self.url!, pin: self.PIN!) { error, response in
                callback(error, response)
            }
        }
    }
    
    @objc func start(_ callback: RCTResponseSenderBlock) {
        post { error, response in
            if error == nil {
                self.sendEvent(channel: successChannel, eventValue: response)
            } else {
                self.sendEvent(channel: eventChannel, eventValue: error)
            }
        }
        callback([])
    }
    
    func sendEvent(channel: String, eventValue: String?) {
        let attemptsLeft = NSNumber(value: self.cieSDK?.attemptsLeft ?? 0)
        let body: [String: Any] = ["event": eventValue ?? "", "attemptsLeft": attemptsLeft]
        // self.sendEvent(channel: channel, eventValue: body["event"] as? String )
    }
    
    @objc func launchCieID(_ callback: RCTResponseSenderBlock) {
        // TODO: Implement CIE ID
        callback([])
    }
    
    // The following methods are not available on iOS
    // Implement them if needed for Android or other platforms
    
    @objc func startListeningNFC(_ callback: RCTResponseSenderBlock) {
        callback([])
    }
    
    @objc func stopListeningNFC(_ callback: RCTResponseSenderBlock) {
        callback([])
    }
    
    @objc func openNFCSettings(_ callback: RCTResponseSenderBlock) {
        callback([])
    }
    
    @objc func hasApiLevelSupport(_ callback: RCTResponseSenderBlock) {
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
