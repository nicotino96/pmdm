package com.afundacion.fp.sessions;

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
    protected static boolean preconditionsAreCorrect = true;

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
        preconditionsAreCorrect = true;
    }

    /**
     * Sends a POST request that declares a new testing session in the
     * backend. This can be used inside the test suite in order to get a record
     * of the requests made by the app.
     */
    protected static void generateSessionServerSide() throws InterruptedException {
        generateSessionServerSide(null, 0);
    }

    protected static void generateSessionServerSide(String urlToIntercept, int desiredResponseCode) throws InterruptedException {
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
        try {
            sendRequestToTestingSessionEndpoint("POST", timestamp, requestBody);
        } catch (IOException e) {
            // Probably missing permissions in AndroidManifest.xml
            preconditionsAreCorrect = false;
            e.printStackTrace();
        }
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

    protected static void registerUserForLogin(String username, String password) {
        try {
            URL url = new URL(Server.name + "/users");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            JSONObject requestBody = new JSONObject();
            requestBody.put("username", username);
            requestBody.put("password", password);
            connection.setDoOutput(true);
            BufferedOutputStream os = new BufferedOutputStream(connection.getOutputStream());
            os.write(requestBody.toString().getBytes());
            os.flush();
            connection.connect();
            InputStream is = connection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line;
            StringBuilder result = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }
            is.close();
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

    protected static void putNewStatus(String username, String password, String newStatus) {
        try {
            URL url = new URL(Server.name + "/sessions");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            JSONObject requestBody = new JSONObject();
            requestBody.put("username", username);
            requestBody.put("password", password);
            connection.setDoOutput(true);
            BufferedOutputStream os = new BufferedOutputStream(connection.getOutputStream());
            os.write(requestBody.toString().getBytes());
            os.flush();
            connection.connect();
            InputStream is = connection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line;
            StringBuilder result = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }
            is.close();
            JSONObject response = new JSONObject(result.toString());
            String token = response.getString("sessionToken");
            // Now we have a token, update the status
            // FIXME: Ugly copy-paste
            URL url2 = new URL(Server.name + "/users/" + username + "/status");
            HttpURLConnection connection2 = (HttpURLConnection) url2.openConnection();
            connection2.setRequestMethod("PUT");
            connection2.setRequestProperty("Session-Token", token);
            JSONObject requestBody2 = new JSONObject();
            requestBody2.put("status", newStatus);
            connection2.setDoOutput(true);
            BufferedOutputStream os2 = new BufferedOutputStream(connection2.getOutputStream());
            os2.write(requestBody2.toString().getBytes());
            os2.flush();
            connection2.connect();
            InputStream is2 = connection2.getInputStream();
            BufferedReader reader2 = new BufferedReader(new InputStreamReader(is2));
            String line2;
            StringBuilder result2 = new StringBuilder();
            while ((line2 = reader2.readLine()) != null) {
                result2.append(line2);
            }
            is2.close();
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }
}