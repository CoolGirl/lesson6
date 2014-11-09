package ru.ifmo.ctddev.katununa.rss_reader_hw6;

import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;


public class FeedActivity extends ListActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    public static final String EXTRA_CHANNEL_ID = "extra_channel_id";

    private long channelId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        channelId = getIntent().getLongExtra(EXTRA_CHANNEL_ID, -1);
        if (channelId == -1) finish();
        fillData();
        FeedFetchingService.startActionUpdateChannel(this, channelId);
    }

    public static final String LINK_EXTRA = "link_extra";
    public static final String DESCRIPTION_EXTRA = "description_extra";
    public static final String TITLE_EXTRA = "title_extra";

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Cursor item = ((Cursor) l.getAdapter().getItem(position));
        Intent intent = new Intent(this, DescriptionActivity.class);
        intent.putExtra(LINK_EXTRA, item.getString(item.getColumnIndex(DBAdapter.KEY_NEWS_URL)));
        intent.putExtra(DESCRIPTION_EXTRA, item.getString(item.getColumnIndex(DBAdapter.KEY_NEWS_DESCRIPTION)));
        intent.putExtra(TITLE_EXTRA, item.getString(item.getColumnIndex(DBAdapter.KEY_NEWS_TITLE)));
        startActivity(intent);
    }

    private void fillData() {
        getLoaderManager().initLoader(0, null, this);
        setListAdapter(new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1, null,
                new String[] {DBAdapter.KEY_NEWS_TITLE},
                new int[] {android.R.id.text1}, 0));
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        CursorLoader loader = new CursorLoader(this, Uri.parse(FeedContentProvider.NEWS_URI.toString() + "/" +channelId), null, null, null, null);
        setListAdapter(new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1, null, new String[] {DBAdapter.KEY_NEWS_TITLE}, new int[] {android.R.id.text1}, 0));
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        if (((CursorAdapter) getListAdapter()).getCursor() != null && ((CursorAdapter) getListAdapter()).getCursor().getCount() != 0)
            Toast.makeText(this, "Feed updated", Toast.LENGTH_SHORT).show();
        ((CursorAdapter) getListAdapter()).swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        ((CursorAdapter) getListAdapter()).swapCursor(null);
    }
}
