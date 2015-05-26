package com.danmorton.cordova.plugin.mopub;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.util.Log;
import android.view.View;
import java.util.Iterator;


import com.mopub.mobileads.MoPubView;
import com.mopub.mobileads.MoPubInterstitial;
import com.mopub.mobileads.MoPubErrorCode;
import static com.mopub.mobileads.MoPubInterstitial.InterstitialAdListener;


public class MopubPlugin extends CordovaPlugin implements InterstitialAdListener {
	//tags for identifying Cordova Calls.
	private static final String ACTION_SHOW_INTERSTITIAL = "showInterstitial";
	private static final String ACTION_CACHE_INTERSTITIAL = "cacheInterstitial";
	private static final String TAG = "MopubPlugin";
	
	private MoPubInterstitial mMoPubInterstitial;
	private MoPubView mMoPubView;

	@Override
	public boolean execute(String action, JSONArray args, CallbackContext callback) throws JSONException {
		final String add_unit_id = args.getString(0);
		final CallbackContext _callback = callback;
		if (action.equals(ACTION_SHOW_INTERSTITIAL)){
			cordova.getActivity().runOnUiThread(new Runnable() {
				@Override
				public void run() {
					MopubPlugin.this.showInterstitial(add_unit_id, _callback);
				}
			});
		} else if (action.equals(ACTION_CACHE_INTERSTITIAL)){
			cordova.getActivity().runOnUiThread(new Runnable() {
				@Override
				public void run() {
					MopubPlugin.this.preloadInterstitial(add_unit_id, _callback);
				}
			});
		}
		else return false; //easier to return true in all cases but this one...
		return true;
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
        if (mMoPubInterstitial != null) {
            mMoPubInterstitial.destroy();
            mMoPubInterstitial = null;
        }
	}

	/** 
	 * Functions for setting up Mopub and handling Callbacks...
	 */
	/** 
	 * Register callback to call later...
	 */	
	private HashMap<String, CallbackContext> callbackMap;
	public void registerCallback(CallbackContext callback, java.lang.String funcId) {
		if (null == callbackMap) callbackMap = new HashMap<String, CallbackContext>();
		callbackMap.put(funcId, callback);
	}
	/** 
	 * Register callback to call later...
	 */	
	public void doSuccessCallback(java.lang.String funcId) {
		if (null == callbackMap) callbackMap = new HashMap<String, CallbackContext>();
		CallbackContext callContext = callbackMap.get( funcId );
		if (callContext != null) {
			callbackMap.remove( funcId );//remove our callback from queue
			callContext.success("{\"location\": \""+location+"\",\"function\": \""+funcId+"\"}");
		}
	}
	/** 
	 * Register callback to call later...
	 */	
	public void doFailureCallback(java.lang.String funcId) {
		if (null == callbackMap) callbackMap = new HashMap<String, CallbackContext>();
		CallbackContext callContext = callbackMap.get( funcId );
		if (callContext != null) {
			callbackMap.remove( funcId );//remove our callback from queue
			callContext.error("{\"location\": \""+location+"\",\"function\": \""+funcId+"\"}");
		}
	}

	/**
	 * Fire event for js
	 * @param eventName (string) name to fire
	 * @param json (json) data to pass
	 */
	public void fireEvent(String eventName, JSONObject json) throws JSONException {

		String namespace = "mopub";
		String event  = "cordova.fireWindowEvent('"+ namespace +"."+ eventName +"');";		
		if (json != null)
			event  = "cordova.fireWindowEvent('"+ namespace +"."+ eventName +"', "+ json.toString() +");";
		String js = "setTimeout(function() { "+ event +" }, 0)";
		if (webView != null) {
			webView.sendJavascript(js);
		} else {
			Log.v(TAG + ":fireEvent", "webView is null!");
		}
	}

	public void showInterstitial(java.lang.String ad_unit, CallbackContext callback) {
		this.registerCallback(callback, ACTION_SHOW_INTERSTITIAL);
		if (mMoPubInterstitial == null) {
			mMoPubInterstitial = new MoPubInterstitial(cordova.getActivity(), ad_unit);
			mMoPubInterstitial.setInterstitialAdListener(this);
		}
		if (!mMoPubInterstitial.show()) {
			//we dont have an interstitial cached...
			mMoPubInterstitial.load();
			//callback failed
			this.doFailureCallback(ACTION_SHOW_INTERSTITIAL);			
		}
	}

	public void preloadInterstitial(java.lang.String location, CallbackContext callback) {
		this.registerCallback(callback, ACTION_CACHE_INTERSTITIAL);
		if (mMoPubInterstitial == null) {
			mMoPubInterstitial = new MoPubInterstitial(cordova.getActivity(), ad_unit);
			mMoPubInterstitial.setInterstitialAdListener(this);
		}
		mMoPubInterstitial.load();
	}
	
    /**
     * Mopub Interstitial Listeners
     */
	// InterstitialAdListener implementation
    @Override
    public void onInterstitialLoaded(MoPubInterstitial interstitial) {
		this.doSuccessCallback(location, ACTION_CACHE_INTERSTITIAL);
    }

    @Override
    public void onInterstitialFailed(MoPubInterstitial interstitial, MoPubErrorCode errorCode) {
		this.doFailureCallback(location, ACTION_CACHE_INTERSTITIAL);
		this.doFailureCallback(location, ACTION_SHOW_INTERSTITIAL);
//		mMoPubInterstitial.load();//load another one? could be loop?
    }

    @Override
    public void onInterstitialShown(MoPubInterstitial interstitial) {
		this.fireEvent("interstitialShown", null);
		this.doSuccessCallback(location, ACTION_SHOW_INTERSTITIAL);
    }

    @Override
    public void onInterstitialClicked(MoPubInterstitial interstitial) {
		this.fireEvent("interstitialClicked", null);
    }

    @Override
    public void onInterstitialDismissed(MoPubInterstitial interstitial) {
		this.fireEvent("interstitialDismissed", null);
		mMoPubInterstitial.load();//load another one...
    }
}