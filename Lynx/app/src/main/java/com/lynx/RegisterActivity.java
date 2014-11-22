package com.lynx;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class RegisterActivity extends Activity {

    private EditText mPasswordView;
    private EditText mUsernameView;
    private EditText mDisplayNameView;
    private EditText mPasswordConfirmView;

    private View mRegisterFormView;
    private View mProgressView;

    private UserRegisterTask mRegistrationTask = null;

    private JSONParser jsonParser = new JSONParser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mPasswordView = (EditText) findViewById(R.id.password_edit_text);
        mUsernameView = (EditText) findViewById(R.id.username_edit_text);
        mDisplayNameView = (EditText) findViewById(R.id.display_name_edit_text);
        mPasswordConfirmView = (EditText) findViewById(R.id.confirm_password_edit_text);

        mRegisterFormView = findViewById(R.id.register_form);
        mProgressView = findViewById(R.id.register_progress);
    }

    public void attemptRegister(View view) {
        if(mRegistrationTask != null) {
            return;
        }

        mPasswordView.setError(null);
        mUsernameView.setError(null);
        mDisplayNameView.setError(null);
        mPasswordConfirmView.setError(null);

        String username = mUsernameView.getText().toString();
        String displayName = mDisplayNameView.getText().toString();
        String password = mPasswordView.getText().toString();
        String passwordConfirm = mPasswordConfirmView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for valid username
        if (TextUtils.isEmpty(username)){
            mUsernameView.setError(getString(R.string.error_field_required));
            cancel = true;
        }
        else if (!isUsernameValid(username)) {
            mUsernameView.setError(getString(R.string.error_invalid_username));
            focusView = mUsernameView;
            cancel = true;
        }

        // Check for valid displayName
        if (TextUtils.isEmpty(displayName)){
            mDisplayNameView.setError(getString(R.string.error_field_required));
            cancel = true;
        }
        else if (!isUsernameValid(displayName)) {
            mDisplayNameView.setError(getString(R.string.error_invalid_displayname));
            focusView = mDisplayNameView;
            cancel = true;
        }

        // Check if password and passwordConfirm Match
        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            cancel = true;
        }

        if (TextUtils.isEmpty(passwordConfirm)) {
            mPasswordConfirmView.setError(getString(R.string.error_field_required));
            cancel = true;
        }
        else if (!TextUtils.isEmpty(password) && !TextUtils.isEmpty(passwordConfirm)) {
            if (!TextUtils.equals(password, passwordConfirm)) {
                mPasswordView.setError(getString(R.string.error_not_matching_passwords));
                focusView = mPasswordView;
                cancel = true;
            }
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mRegistrationTask = new UserRegisterTask(username, displayName, password);
            mRegistrationTask.execute((Void) null);
        }
    }

    private boolean isUsernameValid(String username) {
        return username.length() < 16;
    }

    private boolean isPasswordValid(String password) {
        return password.length() < 24;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mRegisterFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.register, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public class UserRegisterTask extends AsyncTask<Void, Void, Boolean> {

        private final String mUsername;
        private final String mDisplayName;
        private final String mPassword;

        UserRegisterTask (String username, String displayName, String password) {
            mUsername = username;
            mDisplayName = displayName;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            String url = "http://143.215.109.184/t4j/create_user.php";
            System.out.println(url);
            List<NameValuePair> requestParams = new ArrayList<NameValuePair>();
            requestParams.add(new BasicNameValuePair("username", mUsername));
            requestParams.add(new BasicNameValuePair("password", mPassword));
            requestParams.add(new BasicNameValuePair("displayName", mDisplayName));

            JSONObject json = jsonParser.makeHttpRequest(url, "POST", requestParams);

            try {
                int success = json.getInt("success");
                return success == 1;
            } catch (JSONException e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mRegistrationTask = null;
            showProgress(false);

            if (success) {
                Intent lynxIntent = new Intent (getApplicationContext(), LinkedLoginActivity.class);
                startActivity(lynxIntent);
                finish();
            } else {
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mRegistrationTask = null;
            showProgress(false);
        }
    }
}
