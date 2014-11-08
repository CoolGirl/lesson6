package mob_dev_lesson2.katunina.ctddev.ifmo.ru.rss_readerhw5;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Евгения on 07.11.2014.
 */
public class DBAdapter {
    //Common keys
    public static final String KEY_ID = "_id";

    //Channels
    public static final String TABLE_NAME_CHANNELS = "channel";
    public static final String KEY_CHANNELS_NAME = "name";
    public static final String KEY_CHANNELS_URL = "url";
    public static final String CREATE_TABLE_CHANNELS = "CREATE TABLE " + TABLE_NAME_CHANNELS + " ("
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + KEY_CHANNELS_NAME + " TEXT, "
            + KEY_CHANNELS_URL + " TEXT" + ")";

    //News
    public static final String TABLE_NAME_NEWS = "news";
    public static final String KEY_NEWS_CHANNEL_ID = "channel_id";
    public static final String KEY_NEWS_TITLE = "title";
    public static final String KEY_NEWS_DESCRIPTION = "description";
    public static final String KEY_NEWS_URL = "url";
    public static final String KEY_NEWS_TIME = "time";
    public static final String CREATE_TABLE_NEWS = "CREATE TABLE " + TABLE_NAME_NEWS + " ("
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + KEY_NEWS_CHANNEL_ID + " INTEGER NOT NULL, "
            + KEY_NEWS_TITLE + " TEXT, "
            + KEY_NEWS_DESCRIPTION + " TEXT, "
            + KEY_NEWS_URL + " TEXT, "
            + KEY_NEWS_TIME + " INTEGER NOT NULL, "
            + "FOREIGN KEY (" + KEY_NEWS_CHANNEL_ID + " ) REFERENCES "
            + TABLE_NAME_CHANNELS + " (" + KEY_ID + ") ON DELETE CASCADE, "
            + "UNIQUE (" + KEY_NEWS_URL + ") ON CONFLICT IGNORE"
            + ")";

    private static DBAdapter mInstance = null;
    private Context context;
    private SQLiteDatabase db;
    private DBAdapter(Context context){
        this.context = context;
    }

    public static DBAdapter getOpenedInstance(Context context){
        if (mInstance==null)
          mInstance = new DBAdapter(context.getApplicationContext()).open();
        return mInstance;
    }

    private DBAdapter open() {
        DBHelper mDbHelper = new DBHelper(context);
        db = mDbHelper.getWritableDatabase();
        return this;
    }

    public static final String DB_NAME = "database";
    public static final Integer VERSION = 1;
    private static class DBHelper extends SQLiteOpenHelper {
        Context context;

        public DBHelper(Context context) {
            super(context, DB_NAME, null, VERSION);
            this.context = context;
        }

        @Override
        public void onOpen(SQLiteDatabase db) {
            db.execSQL("PRAGMA foreign_keys=ON");
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            sqLiteDatabase.execSQL(CREATE_TABLE_CHANNELS);
            sqLiteDatabase.execSQL(CREATE_TABLE_NEWS);
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {

        }
    }

    public String getUrlByChannelId(long channelId){
        Cursor c = db.query(TABLE_NAME_CHANNELS,new String[] {KEY_CHANNELS_URL},KEY_ID + "=" + channelId,
                null,null,null,null);
        if (c.getCount()==0) {
            c.close();
            return null;
        }
        c.moveToFirst();
        String result = c.getString(c.getColumnIndex(KEY_CHANNELS_URL));
        c.close();
        return result;
    }

    public long createChannel(ContentValues channel) {
        return db.insert(TABLE_NAME_CHANNELS,null,channel);
    }

    public long createChannel(Channel channel){
        ContentValues values = new ContentValues();
        values.put(KEY_CHANNELS_NAME,channel.name);
        values.put(KEY_CHANNELS_URL,channel.url);
        return createChannel(values);
    }

    public boolean changeChannel(ContentValues channel, long channelId) {
        return db.update(TABLE_NAME_CHANNELS, channel, KEY_ID + "=" + channelId,null)==1;
    }

    public boolean changeChannel(Channel channel){
        ContentValues values = new ContentValues();
        values.put(KEY_CHANNELS_NAME,channel.name);
        values.put(KEY_CHANNELS_URL,channel.url);
        changeChannel(values, channel.id);
        return changeChannel(values, channel.id);
    }

    public Cursor getNewsByChannelId(long channelId){
        return db.query(TABLE_NAME_NEWS, new String[] {KEY_ID,KEY_NEWS_URL,KEY_NEWS_DESCRIPTION,KEY_NEWS_TITLE},
    KEY_NEWS_CHANNEL_ID + "=" +channelId,null,null,null,KEY_NEWS_TIME + "DESC");
}

    public Cursor getAllChannels(){
        return db.query(TABLE_NAME_CHANNELS,new String [] {KEY_ID},null,null,null,null,null);
    }

    public long createNews(ContentValues news) {
        return db.insert(TABLE_NAME_CHANNELS,null,news);
    }

    public long createNews(News news, long channelId){
        ContentValues values = new ContentValues();
        values.put(KEY_NEWS_CHANNEL_ID, channelId);
        //id is not needed
        values.put(KEY_NEWS_DESCRIPTION,news.description);
        values.put(KEY_NEWS_TITLE, news.title);
        values.put(KEY_NEWS_URL, news.url);
        values.put(KEY_NEWS_TIME,news.time);
        return createNews(values);
    }

    public boolean deleteChannel(long channelId){
        return db.delete(TABLE_NAME_CHANNELS,KEY_ID + "=" + channelId, null)==1;
    }
}
