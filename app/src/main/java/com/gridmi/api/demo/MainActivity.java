package com.gridmi.api.demo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.gridmi.api.GridmiAPI;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialization the library
        GridmiAPI.init("http://gridmi.ru/API", 10000, JSONObject.class);

        // Create new request instance
        GridmiAPI.Request request = new GridmiAPI.Request("profile/get");

        // Add header & param
        GridmiAPI.Header header = request.addHeader("X-Key", "e10adc3949ba59abbe56e057f20f883e");
        GridmiAPI.Param param = request.addParam("id", 10);

        // Send the request to the server
        GridmiAPI.onRequest(this, request, new GridmiAPI.Handler.OUT() {

            @Override
            protected void onSuccess(GridmiAPI.Response response) {

                // Convert force to JSONObject.class type
                JSONObject jsonObject = (JSONObject) response.getData();

                // Log in console
                Log.d("TAG", jsonObject.toString());

            }

            @Override
            protected void onFailed(Exception exception) {
                Toast.makeText(MainActivity.this, exception.getMessage(), Toast.LENGTH_LONG).show();
            }

        }).start();

    }

}
