package com.arqui.luzbell;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;


import com.arqui.luzbell.LoginActivity.UserLoginTask;
import com.arqui.utils.Http;
import com.arqui.utils.SessionManager;


import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class RegistroActivity extends Activity {
	
	private EditText txtConsumo;
	private EditText txtNumeroSuministro;
	private Button btnRegistrarConsumo;
	
	private View mRegisterStatusView;
	private TextView mRegisterStatusMessageView;
	
	
	final String ws = "http://devniel.com:3000/api";
	
	public Context ctx;
	
	// Session
	SessionManager session;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_registro);
		
		ctx = getApplicationContext();
		
		session = new SessionManager(this.getApplicationContext());
		
		txtConsumo = (EditText)findViewById(R.id.txt_consumo);
		txtNumeroSuministro = (EditText)findViewById(R.id.txt_numero_suministro);
		btnRegistrarConsumo = (Button)findViewById(R.id.registro_consumo_button);
		
		mRegisterStatusView = findViewById(R.id.register_status);
		mRegisterStatusMessageView = (TextView) findViewById(R.id.register_status_message);
		
		
		
		btnRegistrarConsumo.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				registrarConsumo();
			}
		});
	}

	/**
	 * Shows the progress UI and hides the login form.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private void showProgress(final boolean show) {
		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
		// for very easy animations. If available, use these APIs to fade-in
		// the progress spinner.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			int shortAnimTime = getResources().getInteger(
					android.R.integer.config_shortAnimTime);

			mRegisterStatusView.setVisibility(View.VISIBLE);
			mRegisterStatusView.animate().setDuration(shortAnimTime)
					.alpha(show ? 1 : 0)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mRegisterStatusView.setVisibility(show ? View.VISIBLE
									: View.GONE);
						}
					});
			
		} else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			mRegisterStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
		}
	}
	
	
	
	/** ===========================================
	 * REGISTRAR CONSUMO
	 ==============================================*/
	
	public void registrarConsumo(){
		
		RegistroTask registroTask = new RegistroTask();
		registroTask.execute((Void) null);
		
	}
	
	/**============================================
	 * MENU CONFIGURATION
	 * ===========================================*/
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.registro, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		if(item.getItemId() == R.id.action_logout){
			session.logoutUser();
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	
	/**=============================================
	 * REGISTRO TASK
	 * ============================================*/
	
	/**
	 * Represents an asynchronous login/registration task used to authenticate
	 * the user.
	 */
	public class RegistroTask extends AsyncTask<Object, Void, JSONObject> {
		
		@Override
		protected JSONObject doInBackground(Object... params) {
			Http hp = new Http();
			
			// curl -X POST http://localhost:3000/api/consumos/add/ -d 'consumo=123&numero_sumi45' -H 'Authorization: Token 54d71a5d67339ed9701dd8dec669a870e2fd42e8'	
			
			hp.addPostParam(new BasicNameValuePair("consumo", txtConsumo.getText().toString() ));
			hp.addPostParam(new BasicNameValuePair("numero_suministro", txtNumeroSuministro.getText().toString()));
			hp.setMethod("POST");
			hp.setAuthToken(session.getAuthToken());
			hp.setUrl(ws + "/consumos/add/");
			return hp.request(); // I'm using Http as an library of methods, not as an AsyncTask 'cause I'm not invoking execute()
		}
		
		@Override
		protected void onPostExecute(JSONObject response) {
			showProgress(false);
			
			Log.d("REGISTRO DE NUEVO OBJETO", response.toString());
			
			try {
				if (response.getInt("status") == 200) {
					finish();
					
					Intent registro = new Intent(ctx, RegistroActivity.class);
					startActivity(registro);
					Toast.makeText(ctx, "Correctamente registrado", 2000).show();
				}else{
					Toast.makeText(ctx, response.getJSONObject("data").getString("reason"), 2000).show();
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		@Override
		protected void onCancelled() {
			showProgress(false);
		}
	}

}
