package tech.shipr.toolboxdev.model;

import java.io.Serializable;

public class User implements Serializable {

    private Object favcat;
    private String name;
    private Object favtool;


    public User() {
    }

    public User(Object favcat, String name, Object favtool) {
        this.favcat = favcat;
        this.name = name;

        this.favtool = favtool;
    }

    public void setFavcat(Object favcat) { this.favcat = favcat; }

    public Object getFavcat() { return favcat; }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }



    public String toString() {
        return "Name: " + name + "\n " +
                "favcat: " + favcat + "\n ";
    }

    public Object getFavtool() {
        return favtool;
    }

    public void setFavtool(Object favtool) {
        this.favtool = favtool;
    }
}
