package com.afundacion.fp.clips;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.AfterClass;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

public class TestRun {
    private static String timestamp = (new Date()).getTime() + "T";

    /**
     * @return An object containing information about the client requests
     *         during the test suite.
     */
    public TestingSessionRequests getSessionClientRequests() {
        try {
            // FIXME: Busy wait that prevents GET from reaching backend before real app requests
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            String response = sendRequestToTestingSessionEndpoint("GET", timestamp, null);
            JSONArray parsedResponse = new JSONArray(response);
            return new TestingSessionRequests(parsedResponse);
        } catch (IOException | JSONException e) {
            throw new RuntimeException(e);
        }
    }

    @AfterClass
    public static void tearDown() throws IOException {
        sendRequestToTestingSessionEndpoint("DELETE", null, null);
    }

    /**
     * Sends a POST request that declares a new testing session in the
     * backend. This can be used inside the test suite in order to get a record
     * of the requests made by the app.
     */
    protected static void generateSessionServerSide() throws IOException, InterruptedException {
        generateSessionServerSide(null, 0);
    }

    protected static void generateSessionServerSide(String urlToIntercept, int desiredResponseCode) throws IOException, InterruptedException {
        JSONObject requestBody = null;
        if (urlToIntercept != null) {
            requestBody = new JSONObject();
            try {
                requestBody.put("urlToIntercept", urlToIntercept);
                requestBody.put("newResponseCode", desiredResponseCode);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        sendRequestToTestingSessionEndpoint("POST", timestamp, requestBody);
        // FIXME: Busy wait that ensures POST reaches backend before anything else
        Thread.sleep(2000);
    }

    private static String sendRequestToTestingSessionEndpoint(String httpMethod,
                                                              String sessionTimestamp,
                                                              JSONObject requestBody) throws IOException {
        URL url;
        if (sessionTimestamp == null) {
            url = new URL(Server.name);
        } else {
            url = new URL(Server.name + "/" + sessionTimestamp);
        }
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod(httpMethod);
        if (requestBody != null) {
            connection.setDoOutput(true);
            BufferedOutputStream os = new BufferedOutputStream(connection.getOutputStream());
            os.write(requestBody.toString().getBytes());
            os.flush();
            connection.connect();
        }
        InputStream is = connection.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        String line;
        StringBuilder result = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            result.append(line);
        }
        is.close();
        return result.toString();
    }
}
