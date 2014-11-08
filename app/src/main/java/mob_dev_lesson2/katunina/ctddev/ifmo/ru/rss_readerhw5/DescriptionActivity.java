package mob_dev_lesson2.katunina.ctddev.ifmo.ru.rss_readerhw5;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;


public class DescriptionActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_description);
        Intent intent = getIntent();
        setTitle(intent.getStringExtra(FeedActivity.TITLE_EXTRA));
        WebView wv = ((WebView) findViewById(R.id.web_view));
        wv.getSettings().setDefaultTextEncodingName("utf-8");
        wv.loadDataWithBaseURL(null,getIntent().getStringExtra(FeedActivity.DESCRIPTION_EXTRA), "text/html",
                "en_US",null);
    }
}
