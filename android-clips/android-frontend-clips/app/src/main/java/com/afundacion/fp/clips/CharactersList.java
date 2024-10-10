package com.afundacion.fp.clips;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CharactersList {
    private List<Character> characters;
    public CharactersList(JSONArray array) {
        characters = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            try {
                JSONObject jsonElement = array.getJSONObject(i);
                Character aCharacter = new Character(jsonElement);
                characters.add(aCharacter);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public List<Character> getCharacters() {
        return characters;
    }
}
