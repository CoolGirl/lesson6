package ru.ifmo.ctddev.katununa.rss_reader_hw6;

/**
 * Created by Евгения on 07.11.2014.
 */
public class News {
    public final String title;
    public final long id;
    public final String description;
    public final String url;
    public final long time;
    public News(long id, String title, String description, String url, long time){
        this.title = title;
        this.description= description;
        this.url=url;
        this.time = time;
        this.id = id;
    }
}
