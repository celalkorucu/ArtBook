package com.celalkorucu.artbook;

public class Art {
    int id ;
    String artName ;

    public Art(int id, String artName) {
        this.id = id;
        this.artName = artName;
    }

    public int getId() {
        return id;
    }

    public String getArtName() {
        return artName;
    }
}
