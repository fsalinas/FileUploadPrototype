package com.fsalinas.fileupload;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class Login extends Activity {
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
    }
    
	public void onSignInClick(View v) {
		
		EditText userNameTextView = (EditText) this.findViewById(R.id.UserName);
		EditText passwordTextView = (EditText) this.findViewById(R.id.Password);
		
		if ( authenticate(userNameTextView.getText().toString(), passwordTextView.getText().toString()) )
		{
			Intent intent = new Intent();
			intent.setComponent(new ComponentName(this, LockBoxUpload.class));
			startActivity(intent);
		}
		
	}
	
	public boolean authenticate(String userName, String password)  {
		
		boolean loginSuccess = true;
		
/*		DefaultHttpClient httpclient = new DefaultHttpClient();
		httpclient.getParams().setParameter(
				CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);

		HttpPost httpPost;
		try {
			httpPost = new HttpPost(new URL(UPLOAD_URL).toURI());
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		
*/		
		return loginSuccess;
	}
}
