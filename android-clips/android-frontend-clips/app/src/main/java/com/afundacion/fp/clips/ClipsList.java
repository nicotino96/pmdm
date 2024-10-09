package com.afundacion.fp.clips;

import com.afundacion.fp.clips.Clip;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ClipsList {
    private List<Clip> clips;

    public ClipsList(JSONArray array) {
        clips = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            try {
                JSONObject jsonElement = array.getJSONObject(i);
                Clip aClip = new Clip(jsonElement);
                clips.add(aClip);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
