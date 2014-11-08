package mob_dev_lesson2.katunina.ctddev.ifmo.ru.rss_readerhw5;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.database.Cursor;

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
            = "mob_dev_lesson2.katunina.ctddev.ifmo.ru.rss_readerhw5.action.updateChannel";
    private static final String ACTION_UPDATE_ALL_CHANNELS
            = "mob_dev_lesson2.katunina.ctddev.ifmo.ru.rss_readerhw5.action.updateAllChannels";

    // TODO: Rename parameters
    private static final String CHANNEL_ID = "mob_dev_lesson2.katunina.ctddev.ifmo.ru.rss_readerhw5.extra.channel_id";

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
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

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionUpdateChannel(final long channelId) {
        final DBAdapter db = DBAdapter.getOpenedInstance(this);
        String url = db.getUrlByChannelId(channelId);
        if (url != null) {
            RssParser.FeedParsedCallback callback = new RssParser.FeedParsedCallback() {
                @Override
                public void onFeedParsed(List<FeedItem> feedItems) {
                    for (FeedItem item : feedItems) {
                        News n = new News(-1, item.title, item.description, item.link, System.currentTimeMillis() / 1000);
                        db.createNews(n, channelId);
                    }
                    Intent broadcastIntent = new Intent();
                    broadcastIntent.setAction(BROADCAST_ACTION_CHANNEL_UPDATED);
                    broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
                    broadcastIntent.putExtra(CHANNEL_ID, channelId);
                    sendBroadcast(broadcastIntent);
                }
            };
            new RssParser(callback, url);
        }
    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private void handleActionUpdateAllChannels() {
        // TODO: Handle action Baz
        final DBAdapter db = DBAdapter.getOpenedInstance(this);
        Cursor c = db.getAllChannels();
        if (!c.moveToFirst()) return;
        do {
            handleActionUpdateChannel(c.getLong(c.getColumnIndex(DBAdapter.KEY_ID)));
        } while (c.moveToNext());
        c.close();
    }
}
