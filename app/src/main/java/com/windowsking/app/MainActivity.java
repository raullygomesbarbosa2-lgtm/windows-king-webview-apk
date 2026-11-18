package com.windowsking.app;

import android.app.Activity;
import android.app.DownloadManager;
import android.os.Bundle;
import android.os.Environment;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.*;

public class MainActivity extends Activity {
    private final int bg = Color.rgb(21, 27, 41);
    private final int panel = Color.rgb(34, 45, 66);
    private final int blue = Color.rgb(40, 125, 235);
    private LinearLayout root;
    private WebView web;
    private EditText address;
    private LinearLayout browserPanel;

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        getWindow().setStatusBarColor(bg);
        getWindow().setNavigationBarColor(bg);
        showHome();
    }

    private void showHome() {
        root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(20), dp(24), dp(20), dp(18));
        root.setBackgroundColor(bg);

        LinearLayout header = new LinearLayout(this);
        header.setGravity(Gravity.CENTER_VERTICAL);
        TextView title = label("WINDOWS KING", 25, Color.WHITE, true);
        header.addView(title, new LinearLayout.LayoutParams(0, -2, 1));
        TextView settings = label("⚙", 27, Color.WHITE, false);
        settings.setOnClickListener(v -> openAndroidSettings());
        header.addView(settings);
        root.addView(header);

        TextView sub = label("SEU AMBIENTE DIGITAL", 11, 0xFFA9C9FF, true);
        LinearLayout.LayoutParams subp = new LinearLayout.LayoutParams(-1, -2);
        subp.topMargin = dp(5);
        root.addView(sub, subp);

        LinearLayout welcome = new LinearLayout(this);
        welcome.setOrientation(LinearLayout.VERTICAL);
        welcome.setPadding(dp(18), dp(18), dp(18), dp(18));
        welcome.setBackground(round(panel, 18));
        TextView w1 = label("BEM-VINDO", 11, 0xFFA9C9FF, true);
        TextView w2 = label("Seu espaço. Suas regras.", 21, Color.WHITE, true);
        TextView w3 = label("Navegue e organize seus arquivos.", 13, 0xFFD0D8E8, false);
        welcome.addView(w1); welcome.addView(w2); welcome.addView(w3);
        LinearLayout.LayoutParams wp = new LinearLayout.LayoutParams(-1, -2);
        wp.topMargin = dp(22);
        root.addView(welcome, wp);

        String[][] apps = {
            {"🌐", "Internet"}, {"📁", "Arquivos"}, {"⬇", "Downloads"},
            {"🎮", "Jogos"}, {"▦", "Aplicativos"}, {"⚙", "Configurações"}
        };
        int[] colors = {0xFF287DEB, 0xFFD99B32, 0xFF239C79, 0xFF8457DC, 0xFF238DAD, 0xFF68758B};
        for (int row = 0; row < 2; row++) {
            LinearLayout line = new LinearLayout(this);
            line.setGravity(Gravity.CENTER);
            for (int col = 0; col < 3; col++) {
                final int index = row * 3 + col;
                LinearLayout card = new LinearLayout(this);
                card.setOrientation(LinearLayout.VERTICAL);
                card.setGravity(Gravity.CENTER);
                card.setPadding(dp(4), dp(12), dp(4), dp(12));
                card.setBackground(round(0xFF263247, 14));
                TextView icon = label(apps[index][0], 27, Color.WHITE, true);
                icon.setGravity(Gravity.CENTER);
                icon.setBackground(round(colors[index], 14));
                card.addView(icon, new LinearLayout.LayoutParams(dp(48), dp(48)));
                TextView name = label(apps[index][1], 11, Color.WHITE, true);
                name.setGravity(Gravity.CENTER);
                LinearLayout.LayoutParams np = new LinearLayout.LayoutParams(-1, -2);
                np.topMargin = dp(8);
                card.addView(name, np);
                card.setOnClickListener(v -> onTile(index));
                LinearLayout.LayoutParams cp = new LinearLayout.LayoutParams(0, dp(112), 1);
                cp.setMargins(dp(4), dp(8), dp(4), dp(8));
                line.addView(card, cp);
            }
            root.addView(line);
        }

        TextView foot = label("WINDOWS KING • ANDROID", 10, 0xFF9AA8BD, true);
        foot.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams fp = new LinearLayout.LayoutParams(-1, -2);
        fp.topMargin = dp(18);
        root.addView(foot, fp);
        setContentView(root);
    }

    private void onTile(int index) {
        switch (index) {
            case 0: showBrowser("https://www.google.com"); break;
            case 1:
                try {
                    IntentHelper.openFiles(this);
                } catch (Exception e) { toast("Não foi possível abrir os arquivos."); }
                break;
            case 2: showBrowser("https://www.google.com/search?q=meus+downloads"); break;
            case 3: showInfo("Jogos", "A área de jogos será adicionada em uma próxima versão."); break;
            case 4: showInfo("Aplicativos", "A área de aplicativos será adicionada em uma próxima versão."); break;
            case 5: openAndroidSettings(); break;
        }
    }

    private void showBrowser(String url) {
        browserPanel = new LinearLayout(this);
        browserPanel.setOrientation(LinearLayout.VERTICAL);
        browserPanel.setBackgroundColor(bg);

        LinearLayout top = new LinearLayout(this);
        top.setPadding(dp(8), dp(8), dp(8), dp(8));
        top.setGravity(Gravity.CENTER_VERTICAL);
        top.setBackgroundColor(panel);

        TextView back = navButton("‹");
        back.setOnClickListener(v -> { if (web != null && web.canGoBack()) web.goBack(); else showHome(); });
        top.addView(back);

        address = new EditText(this);
        address.setSingleLine(true);
        address.setTextSize(14);
        address.setTextColor(Color.WHITE);
        address.setHintTextColor(0xFFB5C0D0);
        address.setHint("Digite um endereço ou pesquisa");
        address.setPadding(dp(12), 0, dp(12), 0);
        address.setBackground(round(0xFF151B29, 22));
        address.setImeOptions(EditorInfo.IME_ACTION_GO);
        top.addView(address, new LinearLayout.LayoutParams(0, dp(44), 1));

        TextView go = navButton("Ir");
        go.setTextSize(14);
        go.setOnClickListener(v -> loadAddress());
        top.addView(go);
        browserPanel.addView(top);

        LinearLayout controls = new LinearLayout(this);
        controls.setGravity(Gravity.CENTER);
        controls.setPadding(dp(4), dp(4), dp(4), dp(4));
        controls.setBackgroundColor(0xFF1C2638);
        String[] names = {"Início", "Voltar", "Avançar", "Atualizar"};
        for (int i = 0; i < names.length; i++) {
            final int action = i;
            TextView b = label(names[i], 12, Color.WHITE, true);
            b.setGravity(Gravity.CENTER);
            b.setPadding(dp(8), dp(8), dp(8), dp(8));
            b.setOnClickListener(v -> {
                if (action == 0) showBrowser("https://www.google.com");
                else if (action == 1 && web.canGoBack()) web.goBack();
                else if (action == 2 && web.canGoForward()) web.goForward();
                else if (action == 3) web.reload();
            });
            controls.addView(b, new LinearLayout.LayoutParams(0, -2, 1));
        }
        browserPanel.addView(controls);

        web = new WebView(this);
        web.getSettings().setJavaScriptEnabled(true);
        web.getSettings().setDomStorageEnabled(true);
        web.getSettings().setLoadsImagesAutomatically(true);
        web.getSettings().setSupportZoom(true);
        web.getSettings().setBuiltInZoomControls(true);
        web.getSettings().setDisplayZoomControls(false);
        web.setWebChromeClient(new WebChromeClient());
        web.setWebViewClient(new WebViewClient() {
            @Override public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return false;
            }
            @Override public void onPageFinished(WebView view, String pageUrl) {
                if (address != null && pageUrl != null) address.setText(pageUrl);
            }
        });
        web.setDownloadListener(new DownloadListener() {
            @Override public void onDownloadStart(String url, String userAgent, String contentDisposition,
                                                  String mimetype, long contentLength) {
                try {
                    DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
                    request.setMimeType(mimetype);
                    request.addRequestHeader("User-Agent", userAgent);
                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                    request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,
                        android.webkit.URLUtil.guessFileName(url, contentDisposition, mimetype));
                    DownloadManager dm = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                    dm.enqueue(request);
                    toast("Download iniciado. Veja as notificações.");
                } catch (Exception e) { toast("Não foi possível iniciar o download."); }
            }
        });
        browserPanel.addView(web, new LinearLayout.LayoutParams(-1, 0, 1));
        setContentView(browserPanel);

        address.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_GO ||
                (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                loadAddress(); return true;
            }
            return false;
        });
        web.loadUrl(url);
    }

    private void loadAddress() {
        if (address == null || web == null) return;
        String value = address.getText().toString().trim();
        if (value.isEmpty()) return;
        if (!value.contains("://")) {
            if (value.contains(".") && !value.contains(" ")) value = "https://" + value;
            else value = "https://www.google.com/search?q=" + Uri.encode(value);
        }
        ((InputMethodManager)getSystemService(INPUT_METHOD_SERVICE))
            .hideSoftInputFromWindow(address.getWindowToken(), 0);
        web.loadUrl(value);
    }

    @Override public void onBackPressed() {
        if (web != null && web.canGoBack()) web.goBack();
        else if (web != null) { web = null; showHome(); }
        else super.onBackPressed();
    }

    private void openAndroidSettings() {
        try { startActivity(new android.content.Intent(android.provider.Settings.ACTION_SETTINGS)); }
        catch (Exception e) { toast("Configurações indisponíveis."); }
    }
    private void showInfo(String title, String message) {
        new android.app.AlertDialog.Builder(this).setTitle(title).setMessage(message)
            .setPositiveButton("OK", null).show();
    }
    private void toast(String s) { Toast.makeText(this, s, Toast.LENGTH_LONG).show(); }
    private TextView navButton(String s) {
        TextView t = label(s, 24, Color.WHITE, true);
        t.setGravity(Gravity.CENTER);
        t.setPadding(dp(10), 0, dp(10), 0);
        return t;
    }
    private TextView label(String s, int size, int color, boolean bold) {
        TextView t = new TextView(this);
        t.setText(s); t.setTextSize(size); t.setTextColor(color);
        if (bold) t.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        return t;
    }
    private android.graphics.drawable.GradientDrawable round(int color, int radius) {
        android.graphics.drawable.GradientDrawable d = new android.graphics.drawable.GradientDrawable();
        d.setColor(color); d.setCornerRadius(dp(radius)); return d;
    }
    private int dp(int n) { return (int)(n * getResources().getDisplayMetrics().density + 0.5f); }

    // Keeps the Android file picker native while the web browser is built into Windows King.
    static class IntentHelper {
        static void openFiles(Activity activity) {
            android.content.Intent i = new android.content.Intent(android.content.Intent.ACTION_OPEN_DOCUMENT);
            i.setType("*/*");
            i.addCategory(android.content.Intent.CATEGORY_OPENABLE);
            activity.startActivity(i);
        }
    }
}
