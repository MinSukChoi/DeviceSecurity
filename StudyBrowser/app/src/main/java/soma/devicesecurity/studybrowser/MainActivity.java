package soma.devicesecurity.studybrowser;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @Bind(R.id.webView) WebView webView;
    @Bind(R.id.editText_address) EditText editText_address;
    @Bind(R.id.imageView_block) ImageView imageView_block;
    @OnClick(R.id.button_move)
    public void move() {
        String url = editText_address.getText().toString();
        webView.loadUrl(url);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        webView.setWebViewClient(new WebClient());
        WebSettings set = webView.getSettings();
        set.setJavaScriptEnabled(true);
        set.setBuiltInZoomControls(true);
        webView.loadUrl("http://www.naver.com");
    }

    class WebClient extends WebViewClient {
        private int delay = 0;
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {

            if (imageView_block.getVisibility() == View.INVISIBLE) {
                imageView_block.setVisibility(View.VISIBLE);
                Log.v("TEST", "HEHE");
                delay += 1000;
            }

            //isRedirected = false;
        }
        public void onPageFinished(WebView view, String url) {
            if(imageView_block.getVisibility() == View.VISIBLE) {
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        imageView_block.setVisibility(View.INVISIBLE);
                    }
                }, delay);
            Log.v("TEST", ""+delay);
            }
        }

    }
}
