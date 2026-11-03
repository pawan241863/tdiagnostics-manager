
package com.example.tdiagnosticsmanager;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.webkit.*;
import android.widget.*;

public class MainActivity extends Activity {

    private static final String HOME =
        "https://tdiagnostics.telangana.gov.in/";

    private static final String PATIENT_EDIT =
        "https://tdiagnostics.telangana.gov.in/LabUser/UniqueDataReportNew.aspx";

    private static final String DELETE_AADHAAR =
        "https://tdiagnostics.telangana.gov.in/LabUser/UpdateDataReport.aspx";

    private static final String LOGOUT_URL =
        "https://tdiagnostics.telangana.gov.in/logout.aspx";

    private LinearLayout root;
    private WebView webView;
    private boolean loggingOut = false;

    private final String[] accounts = {
        "gadnew", "sidnew", "newdb2", "ntc"
    };

    private int dp(int n) {
        return (int)(n * getResources()
            .getDisplayMetrics().density);
    }

    private Button makeButton(String text) {
        Button b = new Button(this);
        b.setText(text);
        b.setAllCaps(false);
        return b;
    }

    private void setupRoot() {
        root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(16), dp(16), dp(16), dp(16));
        root.setBackgroundColor(Color.rgb(245,247,251));
        setContentView(root);
    }

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        showLogin();
    }

    private void showLogin() {
        loggingOut = false;
        setupRoot();

        TextView title = new TextView(this);
        title.setText("T-Diagnostics Manager");
        title.setTextSize(25);
        title.setTextColor(Color.rgb(20,45,80));
        title.setGravity(Gravity.CENTER);

        root.addView(title,
            new LinearLayout.LayoutParams(-1, dp(65)));

        TextView label = new TextView(this);
        label.setText("Select Login Account");
        root.addView(label);

        Spinner spinner = new Spinner(this);
        spinner.setAdapter(new ArrayAdapter<>(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            accounts
        ));
        root.addView(spinner);

        EditText username = new EditText(this);
        username.setSingleLine(true);
        username.setHint("User ID");
        username.setText(accounts[0]);
        root.addView(username);

        EditText password = new EditText(this);
        password.setSingleLine(true);
        password.setHint("Password");
        password.setInputType(129);
        root.addView(password);

        spinner.setOnItemSelectedListener(
            new android.widget.AdapterView.OnItemSelectedListener() {

                public void onNothingSelected(
                    android.widget.AdapterView<?> p) {}

                public void onItemSelected(
                    android.widget.AdapterView<?> p,
                    View v, int position, long id) {

                    username.setText(accounts[position]);
                    password.setText("");
                }
            }
        );

        Button login = makeButton("Login");
        root.addView(login);

        login.setOnClickListener(v -> {
            String u = username.getText().toString().trim();
            String p = password.getText().toString();

            if (u.isEmpty() || p.isEmpty()) {
                Toast.makeText(this,
                    "Enter User ID and Password",
                    Toast.LENGTH_SHORT).show();
                return;
            }

            openPortal(u, p);
        });
    }

    private String quote(String s) {
        return "'" + s.replace("\\", "\\\\")
            .replace("'", "\\'")
            .replace("\n", "")
            .replace("\r", "") + "'";
    }

    private void openPortal(String user, String pass) {

        setupRoot();

        LinearLayout toolbar = new LinearLayout(this);

        Button edit = makeButton("Patient Edit");
        Button aadhaar = makeButton("Delete Aadhaar");
        Button logout = makeButton("Logout");

        toolbar.addView(edit,
            new LinearLayout.LayoutParams(0, dp(52), 1));

        toolbar.addView(aadhaar,
            new LinearLayout.LayoutParams(0, dp(52), 1));

        toolbar.addView(logout,
            new LinearLayout.LayoutParams(0, dp(52), 1));

        root.addView(toolbar);

        webView = new WebView(this);

        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setSupportZoom(true);
        settings.setBuiltInZoomControls(true);
        settings.setDisplayZoomControls(false);

        webView.setWebChromeClient(new WebChromeClient());

        webView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                if (loggingOut &&
                    url != null &&
                    url.toLowerCase().contains("/logout.aspx")) {

                    view.clearHistory();
                    view.clearCache(true);

                    view.postDelayed(() -> {
                        if (webView != null) {
                            webView.destroy();
                            webView = null;
                        }

                        showLogin();

                    }, 500);

                    return;
                }

                if (!loggingOut && HOME.equals(url)) {

                    String js =
                        "(function(){" +
                        "var u=document.getElementById('UserName');" +
                        "var p=document.getElementById('Password');" +
                        "if(u&&p){" +
                        "u.value=" + quote(user) + ";" +
                        "p.value=" + quote(pass) + ";" +
                        "var b=document.getElementById('LoginButton');" +
                        "if(b)b.click();" +
                        "}" +
                        "})();";

                    view.evaluateJavascript(js, null);
                }
            }
        });

        root.addView(webView,
            new LinearLayout.LayoutParams(-1, 0, 1));

        edit.setOnClickListener(v ->
            webView.loadUrl(PATIENT_EDIT));

        aadhaar.setOnClickListener(v ->
            webView.loadUrl(DELETE_AADHAAR));

        logout.setOnClickListener(v -> {

            if (webView != null && !loggingOut) {
                loggingOut = true;
                webView.loadUrl(LOGOUT_URL);
            }
        });

        webView.loadUrl(HOME);
    }

    @Override
    public void onBackPressed() {

        if (webView != null && webView.canGoBack()) {
            webView.goBack();
        } else {
            showLogin();
        }
    }

    @Override
    protected void onDestroy() {
        if (webView != null) {
            webView.stopLoading();
            webView.destroy();
            webView = null;
        }
        super.onDestroy();
    }
}
