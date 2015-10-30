package br.com.vsgdev.promobeer.models;

import java.util.Calendar;

public class Comment {
    private Integer id;
    private String content;
    private User user;
    private Calendar date;
    private Item item;

    public Comment(final Item item, String content) {
        this.content = content;
        setDate(Calendar.getInstance());
        setItem(item);
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Calendar getDate() {
        return date;
    }

    public void setDate(Calendar date) {
        this.date = date;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
