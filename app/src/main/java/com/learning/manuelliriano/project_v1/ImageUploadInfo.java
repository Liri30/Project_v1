package com.learning.manuelliriano.project_v1;

public class ImageUploadInfo {


    private String id;
    private String imageName;
    private String comentario;
    private String location;
    private String currentUser;

    public ImageUploadInfo() {

    }

    public ImageUploadInfo(String id, String imageName, String comentario, String location, String currentUser) {
        this.id = id;
        this.imageName = imageName;
        this.comentario = comentario;
        this.location = location;
        this.currentUser = currentUser;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(String currentUser) {
        this.currentUser = currentUser;
    }

    public String getEmail() {
        return currentUser;
    }

    public void setEmail(String email) {
        this.currentUser = email;
    }


    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}