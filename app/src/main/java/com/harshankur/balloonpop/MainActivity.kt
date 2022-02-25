package com.harshankur.balloonpop

import android.content.ActivityNotFoundException
import android.content.Context
import android.os.Bundle
import android.webkit.JavascriptInterface
import androidx.core.content.ContextCompat.startActivity
import android.content.Intent
import android.net.Uri
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent

class MainActivity : AppCompatActivity() {
    private lateinit var webView: WebView

    class JavaScriptShareInterface(private val mContext: Context) {
        @JavascriptInterface
        fun nativeShare(title: String, text: String, url: String) {
            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TITLE, title)
                putExtra(Intent.EXTRA_TEXT, "$text $url")
                type = "text/plain"
            }
            val shareIntent = Intent.createChooser(sendIntent, null)
            startActivity(mContext, shareIntent, null)
        }

        @JavascriptInterface
        fun nativeTwitterShare(url: String) {
            try {
                Intent(Intent.ACTION_VIEW).also {
                    it.setPackage("com.twitter.android")
                    it.data = Uri.parse(url)
                    mContext.startActivity(it)
                }
            } catch (e: ActivityNotFoundException) {
                Intent(Intent.ACTION_VIEW).also {
                    it.data = Uri.parse(url)
                    mContext.startActivity(it)
                }
            }
        }

        @JavascriptInterface
        fun nativeOpenBrowserLink(url: String) {
            Intent(Intent.ACTION_VIEW).also {
                it.data = Uri.parse(url)
                mContext.startActivity(it)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        webView = findViewById<WebView>(R.id.webview)
        webView.settings.setJavaScriptEnabled(true)
        webView.settings.setDomStorageEnabled(true)
        webView.addJavascriptInterface(JavaScriptShareInterface(this), "AndroidShareHandler");

        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                if (url != null) {
                    view?.loadUrl(url)
                }
                return true
            }
        }
        webView.loadUrl("https://balloonpop.xyz?device=android")
    }

    override fun onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack()
        } else {
            super.onBackPressed()
        }
    }
}