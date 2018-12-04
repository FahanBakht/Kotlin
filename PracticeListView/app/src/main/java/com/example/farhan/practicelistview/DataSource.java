package com.example.farhan.practicelistview;



public class DataSource {

    private int id;
    private String name;
    private int image;

    public DataSource(int id, String name, int image) {
        this.id = id;
        this.name = name;
        this.image = image;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getImage() {
        return image;
    }
}
