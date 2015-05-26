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

- see: ```https://github.com/danmorton/mopub-framework```

Android Mopub SDK Compilation

- Download latest SDK from ```https://github.com/mopub/mopub-android-sdk```
- Open in Android Studio
- Compile to aar file

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
