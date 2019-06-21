package com.example.jnubus;


public class Relation {


    private int id;
    private int busid;
    private int placeid;
    private int waypoints;

    public Relation() {
    }

    public Relation(int id, int busid, int placeid, int waypoints) {
        this.id = id;
        this.busid = busid;
        this.placeid = placeid;
        this.waypoints = waypoints;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getBusid() {
        return busid;
    }

    public void setBusid(int busid) {
        this.busid = busid;
    }

    public int getPlaceid() {
        return placeid;
    }

    public void setPlaceid(int placeid) {
        this.placeid = placeid;
    }

    public int getWaypoints() {
        return waypoints;
    }

    public void setWaypoints(int waypoints) {
        this.waypoints = waypoints;
    }
}
