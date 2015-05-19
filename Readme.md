Cordova Mopub plugin v0.0.1
=============

This is a cordova implementation of Mopub for iOS/Android.

Usage
-------

The plugin has the following calls:
```JavaScript
	Mopub.cacheInterstitial(success, fail, adunitId);
```
This is called to cache/preload the interstitial. If this is not called first likely will fail on first show...
Parameters: successCallback, failureCallback, mopub ad unit id
```JavaScript
	Chartboost.showInterstitial(success, fail, adunitId);
```
This is called to show the interstitial Ad.
Parameters: successCallback, failureCallback, mopub ad unit id

Window Event Callbacks:

```JavaScript
	mopub.interstitialShown
	mopub.interstitialClicked
	mopub.interstitialDismissed
```

Implement these via

```
	window.addEventListener("mopub.event", funciton(data) {
		//do something..
	});
```

Update Mopub SDK
----------------

iOS Mopub SDK Compilation

- Create new Xcode project for Cocoa Touch Framework
- Add Mopub SDK including desired Ad Libraries
- build and replace framework at /libs/ios/MopubSDK.framework
- Add/Remove/Update third party libraries as necessary.
- - if adding/removing you need to update plugin.xml accordingly

Android Mopub SDK Compilation

- Open Latest Android SDK in Android Studio
- Compile to jar file

INSTALLATION
-------------

	cordova plugin add https://github.com/danmorton/cordova-mopub-plugin


SUPPORTED PLATFORMS
-------------------

- iOS
- Android

TODO
------

- Banner Support
- More Platforms
- Keywords

CHANGELOG
---------
v0.0.1:

 - First release.
