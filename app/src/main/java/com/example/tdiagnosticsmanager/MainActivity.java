package com.example.tdiagnosticsmanager;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.view.Gravity;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebSettings;
import android.widget.*;
import android.text.InputType;

public class MainActivity extends Activity {
    private static final String HOME = "https://tdiagnostics.telangana.gov.in/";
    private static final String PATIENT_EDIT = "https://tdiagnostics.telangana.gov.in/LabUser/UniqueDataReportNew.aspx";
    private static final String DELETE_AADHAAR = "https://tdiagnostics.telangana.gov.in/LabUser/UpdateDataReport.aspx";
    private WebView webView;
    private LinearLayout root;
    private EditText username, password;
    private String selectedUser = "gadnew";

    private final String[] accounts = {"gadnew", "sidnew", "newdb2", "ntc"};

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showLogin();
    }

    private int dp(int value) { return (int)(value * getResources().getDisplayMetrics().density); }

    private TextView label(String text, int size, int color) {
        TextView t = new TextView(this);
        t.setText(text); t.setTextSize(size); t.setTextColor(color);
        return t;
    }

    private Button button(String text) {
        Button b = new Button(this);
        b.setText(text);
        b.setAllCaps(false);
        return b;
    }

    private void setupRoot() {
        root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(20), dp(20), dp(20), dp(20));
        root.setBackgroundColor(Color.rgb(245,247,251));
        setContentView(root);
    }

    private void showLogin() {
        setupRoot();
        TextView title = label("T-Diagnostics Manager", 25, Color.rgb(20,45,80));
        title.setTypeface(null, Typeface.BOLD);
        title.setGravity(Gravity.CENTER);
        root.addView(title, new LinearLayout.LayoutParams(-1, dp(60)));
        TextView sub = label("Telangana Health Services", 14, Color.DKGRAY);
        sub.setGravity(Gravity.CENTER);
        root.addView(sub, new LinearLayout.LayoutParams(-1, dp(35)));

        TextView accountLabel = label("Select login account", 15, Color.DKGRAY);
        root.addView(accountLabel);
        Spinner spinner = new Spinner(this);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, accounts);
        spinner.setAdapter(adapter);
        root.addView(spinner, new LinearLayout.LayoutParams(-1, dp(52)));
        username = new EditText(this);
        username.setSingleLine(true);
        username.setHint("User ID");
        username.setText(selectedUser);
        root.addView(username, new LinearLayout.LayoutParams(-1, dp(55)));
        password = new EditText(this);
        password.setSingleLine(true);
        password.setHint("Password");
        password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        root.addView(password, new LinearLayout.LayoutParams(-1, dp(55)));

        spinner.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            public void onNothingSelected(android.widget.AdapterView<?> p) {}
            public void onItemSelected(android.widget.AdapterView<?> p, View v, int position, long id) {
                selectedUser = accounts[position];
                username.setText(selectedUser);
                password.setText("");
            }
        });

        TextView note = label("For security, passwords are not saved in the app. Enter the password for the selected account.", 12, Color.GRAY);
        root.addView(note, new LinearLayout.LayoutParams(-1, dp(55)));

        Button login = button("Continue to T-Diagnostics");
        root.addView(login, new LinearLayout.LayoutParams(-1, dp(54)));
        login.setOnClickListener(v -> {
            String u = username.getText().toString().trim();
            String p = password.getText().toString();
            if (u.isEmpty() || p.isEmpty()) {
                Toast.makeText(this, "Enter User ID and password", Toast.LENGTH_SHORT).show();
                return;
            }
            selectedUser = u;
            openPortalWithLogin(u, p);
        });
    }

    private void openPortalWithLogin(String user, String pass) {
        setupRoot();
        LinearLayout toolbar = new LinearLayout(this);
        toolbar.setOrientation(LinearLayout.HORIZONTAL);
        Button editPage = button("Patient Edit");
        Button aadhaarPage = button("Delete Aadhaar");
        Button logout = button("Logout");
        toolbar.addView(editPage, new LinearLayout.LayoutParams(0, dp(52), 1));
        toolbar.addView(aadhaarPage, new LinearLayout.LayoutParams(0, dp(52), 1));
        toolbar.addView(logout, new LinearLayout.LayoutParams(0, dp(52), 1));
        root.addView(toolbar, new LinearLayout.LayoutParams(-1, dp(56)));
        editPage.setOnClickListener(v -> loadPage(PATIENT_EDIT));
        aadhaarPage.setOnClickListener(v -> loadPage(DELETE_AADHAAR));
        logout.setOnClickListener(v -> {
            if (webView != null) { webView.clearHistory(); webView.clearCache(true); webView.destroy(); webView = null; }
            showLogin();
        });

        webView = new WebView(this);
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setSupportZoom(true);
        settings.setBuiltInZoomControls(true);
        settings.setDisplayZoomControls(false);
        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new WebViewClient() {
            @Override public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                // Fill the portal's known login fields and submit. If the portal changes,
                // the page remains available for manual login.
                String js = "(function(){try{" +
                    "var u=document.getElementById('UserName');" +
                    "var p=document.getElementById('Password');" +
                    "if(u&&p){u.value=" + jsQuote(user) + ";p.value=" + jsQuote(pass) + ";" +
                    "var b=document.getElementById('LoginButton');if(b)b.click();}" +
                    "}catch(e){}})();";
                view.evaluateJavascript(js, null);
            }
        });
        root.addView(webView, new LinearLayout.LayoutParams(-1, 0, 1));
        webView.loadUrl(HOME);
    }

    private String jsQuote(String s) {
        return "'" + s.replace("\\", "\\\\").replace("'", "\\'").replace("\n", "").replace("\r", "") + "'";
    }

    private void showDashboard() {
        setupRoot();
        TextView title = label("T-Diagnostics", 24, Color.rgb(20,45,80));
        title.setTypeface(null, Typeface.BOLD);
        root.addView(title);
        TextView subtitle = label("Choose an action", 14, Color.GRAY);
        root.addView(subtitle);
        Button edit = button("Patient Details Edit");
        Button aadhaar = button("Delete Aadhaar");
        Button logout = button("Logout");
        root.addView(edit, new LinearLayout.LayoutParams(-1, dp(58)));
        root.addView(aadhaar, new LinearLayout.LayoutParams(-1, dp(58)));
        root.addView(logout, new LinearLayout.LayoutParams(-1, dp(58)));
        edit.setOnClickListener(v -> loadPage(PATIENT_EDIT));
        aadhaar.setOnClickListener(v -> loadPage(DELETE_AADHAAR));
        logout.setOnClickListener(v -> {
            if (webView != null) { webView.clearHistory(); webView.clearCache(true); webView.destroy(); webView = null; }
            showLogin();
        });
    }

    private void loadPage(String url) {
        if (webView == null) {
            setupRoot();
            webView = new WebView(this);
            webView.getSettings().setJavaScriptEnabled(true);
            webView.getSettings().setDomStorageEnabled(true);
            webView.setWebViewClient(new WebViewClient());
            root.addView(webView, new LinearLayout.LayoutParams(-1, 0, 1));
        }
        webView.loadUrl(url);
    }

    @Override public void onBackPressed() {
        if (webView != null && webView.canGoBack()) webView.goBack();
        else showLogin();
    }
}
