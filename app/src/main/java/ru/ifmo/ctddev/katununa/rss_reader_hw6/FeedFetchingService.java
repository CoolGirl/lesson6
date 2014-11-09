package ru.ifmo.ctddev.katununa.rss_reader_hw6;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import java.util.List;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class FeedFetchingService extends IntentService {
    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_UPDATE_CHANNEL
            = "ru.ifmo.ctddev.katununa.rss_readerhw5.action.updateChannel";
    private static final String ACTION_UPDATE_ALL_CHANNELS
            = "ru.ifmo.ctddev.katununa.rss_readerhw5.action.updateAllChannels";

    private static final String CHANNEL_ID = "ru.ifmo.ctddev.katununa.rss_readerhw5.extra.channel_id";

    public static void startActionUpdateChannel(Context context, long channelId) {
        Intent intent = new Intent(context, FeedFetchingService.class);
        intent.setAction(ACTION_UPDATE_CHANNEL);
        intent.putExtra(CHANNEL_ID, channelId);
        context.startService(intent);
    }

    public static void startActionUpdateAllChannels(Context context) {
        Intent intent = new Intent(context, FeedFetchingService.class);
        intent.setAction(ACTION_UPDATE_ALL_CHANNELS);
        context.startService(intent);
    }


    public FeedFetchingService() {
        super("FeedFetchingService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_UPDATE_CHANNEL.equals(action)) {
                final long channelId = intent.getLongExtra(CHANNEL_ID, -1);
                handleActionUpdateChannel(channelId);
            } else if (ACTION_UPDATE_ALL_CHANNELS.equals(action)) {
                handleActionUpdateAllChannels();
            }
        }
    }

    public static final String BROADCAST_ACTION_CHANNEL_UPDATED = "channel updated";

    private void handleActionUpdateChannel(final long channelId) {
        final DBAdapter db = DBAdapter.getOpenedInstance(this);
        String url = db.getUrlByChannelId(channelId);
        if (url != null) {
            RssParser.FeedParsedCallback callback = new RssParser.FeedParsedCallback() {
                @Override
                public void onFeedParsed(List<FeedItem> feedItems) {
                    for (FeedItem item : feedItems) {
                        ContentValues values = new ContentValues();
                        values.put(DBAdapter.KEY_NEWS_DESCRIPTION, item.description);
                        values.put(DBAdapter.KEY_NEWS_TITLE, item.title);
                        values.put(DBAdapter.KEY_NEWS_URL, item.link);
                        values.put(DBAdapter.KEY_NEWS_CHANNEL_ID, channelId);
                        values.put(DBAdapter.KEY_NEWS_TIME, System.currentTimeMillis() / 1000);
                        getContentResolver().insert(Uri.parse(FeedContentProvider.NEWS_URI.toString() + "/" + channelId), values);
                    }
                    Intent broadcastIntent = new Intent();
                    broadcastIntent.setAction(BROADCAST_ACTION_CHANNEL_UPDATED);
                    broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
                    broadcastIntent.putExtra(CHANNEL_ID, channelId);
                    sendBroadcast(broadcastIntent);
                    getContentResolver().notifyChange(Uri.parse(FeedContentProvider.NEWS_URI.toString() + "/" + channelId), null);
                }
            };
            new RssParser(callback, url);
        }
    }

    private void handleActionUpdateAllChannels() {
        final DBAdapter db = DBAdapter.getOpenedInstance(this);
        Cursor c = db.getAllChannels();
        if (!c.moveToFirst()) return;
        do {
            handleActionUpdateChannel(c.getLong(c.getColumnIndex(DBAdapter.KEY_ID)));
        } while (c.moveToNext());
        c.close();
    }
}
