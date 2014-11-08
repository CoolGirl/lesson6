package mob_dev_lesson2.katunina.ctddev.ifmo.ru.rss_readerhw5;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import java.util.List;


public class FeedActivity extends ListActivity {

    public static final String EXTRA_CHANNEL_ID = "extra_channel_id";

    private long channelId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        channelId = getIntent().getLongExtra(EXTRA_CHANNEL_ID, -1);
        if (channelId == -1) finish();
        setListAdapter(new SimpleCursorAdapter(this, android.R.layout.simple_list_item_2, getContentResolver().query(
                Uri.parse(FeedContentProvider.AUTHORITY
                        + "/news/" + channelId), null, null, null, null),
                new String[] {DBAdapter.KEY_NEWS_TITLE, DBAdapter.KEY_NEWS_DESCRIPTION}, new int[] {android.R.id.text1, android.R.id.text2}, 0));
        FeedFetchingService.startActionUpdateChannel(this, channelId);
    }

    public static final String LINK_EXTRA = "link_extra";
    public static final String DESCRIPTION_EXTRA = "description_extra";
    public static final String TITLE_EXTRA = "title_extra";

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        FeedItem item = ((FeedItem) l.getAdapter().getItem(position));
        Intent intent = new Intent(this, DescriptionActivity.class);
        intent.putExtra(LINK_EXTRA,item.link);
        intent.putExtra(DESCRIPTION_EXTRA, item.description);
        intent.putExtra(TITLE_EXTRA, item.title);
        startActivity(intent);
    }
}
