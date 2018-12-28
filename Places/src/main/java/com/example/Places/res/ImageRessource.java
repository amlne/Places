package com.example.Places.res;

public class ImageRessource {


    private String idPlace; //
    private String idImage;

    public ImageRessource(String idplace) {
        this.idPlace = idplace;
        this.idImage = null;
    }

    public String getIdPlace() {
        return idPlace;
    }

    public void setIdPlace(String idPlace) {
        this.idPlace = idPlace;
    }

    public String getIdImage() {
        return idImage;
    }

    public void setIdImage(String idImage) {
        this.idImage = idImage;
    }
}
