package com.arqui.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpUriRequest;
import org.json.JSONException;
import org.json.JSONObject;

import com.arqui.listeners.HttpResponseListener;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

/**
 * @author Daniel Flores
 * A library to make Http Request as an AsyncTask
 */

public class Http extends AsyncTask<Object, Void, JSONObject>{
	
	static final String TAG = Http.class.getSimpleName();
	
	public String result;
	public HttpResponseListener responseListener;
	public LinearLayout progressBar;

	public String url;
	public String method;
	private ArrayList <NameValuePair> headers;
    private ArrayList<NameValuePair> postParams;
    
    private String authToken;

	/** ============================================================
	 * CONSTRUCTORS
	 ============================================================*/
	
	public Http(String url) {
        this.url = url;
        this.headers = new ArrayList<NameValuePair>();
        this.postParams = new ArrayList<NameValuePair>();
    }
    
    public Http() {
        this.headers = new ArrayList<NameValuePair>();
        this.postParams = new ArrayList<NameValuePair>();
    }
    
    /** ============================================================
	 * SET HEADERS PARAMETERS
	 ============================================================*/
	
	public void setHeadersParameters(HttpUriRequest request)
    {
        for(NameValuePair h : headers) {
            request.addHeader(h.getName(), h.getValue());
        }
    }
	
	/** ============================================================
	 * ADD POST PARAM
	 ============================================================*/

    public void addPostParam(NameValuePair param) {
        postParams.add(param);
    }
    
    /** ============================================================
	 * GET DATA
	 ============================================================*/

    public JSONObject getData() {
        JSONObject jsonObj = new JSONObject();

        for(NameValuePair p : postParams) {
            try {
                jsonObj.put(p.getName(), p.getValue());
            } catch (JSONException e) {
                Log.e(TAG, "JSONException: " + e);
            }
        }
        return jsonObj;
    }
    
    /** ============================================================
	 * DO IN BACKGROUND
	 ============================================================*/
	
	protected JSONObject doInBackground(Object... params) {
		return request();
	}
	
    
    /** ============================================================
	 * PRE EXECUTE
	 ============================================================*/
    
	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		super.onPreExecute();
		if(progressBar != null)
			progressBar.setVisibility(View.VISIBLE);
	}
	
	/** ============================================================
	 * POST EXECUTE
	 ============================================================*/
	
	@Override 
	protected void onPostExecute(JSONObject response){
		if(progressBar != null)
			progressBar.setVisibility(View.GONE);
		
		Integer status;
		JSONObject data;
		
		try {
			status = response.getInt("status");
			data = response.getJSONObject("data");
		} catch (JSONException e) {
			e.printStackTrace();
			
			status = 400;
			data = null;
		}
		
		responseListener.onResponse(data,status);
	}
	
	/**============================================================
	 * REQUEST
	 * Make query to a network service
	 * @return JSONObject json representation of result
	 ============================================================*/
	
	public JSONObject request() {
		
		HttpURLConnection connection = null;
		URL to_url = null;
		
		try {
			to_url = new URL(this.getUrl());
			connection = (HttpURLConnection) to_url.openConnection();
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setRequestProperty("Accept", "application/json");
			
			if (this.getMethod().equals("POST")){
				connection.setRequestMethod("POST");
				connection.setDoOutput(true);
			}else{
				connection.setRequestMethod("GET");
			}
						
			if(this.getAuthToken() != null)
				connection.setRequestProperty("Authorization","Token " + this.getAuthToken());
	        
	        // Data
	        if(this.getMethod().equals("POST") && this.getData() != null){
	        	
	        	Log.d(TAG, "SETEAR DATA");
	        	
				byte[] outputBytes = this.getData().toString().getBytes("UTF-8");
				OutputStream os = connection.getOutputStream();
				os.write(outputBytes);
				os.flush();
				os.close();
	        }
						
			String result = null;

			// Response
			try {
				result = convertStreamToString(connection.getInputStream());
			}catch(IOException exp){
				// Error Response
				int statusCode = connection.getResponseCode();
				String message = connection.getResponseMessage();
				result = convertStreamToString(connection.getErrorStream());
			}
			
			Log.d(TAG,result);
			
			JSONObject response = new JSONObject();
			response.put("status", connection.getResponseCode());
			response.put("data", new JSONObject(result));
			return response;
			
		} catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e){
        	e.printStackTrace();
        }
		
		return null;
	}

	/** ============================================================
	 * STREAM RESULT TO STRING
	 ============================================================*/
	
	private static String convertStreamToString(InputStream is) {
        /*
         * To convert the InputStream to String we use the BufferedReader.readLine()
         * method. We iterate until the BufferedReader return null which means
         * there's no more data to read. Each line will appended to a StringBuilder
         * and returned as String.
         */
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return sb.toString();
    }
	
	/** ============================================================
	 * AUTH TOKEN - GETTERS AND SETTERS
	 ============================================================*/
    
    public String getAuthToken() {
		return authToken;
	}

	public void setAuthToken(String authToken) {
		this.authToken = authToken;
	}
	
	/** ============================================================
	 * PROGRESS BAR - GETTERS AND SETTERS
	 ============================================================*/

	public LinearLayout getProgressBar() {
		return progressBar;
	}
	
	public void setProgressBar(LinearLayout progressBar) {
		this.progressBar = progressBar;
	}
	
	/** ============================================================
	 * URL- GETTERS AND SETTERS
	 ============================================================*/

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
	/** ============================================================
	 * METHOD - GETTERS AND SETTERS
	 ============================================================*/

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {	
		if(!method.toUpperCase().equals("POST") && !method.toUpperCase().equals("GET")){
			throw new InvalidParameterException("El método " + method + " es inválido.");
		}
		
		this.method = method;
	}
	
	/** ============================================================
	 * RESPONSE LISTENER- GETTERS AND SETTERS
	 ============================================================*/
	
	public HttpResponseListener getResponseListener() {
		return responseListener;
	}

	public void setResponseListener(HttpResponseListener responseListener) {
		this.responseListener = responseListener;
	}	
}