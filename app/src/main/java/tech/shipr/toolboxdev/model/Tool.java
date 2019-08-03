package tech.shipr.toolboxdev.model;

import java.io.Serializable;

public class Tool implements Serializable {

    private String text;
    private String name;
    private String url;
    private String key;
    private String cat;
    private String about;

    public Tool() {
    }

    public Tool(String text, String name, String url, String key, String cat, String about) {
        this.text = text;
        this.name = name;
        this.url = url;
        this.key = key;
        this.cat = cat;
        this.about = about;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String toString() {
        return "Name: " + name + "\n " +
                "Url: " + url + "\n " +
                "Text: " + text + "\n ";
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getCat() {
        return cat;
    }

    public void setCat(String cat) {
        this.cat = cat;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }
}
