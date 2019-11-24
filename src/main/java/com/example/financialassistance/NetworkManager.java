package com.example.financialassistance;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NetworkManager {
    public static NetworkManager sharedInstance = new NetworkManager();

    private OkHttpClient client;
    private String authCode;
    private String idToken;
    private String accessToken;

    private NetworkManager(){
        OkHttpClient client = new OkHttpClient();
        this.client = client;

    }

    public void setIdToken(String token) {
        this.idToken = token;
    }

    public void setAuthCode(String authCode) {
        this.authCode = authCode;
    }

    public void getAccessCode(final NetworkResponseCallback callback) {
//        RequestBody requestBody = new FormBody.Builder()
//                .add("scope", "email%20profile")
//                .add("client_id", "401640767936-ums4tnqsi6083ij25f55lj1stu70l2ll.apps.googleusercontent.com")
//                .add("response_type", "code")
//                .add("redirect_uri","com.example.financialassistance/oauth2redirect")
//                .build();

        final Request request = new Request.Builder()
                .url("https://accounts.google.com/o/oauth2/v2/auth?scope=email%20profile&response_type=code&redirect_uri=com.example.financialassistance/oauth2redirect&client_id=401640767936-d1s59a5pvsimku52s3dijm01s7buk7h9.apps.googleusercontent.com")
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try {
                    JSONObject jsonObject = new JSONObject(response.body().string());
//                    accessToken = jsonObject.getString("access_token");
                    callback.success(jsonObject);
//                    final String message = jsonObject.toString(5);
//                    Log.i(LOG_TAG, message);
                } catch (JSONException e) {
                    e.printStackTrace();
                    callback.failure();
                }
            }

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.d("NEtworkManager", "Failed fetch access token");
            }
        });
    }

    public void storeAccessToken(final NetworkResponseCallback callback) {
        RequestBody requestBody = new FormBody.Builder()
                .add("grant_type", "authorization_code")
                .add("client_id", "401640767936-ums4tnqsi6083ij25f55lj1stu70l2ll.apps.googleusercontent.com")   // something like : ...apps.googleusercontent.com
                .add("client_secret", "tnuzjaA5_-4cQbHmzOPkQEHh")
                .add("redirect_uri","")
                .add("code", authCode) // device code.
                .add("id_token", idToken) // This is what we received in Step 5, the jwt token.
                .build();

        final Request request = new Request.Builder()
                .url("https://www.googleapis.com/oauth2/v4/token")
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try {
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    accessToken = jsonObject.getString("access_token");
                    callback.success(jsonObject);
//                    final String message = jsonObject.toString(5);
//                    Log.i(LOG_TAG, message);
                } catch (JSONException e) {
                    e.printStackTrace();
                    callback.failure();
                }
            }

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.d("NEtworkManager", "Failed fetch access token");
            }
        });
    }

    public void detectIntent(String text, final NetworkResponseCallback callback) throws JSONException {
        JSONObject textObject = new JSONObject();
        textObject.put("languageCode", "en");
        textObject.put("text", text);

        JSONObject queryInputObject = new JSONObject();
        queryInputObject.put("text", textObject);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("queryInput", queryInputObject);

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString(2));

        final Request request = new Request.Builder()
                .url("https://dialogflow.googleapis.com/v2/projects/hackathonagent-xryjnr/agent/sessions/1234oiahsdo:detectIntent")
                .header("Authorization", "Bearer " + accessToken)
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try {
                    final JSONObject jsonObject = new JSONObject(response.body().string());
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            callback.success(jsonObject);
                        }
                    });
//                    final String message = jsonObject.toString(5);
//                    Log.i(LOG_TAG, message);
                } catch (JSONException e) {
                    e.printStackTrace();
                    callback.failure();
                }
            }

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.d("NEtworkManager", "Failed fetch access token");
            }
        });
    }

}
