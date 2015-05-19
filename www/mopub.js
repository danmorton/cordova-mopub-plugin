	var Mopub = exports;
	var exec = require('cordova/exec');
	var cordova = require('cordova');
	Mopub.showInterstitial = function(sucessCallback, failCallback, adunitId) {
		exec(sucessCallback, failCallback, "ChartboostPlugin", "showInterstitial", [adunitId]);
	}
    Mopub.cacheInterstitial = function(sucessCallback, failCallback, adunitId) {
        exec(sucessCallback, failCallback, "ChartboostPlugin", "cacheInterstitial", [adunitId]);
    }