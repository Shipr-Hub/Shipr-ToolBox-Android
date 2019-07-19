package tech.shipr.toolboxdev.model;

import java.io.Serializable;

public class Tool implements Serializable {

    private String text;
    private String name;
    private String url;

    public Tool() {
    }

    public Tool(String text, String name, String url) {
        this.text = text;
        this.name = name;
        this.url = url;
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

    public String toString(){
        return "Name: " + name + "\n " +
                "Url: " + url + "\n " +
                "Text: " + text + "\n " ;
    }
}
