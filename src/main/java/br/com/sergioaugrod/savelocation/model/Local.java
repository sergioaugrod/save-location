package br.com.sergioaugrod.savelocation.model;

public class Local {

    private long id;
    private String descricao;
    private double latitude;
    private double longitude;

    public Local() {
    }

    public Local(long id, String descricao, double latitude, double longitude) {
        this.id = id;
        this.descricao = descricao;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Local(String descricao, double latitude, double longitude) {
        this.descricao = descricao;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        return descricao;
    }

}
