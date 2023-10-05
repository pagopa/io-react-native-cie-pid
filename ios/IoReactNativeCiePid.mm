#import <React/RCTBridgeModule.h>

@interface RCT_EXTERN_MODULE(IoReactNativeCiePid, NSObject)

/* RCT_EXTERN_METHOD(multiply:(float)a withB:(float)b
                 withResolver:(RCTPromiseResolveBlock)resolve
                 withRejecter:(RCTPromiseRejectBlock)reject) */

RCT_EXTERN_METHOD(start:(RCTResponseSenderBlock)callback)

RCT_EXTERN_METHOD(isNFCEnabled:(RCTResponseSenderBlock)callback)

RCT_EXTERN_METHOD(hasNFCFeature:(RCTResponseSenderBlock)callback)

RCT_EXTERN_METHOD(setPin:(NSString*) pin)

RCT_EXTERN_METHOD(setAuthenticationUrl:(NSString*) url)

RCT_EXTERN_METHOD(setAlertMessage:(NSString*)key withValue:(NSString*)value)

RCT_EXTERN_METHOD(launchCieID:(RCTResponseSenderBlock)callback)

RCT_EXTERN_METHOD(startListeningNFC:(RCTResponseSenderBlock)callback)

RCT_EXTERN_METHOD(stopListeningNFC:(RCTResponseSenderBlock)callback)

RCT_EXTERN_METHOD(openNFCSettings:(RCTResponseSenderBlock)callback)

RCT_EXTERN_METHOD(hasApiLevelSupport:(RCTResponseSenderBlock)callback)

+ (BOOL)requiresMainQueueSetup
{
  return NO;
}

@end
