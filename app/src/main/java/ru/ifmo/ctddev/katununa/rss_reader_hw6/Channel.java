package ru.ifmo.ctddev.katununa.rss_reader_hw6;

/**
 * Created by Евгения on 07.11.2014.
 */
public class Channel {
    public final String name;
    public final String url;
    public final long id;
    public Channel(String name, String url, long id){
        this.name = name;
        this.url = url;
        this.id = id;
    }
}
