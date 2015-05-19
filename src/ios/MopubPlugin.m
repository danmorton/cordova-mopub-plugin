#import <Cordova/CDV.h>

#import <CommonCrypto/CommonDigest.h>
#import <AdSupport/AdSupport.h>

#import <MopubSDK/MPInterstitialAdController.h>

//#import "MPInterstitialAdController.h"

@interface MopubPlugin : CDVPlugin <MPInterstitialAdControllerDelegate>{
//	NSMutableArray* _queue;
    NSMutableDictionary* _queue;
}
@property (nonatomic, strong) MPInterstitialAdController *interstitial;

-(void) showInterstitial:(CDVInvokedUrlCommand*)command;
-(void) cacheInterstitial:(CDVInvokedUrlCommand*)command;

@end

@implementation MopubPlugin

#pragma mark -
#pragma mark Helpers

/**
 * Fire a JavaScript DOM event.
 * @param event The event name
 * @param data The event data payload
 */
- (void)fireEvent:(NSString *)event data:(NSDictionary *)data
{
	if (data == nil) {
		NSString *jsFormat = @"setTimeout(function(){ cordova.fireWindowEvent('mopub.%@'); }, 0);";
		[self.commandDelegate evalJs:[NSString stringWithFormat:jsFormat, event]];
	} else {
		NSString *json = nil;
		NSError *error = nil;
		NSData *jsonData = [NSJSONSerialization dataWithJSONObject:data options:0 error:&error];
		if (!jsonData) {
			NSLog(@"JSON serialization error: %@", error);
		} else {
			json = [[NSString alloc] initWithData:jsonData encoding:NSUTF8StringEncoding];
		}
		NSString *jsFormat = @"setTimeout(function(){ cordova.fireWindowEvent('mopub.%@', %@); }, 0);";
		[self.commandDelegate evalJs:[NSString stringWithFormat:jsFormat, event, json]];
	}
}

-(void) registerCallback:(CDVInvokedUrlCommand*)command withFunctionID:(NSString*)funcId {
	NSString* callbackId = command.callbackId;
    [_queue setObject: callbackId forKey:funcId];
}

-(void) doSuccessCallbackForFunctionId:(NSString*)funcId {
    if(_queue != nil && [_queue objectForKey:funcId] != nil) {
		NSString* callbackId = [_queue objectForKey:funcId];
		[_queue removeObjectForKey:funcId];
		CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:@""];
		[self.commandDelegate sendPluginResult:pluginResult callbackId:callbackId];
    }
}

-(void) doFailureCallbackForFunctionId:(NSString*)funcId {
    if(_queue != nil && [_queue objectForKey:funcId] != nil) {
		NSString* callbackId = [_queue objectForKey:funcId];
		[_queue removeObjectForKey:funcId];
		CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:@""];
		[self.commandDelegate sendPluginResult:pluginResult callbackId:callbackId];
    }
}

#pragma mark -
#pragma mark Mopub interstitials

-(void) showInterstitial:(CDVInvokedUrlCommand*)command {
	[self registerCallback:command withFunctionID:@"showInterstitial"];
	NSString* ad_unit_id = [command.arguments objectAtIndex:0];
	if (self.interstitial == nil) {
        self.interstitial = [MPInterstitialAdController interstitialAdControllerForAdUnitId:ad_unit_id];
		self.interstitial.delegate = self;
	}
	[self.interstitial showFromViewController:self.viewController];
}

-(void) cacheInterstitial:(CDVInvokedUrlCommand*)command {
	[self registerCallback:command withFunctionID:@"cacheInterstitial"];
	NSString* ad_unit_id = [command.arguments objectAtIndex:0];
	if (self.interstitial == nil) {
        self.interstitial = [MPInterstitialAdController interstitialAdControllerForAdUnitId:ad_unit_id];
		self.interstitial.delegate = self;
	}
	[self.interstitial loadAd];
}

#pragma mark - <MPInterstitialAdControllerDelegate>

- (void)interstitialDidLoadAd:(MPInterstitialAdController *)interstitial
{
	[self doSuccessCallbackForFunctionId:@"cacheInterstitial"];
}

- (void)interstitialDidFailToLoadAd:(MPInterstitialAdController *)interstitial
{
	[self doFailureCallbackForFunctionId:@"cacheInterstitial"];
	[self doFailureCallbackForFunctionId:@"showInterstitial"];
}

- (void)interstitialDidExpire:(MPInterstitialAdController *)interstitial
{
	[self doFailureCallbackForFunctionId:@"cacheInterstitial"];
	[self doFailureCallbackForFunctionId:@"showInterstitial"];
//	[self.interstitial loadAd];//try to load a new one??? could get loopy
}

- (void)interstitialDidAppear:(MPInterstitialAdController *)interstitial
{
	[self doSuccessCallbackForFunctionId:@"showInterstitial"];
	[self fireEvent:@"interstitialShown" data:nil];
}

- (void)interstitialDidDisappear:(MPInterstitialAdController *)interstitial
{
	[self fireEvent:@"interstitialDismissed" data:nil];
	[self.interstitial loadAd];//load another...
}

- (void)interstitialDidReceiveTapEvent:(MPInterstitialAdController *)interstitial
{
	[self fireEvent:@"interstitialClicked" data:nil];
}

@end
