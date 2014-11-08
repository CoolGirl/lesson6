package mob_dev_lesson2.katunina.ctddev.ifmo.ru.rss_readerhw5;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.graphics.Paint;
import android.net.Uri;

public class FeedContentProvider extends ContentProvider {

    // database
    private DBAdapter database;

    // used for the UriMacher
    private static final int CHANNELS = 10;
    private static final int NEWS = 20;

    public static final String AUTHORITY = "mob_dev_lesson2.katunina.ctddev.ifmo.ru.rss_readerhw5.feeds";

    private static final String BASE_CHANNELS = "channels";
    private static final String BASE_NEWS = "news";

    public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
            + "/feeds";
    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
            + "/news";

    private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        sURIMatcher.addURI(AUTHORITY, BASE_CHANNELS, CHANNELS);
        sURIMatcher.addURI(AUTHORITY, BASE_NEWS, NEWS);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int uriType = sURIMatcher.match(uri);
        if (uriType == CHANNELS) {
            return database.deleteChannel(Long.parseLong(uri.getLastPathSegment())) ? 1 : 0;
        }
        throw new UnsupportedOperationException();
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        int uriType = sURIMatcher.match(uri);
        long id = -1;
        if (uriType == NEWS) {
            id = database.createNews(values);
        } else if (uriType == CHANNELS) {
            id = database.createChannel(values);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return Uri.parse(uri.toString() + "/" + id);
    }

    @Override
    public boolean onCreate() {
        database = DBAdapter.getOpenedInstance(getContext());
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
            String[] selectionArgs, String sortOrder) {
        int uriType = sURIMatcher.match(uri);
        Cursor result = null;
        if (uriType == NEWS) {
            String lastPathSegment = uri.getLastPathSegment();
            long channelId = Long.parseLong(lastPathSegment);
            result = database.getNewsByChannelId(channelId);
        } else if (uriType == CHANNELS) {
            result = database.getAllChannels();
        }
        if (result != null)
            result.setNotificationUri(getContext().getContentResolver(), uri);
        return result;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
            String[] selectionArgs) {
        int uriType = sURIMatcher.match(uri);
        if (uriType != CHANNELS) throw new UnsupportedOperationException();
        long id = Long.parseLong(uri.getLastPathSegment());
        int result = database.changeChannel(values, id) ? 1 : 0;
        getContext().getContentResolver().notifyChange(uri, null);
        return result;
    }
}
