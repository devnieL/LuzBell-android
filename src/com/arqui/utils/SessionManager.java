package com.arqui.utils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.arqui.luzbell.LoginActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * Reusing :
 * http://www.androidhive.info/2012/08/android-session-management-using
 * -shared-preferences/ Adapting to project.
 * 
 * @author devnieL
 * 
 */
public class SessionManager {
	// Shared Preferences
	SharedPreferences pref;

	// Editor for Shared preferences
	Editor editor;

	// Context
	Context _context;

	// Shared pref mode
	int PRIVATE_MODE = 0;

	// Sharedpref file name
	private static final String PREF_NAME = "QuickyTaxi";

	// All Shared Preferences Keys
	private static final String IS_LOGIN = "IsLoggedIn";

	// User name (make variable public to access from outside)
	public static final String KEY_NAME = "name";

	// Email address (make variable public to access from outside)
	public static final String KEY_EMAIL = "email";

	// Constructor
	public SessionManager(Context context) {
		this._context = context;
		pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
		editor = pref.edit();
	}

	/**
	 * @param name of the session data
	 * @param value of the session data
	 */

	public void addJSONData(JSONObject data) {
		Iterator iter = data.keys();
		try {
			while (iter.hasNext()) {
				String key = (String) iter.next();
				String value = data.getString(key);
				editor.putString(key, value);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		editor.commit();
	}
	
	/**
	 * Add a simple key-value to the session
	 * 
	 * @param key
	 * @param value
	 */
	
	public void add(String key, String value){ 
		editor.putString(key, value);
		// commit changes
		editor.commit();
	}
	
	/**
	 * Get an specific data from session.
	 * @param key
	 * @return
	 */
	
	public String get(String key){
		return pref.getString(key, null);
	}

	/**
	 * Create login session
	 * */

	public void createLoginSession() {
		// Storing login value as TRUE
		editor.putBoolean(IS_LOGIN, true);

		// commit changes
		editor.commit();
	}

	/**
	 * Check login method will check user login status If false it will redirect
	 * user to login page Else won't do anything
	 * */

	public void checkLogin() {
		// Check login status
		if (!this.isLoggedIn()) {
			// user is not logged in redirect him to Login Activity
			Intent i = new Intent(_context, LoginActivity.class);
			// Closing all the Activities
			i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

			// Add new Flag to start new Activity
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

			// Staring Login Activity
			_context.startActivity(i);
		}
	}

	/**
	 * Get stored session data
	 * */

	public HashMap<String, String> getUserDetails() {
		HashMap<String, String> user = new HashMap<String, String>();
		// user name
		user.put(KEY_NAME, pref.getString(KEY_NAME, null));

		// user email id
		user.put(KEY_EMAIL, pref.getString(KEY_EMAIL, null));
		
		HashMap<String, String> user_data = (HashMap<String, String>) pref.getAll();

		// return user
		return user_data;
	}
	
	/**
	 * Get Auth Token
	 */
	
	public String getAuthToken(){
		return pref.getString("token", null);
	}

	/**
	 * Clear session details
	 * */

	public void logoutUser() {
		// Clearing all data from Shared Preferences
		editor.clear();
		editor.commit();

		// After logout redirect user to Loing Activity
		Intent i = new Intent(_context, LoginActivity.class);
		// Closing all the Activities
		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

		// Add new Flag to start new Activity
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

		// Staring Login Activity
		_context.startActivity(i);
	}

	/**
	 * Quick check for login
	 * **/

	// Get Login State
	public boolean isLoggedIn() {
		return pref.getBoolean(IS_LOGIN, false);
	}
}
