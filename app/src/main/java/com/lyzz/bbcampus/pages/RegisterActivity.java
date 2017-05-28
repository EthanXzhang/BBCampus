package com.lyzz.bbcampus.pages;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.SpannedString;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.BackgroundColorSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.lyzz.bbcampus.R;
import com.lyzz.bbcampus.service.AysncTaskPost;

import org.json.JSONException;
import org.json.JSONObject;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class RegisterActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // UI references.
    private AutoCompleteTextView mEmailView;    //自动填充 账号输入栏
    private EditText mPasswordView;             //密码输入栏
    private EditText rePasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private String username,phone,avatar,birth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Intent intent=getIntent();
        username=intent.getStringExtra("username");
        phone=intent.getStringExtra("phone");
        avatar=intent.getStringExtra("avatar");
        birth=intent.getStringExtra("birth");
        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        populateAutoComplete();
        mPasswordView=(EditText)findViewById(R.id.password);
        rePasswordView = (EditText) findViewById(R.id.checkpassword);
        rePasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptRegister();
                    return true;
                }
                return false;
            }
        });

        Button mEmailRegisterButton = (Button) findViewById(R.id.register);//登陆按钮
        mEmailRegisterButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptRegister();
            }
        });//注册方法attemptLogin
        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
        Button back=(Button)findViewById(R.id.back);
        back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(RegisterActivity.this,RegisterDetail.class);
                startActivity(intent);
            }
        });

    }
    //自动填充
    private void populateAutoComplete() {
        if (!mayRequestContacts()) {
            return;
        }

        getLoaderManager().initLoader(0, null, this);
    }
    //检索
    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(mEmailView, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
                        }
                    });
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateAutoComplete();
            }
        }
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptRegister() {
        if (mAuthTask != null) {
            return;
        }

        // 重置错误提示
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();
        String repassword=rePasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // 检查密码
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }
        if (!TextUtils.isEmpty(repassword)&&!(repassword.equals(password))) {
            rePasswordView.setError("两次密码不一致");
            focusView = rePasswordView;
            cancel = true;
        }

        // 检查账户名
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserLoginTask(email, password);
            mAuthTask.execute((Void) null);
        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return true;
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
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
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
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
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(RegisterActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
    }


    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }

    //多线程访问服务器
    public class UserLoginTask extends AsyncTask<Void, Void, String> {

        private final String mEmail;
        private final String mPassword;
        private String content;
        private String url="http://112.74.41.59:3000/v1/user/register";

        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
            //JSON封装登陆的用户数据
            JSONObject UserKey=new JSONObject();
            try {
                UserKey.put("username",mEmail.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                UserKey.put("password",mPassword.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            //生成表单数据
            content="email="+mEmail.toString() + "&nickname="+username+"&password=" + mPassword.toString()+"&phone="+phone;
        }

        @Override
        protected String doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            String result;
            try {
                // Simulate network access.
                //服务器112.74.41.59:3000/v1/user/login
                result = AysncTaskPost.postUserLogin(url,content);
                Thread.sleep(200);
            } catch (InterruptedException e) {
                return "error";
            }

            // TODO: register the new account here.
            return result;
        }

        @Override
        protected void onPostExecute(final String success) {
            mAuthTask = null;
            showProgress(false);
            if (success.equals("error")) {
                Toast.makeText(RegisterActivity.this,"网络未连接", Toast.LENGTH_SHORT).show();
            }
            else if(success.equals("false"))
            {
                Toast.makeText(RegisterActivity.this,"遇到异常，请尝试重启或联系我们", Toast.LENGTH_SHORT).show();
            }
            else
            {
                JSONObject object=null;
                JSONObject detail=null;
                String code;
                try {
                    object=new JSONObject(success);
                    code=object.getString("code");
                    if(code.equals("201"))
                    {
                        detail=object.getJSONObject("message");
                        userDetail(detail);
                        Intent intent=new Intent(RegisterActivity.this,OwnerPage.class);
                        startActivity(intent);
                    }else
                    {
                        Toast.makeText(RegisterActivity.this,object.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d("JSON success",success);
                }
            }
        }

        private void userDetail(JSONObject detail)
        {
            JSONObject object=detail;
            /*
                JSON返回的数据标准格式
             */
            /*
            返回样例JSON
            "{\"userNickName\":\"Ethan\",\"userEmail\":\"376367820@qq.com\",\"userId\":68,\"userPassword\":\"123456\",\"_id\":\"58f0607539069e306678be7d\",\"__v\":0,\"userRegisterTime\":\"2017-04-14T05:28:54.111Z\",\"userBonus\":0,\"userBangBiValue\":0,\"userFollow\":[],\"userFans\":[],\"userCredit\":0,\"userReputation\":1,\"userSafeQuestion\":{\"answer\":\"\",\"question\":\"\"},\"userStatus\":1,\"userSkill\":[],\"userFavor\":[],\"userLabel\":[\"\"],\"userFocus\":[\"\"],\"userLevel\":1,\"userOrganization\":\"\",\"userSchool\":\"\",\"userAddress\":{\"other\":\"\",\"city\":\"\",\"province\":\"\"},\"userPhone\":0,\"userBirth\":\"2017-04-14T05:39:01.556Z\",\"userAvatar\":\"\",\"userSex\":\"male\"}";
             */
            try {
                SharedPreferences userState=getApplicationContext().getSharedPreferences("userstate",0);
                SharedPreferences.Editor editor=userState.edit();
                editor.putInt("login",1);//0未登录；1已登录未更新资料；2登陆且更新资料
                editor.putString("userAvatar",object.getString("userAvatar"));
                editor.putString("userEmail",object.getString("userEmail"));
                editor.putString("userId",object.getString("userId"));
                editor.putString("userNickName",object.getString("userNickName"));
                editor.putString("userBirth",object.getString("userBirth"));
                editor.putString("userOrganization",object.getString("userOrganization"));
                editor.putString("userPhone",object.getString("userPhone"));
                editor.putString("userSchool",object.getString("userSchool"));
                editor.putString("userSex",object.getString("userSex"));
                editor.putString("userBonus",object.getString("userBonus"));
                editor.putString("userBangBiValue",object.getString("userBangBiValue"));
                editor.putString("userFollow",object.getString("userFollow"));
                editor.putString("userFans",object.getString("userFans"));
                editor.putString("userCredit",object.getString("userCredit"));
                editor.putString("userReputation",object.getString("userReputation"));
                editor.putString("userStatus",object.getString("userStatus"));
                editor.putString("userSkill",object.getString("userSkill"));
                editor.putString("userFavor",object.getString("userFavor"));
                editor.putString("userLabel",object.getString("userLabel"));
                editor.putString("userFocus",object.getString("userFocus"));
                editor.putString("userLevel",object.getString("userLevel"));
                editor.commit();
                Toast.makeText(RegisterActivity.this,"登陆成功", Toast.LENGTH_SHORT).show();
            }catch (JSONException e){
                e.printStackTrace();
            }
        }
        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}

