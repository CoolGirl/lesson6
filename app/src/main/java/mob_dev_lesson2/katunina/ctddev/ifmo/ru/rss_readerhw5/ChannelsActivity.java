package mob_dev_lesson2.katunina.ctddev.ifmo.ru.rss_readerhw5;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.SimpleCursorAdapter;


public class ChannelsActivity extends ListActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Cursor cursor = getContentResolver().query(
                Uri.parse(FeedContentProvider.AUTHORITY + "/channels"),
                null, null, null, null);
        setListAdapter(new SimpleCursorAdapter(this, android.R.layout.simple_list_item_2, cursor,
                new String[] {DBAdapter.KEY_CHANNELS_NAME, DBAdapter.KEY_CHANNELS_URL},
                new int[] {android.R.id.text1, android.R.id.text2}, 0));
        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(ChannelsActivity.this, FeedActivity.class);
                intent.putExtra(FeedActivity.EXTRA_CHANNEL_ID, l);
                startActivity(intent);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.channels, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_add) {
            AddDialog addDialog = new AddDialog();
            Bundle args = new Bundle();
            args.putBoolean(AddDialog.ARG_EDIT, false);
            addDialog.setArguments(args);
            getFragmentManager().beginTransaction().add(addDialog, "").commit();
        }
        return super.onOptionsItemSelected(item);
    }
}
