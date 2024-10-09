package com.afundacion.fp.clips;

import org.json.JSONException;
import org.json.JSONObject;

public class Clip {
    private int id;
    private String title;
    private String urlVideo;

    public Clip(JSONObject json) throws JSONException {
        this.id = json.getInt("id");
        this.title = json.getString("title");
        this.urlVideo = json.getString("videoUrl");
    }

    public String getTitle() {
        return title;
    }

    public int getId() {
        return id;
    }

    public String getUrlVideo() {
        return urlVideo;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setUrlVideo(String urlVideo) {
        this.urlVideo = urlVideo;
    }
}
