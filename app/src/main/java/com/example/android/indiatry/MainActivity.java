package com.example.android.indiatry;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.NavUtils;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.PermissionRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
//import android.widget.Toolbar;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.android.volley.Response;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.firebase.ui.auth.AuthUI;
// import com.google.android.gms.common.api.Response;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import android.support.v7.widget.Toolbar;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private static final int RC_SIGN_IN = 6;
    private static final String ANONYMOUS = "anonymous";
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private String mUsername;
    private String email;
    private String uidFirebase;
    private WebView webView;
    private static final String TAG = "tag";
    private Button buttonLogin;
    private TextView loginTextView;
    private Context mContext;
    private DrawerLayout mDrawerLayout;
    private GoogleSignInClient mGoogleSignInClient;
    private CallbackManager mCallbackManager;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private Button loginEmailButton;
    private Session session;
    private String commonUsername;
    private String commonToken;
    // private SessionManager session;
    private String username;
    private String userToken;
    private String tokenFacebook;
    private String sessionGoogleUsername;
    private String sessionGoogleToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        //     webView = new WebView(this);
        setContentView(R.layout.activity_login);
        //     webView = (WebView) findViewById(R.id.webview);
//        webView.addJavascriptInterface(new WepAppInterface(this), "Android");
       /* loginTextView = (TextView) findViewById(R.id.login_tv);
        buttonLogin = (Button) findViewById(R.id.button_login);
       buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFirebaseAuth.addAuthStateListener(mAuthStateListener);
            }
        }); */
        setNavigationViewListener();
     /*    if (savedInstanceState == null) {

            webView.loadUrl("http://carpediemsocial.com/nigeria/logintest.html");
        }
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setAllowContentAccess(true);
        webSettings.setAllowFileAccessFromFileURLs(true);
        webSettings.setAllowUniversalAccessFromFileURLs(true);
        webView.setWebViewClient(new MyWebViewClient()); */
        mFirebaseAuth = FirebaseAuth.getInstance();
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    onSignedInInitialize(user.getDisplayName());
                    email = user.getEmail();
                    uidFirebase = user.getUid();

                    user.getIdToken(true)
                            .addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                                @Override
                                public void onComplete(@NonNull Task<GetTokenResult> task) {
                                    if (task.isSuccessful()) {
                                        String idToken = task.getResult().getToken();
                                    } else {
                                        Log.d(LOG_TAG, "Id token error message", task.getException());
                                    }
                                }
                            });
                   // sendNetworkRequest(email);
                } else {
                    onSignedOutCleanUp();
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setIsSmartLockEnabled(false)
                                    .setAvailableProviders(Arrays.asList(
                                            new AuthUI.IdpConfig.GoogleBuilder().build(),
                                            new AuthUI.IdpConfig.FacebookBuilder().build()))
                                    .build(),
                            RC_SIGN_IN);
                }
            }
        };



        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();

        if (actionbar !=null) {
            actionbar.setDisplayHomeAsUpEnabled(true);
            actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);
        }

        mDrawerLayout = findViewById(R.id.drawer_layout);

        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);

        findViewById(R.id.sign_in_button).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                switch(v.getId()) {
                    case R.id.sign_in_button:
                        signIn();
                        break;
                }
            }
        });
               // signInButton.setSize(signInButton.SIZE_STANDARD);


        mCallbackManager = CallbackManager.Factory.create();
        LoginButton loginButton = findViewById(R.id.login_button);
        loginButton.setReadPermissions("email", "public_profile");
        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "facebook:onSuccess" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook:onError", error);
            }
        });

        editTextEmail = findViewById(R.id.et_email_address);
        editTextPassword = findViewById(R.id.et_password);

        loginEmailButton = (Button) findViewById(R.id.bt_login);

        loginEmailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String email = editTextEmail.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();

                if(TextUtils.isEmpty(email) && TextUtils.isEmpty(password) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password))
                {
                    Toast.makeText(MainActivity.this, "Please enter all the required details", Toast.LENGTH_SHORT).show();
                    }

                else if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
                    sendNetworkRequest(email, password);
                }
            }
        });

        session = new Session(this);
// String userSession = session.getusename();
//String tokenSession = session.getusertoken();

       // session = new SessionManager(getApplicationContext());
       // HashMap<String, String> user = session.getUserDetails();
       // String userid = user.get(SessionManager.KEY_userid);
        }

        private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    // NavigationView click events
    private void setNavigationViewListener() {
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN && resultCode == RESULT_OK) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);

            mCallbackManager.onActivityResult(requestCode, resultCode, data);
            Toast.makeText(this, "Signed In.", Toast.LENGTH_SHORT).show();
        } else if (requestCode == RESULT_CANCELED) {
            Toast.makeText(this, "Sign in cancelled.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            updateUI(account);
        }catch (ApiException e) {
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            updateUI(null);
        }
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            String idToken;
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mFirebaseAuth.getCurrentUser();
                            String usernameFacebook = user.getDisplayName();
                            String personFBEmail = user.getEmail();
                            String fbUserId = user.getUid();
                            user.getIdToken(true)
                                    .addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<GetTokenResult> task) {
                                            if (task.isSuccessful()) {
                                                tokenFacebook = task.getResult().getToken();
                                            } else {
                                                Log.d(LOG_TAG, "Id token error message", task.getException());
                                            }
                                        }
                                    });
                            session.setusename(usernameFacebook);
                            session.setusertoken(tokenFacebook);
                            String userSession = session.getusename();
                            String tokenSession = session.getusertoken();
                            Log.d(TAG, "tokenSession" + tokenSession);
                            Log.d(TAG, "userSession" + userSession);

                            updateUI(user);
                        } else {
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed", Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });

    }

    public void updateUI(Object o) {
        FirebaseUser user = mFirebaseAuth.getCurrentUser();
        if (user != null) {
            username = user.getDisplayName();
            session.setusename(username);
            String commonUsername = session.getusename();
            user.getIdToken(true)
                    .addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                        @Override
                        public void onComplete(@NonNull Task<GetTokenResult> task) {
                            if (task.isSuccessful()) {
                                String idToken = task.getResult().getToken();
                                session.setusertoken(idToken);
                                commonToken = session.getusertoken();
                                String userSession = session.getusename();
                                Log.d(TAG, "tokenSession" + commonToken);
                                Log.d(TAG, "userSession" + userSession);
                            } else {
                                Log.d(LOG_TAG, "Id token error message", task.getException());
                            }
                        }
                    });
            }
    }

    private void onSignedInInitialize (String username){
        mUsername = username;
    }

    private void onSignedOutCleanUp () {
        mUsername = ANONYMOUS;

    }

  /*  @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    } */

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
    /*        case R.id.sign_out_menu:
                AuthUI.getInstance().signOut(this);
                return true; */
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                // NavUtils.navigateUpFromSameTask(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mAuthStateListener != null) {
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onStart() {
        super.onStart();
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account != null) {
            String usernameGoogle = account.getDisplayName();
            String personEmail = account.getEmail();
            String personId = account.getId();
            userToken = account.getIdToken();
            session.setusename(usernameGoogle);
            session.setusertoken(userToken);
            String sessionGoogleToken = session.getusertoken();
            String sessionGoogleUsername = session.getusename();
            Log.d(TAG, "sessionGoogleToken" + sessionGoogleToken);
            Log.d(TAG, "sessionGoogleUsername" + sessionGoogleUsername);

        }
        updateUI(account);
        FirebaseUser currentFacebookUser = mFirebaseAuth.getCurrentUser();
        updateUI(currentFacebookUser);

    }

    private void sendNetworkRequest(String emailU, String passwordU) {

        // final String userName = String.valueOf(mUsername);
        final String userEmail = String.valueOf(emailU);
        final String userPassword = String.valueOf(passwordU);
        final String name = "test";
        final String mobile = "12345678";
        final String address = "Dezven, Bhopal";
        final String email = userEmail;
        final String password = userPassword;

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://carpediemsocial.com/onlineshop/api/login.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(getApplicationContext(), "Post successful.", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "Response:" + response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Error Occurred", Toast.LENGTH_SHORT).show();

            }

        }) { @Override
        protected Map<String, String> getParams() {
            Map<String, String> params = new HashMap<String, String>();
            params.put("log_email", userEmail);
            params.put("log_password", userPassword);
            return params;
        }

        };

        queue.add(stringRequest);
    }

    

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        // set item as selected to persist highlight
        item.setChecked(true);

        // close drawer when item is tapped
        mDrawerLayout.closeDrawers();

        // Add code here to update the UI based on the item selected
        // For example, swap UI fragments here
        switch(id) {

            case R.id.nav_home:
             Toast.makeText(this, "NavigationClick", Toast.LENGTH_SHORT).show();
                //   webView.loadUrl("http://indiatry.com/");
                break;
            case R.id.nav_login:
                Toast.makeText(this, "NavigationClick", Toast.LENGTH_SHORT).show();
                //   webView.loadUrl("http://indiatry.com/login.php");
                break;
            case R.id.nav_register:
                Toast.makeText(this, "NavigationClick", Toast.LENGTH_SHORT).show();
                //webView.loadUrl("http://indiatry.com/registers.php");
                break;
            case R.id.nav_about_industry:
                Toast.makeText(this, "NavigationClick", Toast.LENGTH_SHORT).show();
                //webView.loadUrl("http://indiatry.com/pages/About-Us");
                break;
            case R.id.sign_out_menu:
                FirebaseAuth.getInstance().signOut();
                AuthUI.getInstance().signOut(this);
                Toast.makeText(this, "Signed out", Toast.LENGTH_SHORT).show();
                break;
        }
        return false;
    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url.startsWith("mailto:")) {
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse(url));
                startActivity(emailIntent);
                return true;
            } else if (url.startsWith("tel:")) {
                Intent telephoneIntent = new Intent(Intent.ACTION_DIAL, Uri.parse(url));
                startActivity(telephoneIntent);
                return true;
            }
            else
                view.loadUrl(url);
            return true;
        }
    }

 /*   public class WepAppInterface {

    WepAppInterface(Context c) {

        mContext = c;
    }
    @JavascriptInterface
    public void showToast(String toast) {

        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }
    } */
}
