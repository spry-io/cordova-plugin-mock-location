package com.kosh;

import org.apache.cordova.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.apache.cordova.PermissionHelper;
import android.Manifest;
import android.location.Location;
import android.location.LocationManager;
import android.content.Context;
import android.provider.Settings.Secure;
import 	org.json.JSONObject;

import static org.apache.cordova.LOG.*;

public class MockLocation extends CordovaPlugin {

    private static final String TAG = "MockLocation";
    private static boolean isFake = false;


    String [] permissions = { Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION };

    @Override
    public boolean execute(String action, JSONArray data, CallbackContext callbackContext) throws JSONException {
        String gpsProvider = "unavailable";
        JSONObject parameter = new JSONObject();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            if (action.equals("check")) {
                Context context = this.cordova.getActivity().getApplicationContext();
                Location location = null;
                LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
                if (checkSelfPermission()) {
                    location = manager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
                    d(TAG, "Location => " + location);
                }

                if (location != null) {
                    gpsProvider = location.getProvider();
                    isFake = location.isFromMockProvider();
                } else {
                    d(TAG, "No location!");
                    isFake = false;
                }
                try {
                    if(isFake){
                        parameter.put("isMock", Boolean.TRUE);
                        parameter.put("provider", gpsProvider);
                        PluginResult result = new PluginResult(PluginResult.Status.OK, parameter);
                        callbackContext.sendPluginResult(result);
                    }else{
                        parameter.put("isMock", Boolean.FALSE);
                        parameter.put("provider", gpsProvider);
                        PluginResult result = new PluginResult(PluginResult.Status.OK, parameter);
                        callbackContext.sendPluginResult(result);
                    }

                } catch (JSONException e) {
                    return false;
                }
                return true;
            }else{
                return false;
            }
        }else{
            if (action.equals("check")) {
                try {
                    if (Secure.getString(this.cordova.getActivity().getContentResolver(), Secure.ALLOW_MOCK_LOCATION).equals("0")) {
                        parameter.put("isMock", Boolean.FALSE);
                        parameter.put("provider", gpsProvider);
                        PluginResult result = new PluginResult(PluginResult.Status.OK, parameter);
                        callbackContext.sendPluginResult(result);
                    } else {
                        parameter.put("isMock", Boolean.TRUE);
                        parameter.put("provider", gpsProvider);
                        PluginResult result = new PluginResult(PluginResult.Status.OK, parameter);
                        callbackContext.sendPluginResult(result);
                    }
                } catch (JSONException e) {
                    return false;
                }
                return true;
            } else {
                return false;
            }
        }
    }


    public boolean checkSelfPermission() {
        for(String p : permissions)
        {
            if(!PermissionHelper.hasPermission(this, p))
            {
                return false;
            }
        }
        return true;
    }
}
