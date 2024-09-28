package com.afundacion.fp.clips;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
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

    public boolean contains(String method, String urlContent) {
        for (TestRequest r : requestList) {
            if (r.getMethod().equalsIgnoreCase(method) && r.getUrl().contains(urlContent)) {
                return true;
            }
        }
        return false;
    }

    class TestRequest {
        private String method;
        private String url;

        public TestRequest(JSONObject object) {
            try {
                this.method = object.getString("method");
                this.url = object.getString("url");
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
    }
}
