package com.afundacion.fp.sessions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TestingSessionRequests {
    private List<TestRequest> requestList;

    public TestingSessionRequests(JSONArray array) {
        requestList = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            try {
                requestList.add(new TestRequest(array.getJSONObject(i)));
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public boolean contains(String method, String urlContent, JSONObject body) {
        for (TestRequest r : requestList) {
            if (r.getMethod().equalsIgnoreCase(method) && r.getUrl().contains(urlContent)) {
                if (body != null) {
                    if (jsonObjectsMatchesExpected(body, r.getBody())) {
                        return true;
                    }
                } else {
                    return true;
                };
            }
        }
        return false;
    }

    // Does not explore sublevels
    private boolean jsonObjectsMatchesExpected(JSONObject expectedJson, JSONObject actualJson) {
        try {
            for (Iterator<String> it = expectedJson.keys(); it.hasNext(); ) {
                String key = it.next();
                if (!expectedJson.get(key).equals(actualJson.get(key))) {
                    return false;
                }
            }
        } catch (JSONException e) {
            return false;
        }
        return true;
    }

    class TestRequest {
        private String method;
        private String url;
        private JSONObject body;

        public TestRequest(JSONObject object) {
            try {
                this.method = object.getString("method");
                this.url = object.getString("url");
                try {
                    this.body = new JSONObject(object.getString("body"));
                } catch (JSONException e) {
                    this.body = null;
                }
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }

        public String getMethod() {
            return method;
        }

        public String getUrl() {
            return url;
        }

        public JSONObject getBody() {
            return body;
        }
    }
}