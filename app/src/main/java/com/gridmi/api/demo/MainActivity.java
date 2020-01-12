package com.gridmi.api.demo;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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
        GridmiAPI.init("http://gridmi.com/API/", 10000, JSONObject.class);

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

        GridmiAPI.onRequest(this, new GridmiAPI.Request("profile/get"), new GridmiAPI.Handler.OUT() {

            @Override
            protected void onSuccess(GridmiAPI.Response response) {
                Log.d("TagGridmiAPI", "result = " + ((JSONObject) response.getData()).toString());
            }

            @Override
            protected void onFailed(Exception exception) {
                Log.d("TagGridmiAPI", "exception = " + exception.getMessage());
            }

        }).start();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (data == null || data.getData() == null) return;

        try {

            // Экземпляр запроса к серверу
            GridmiAPI.Request request = new GridmiAPI.Request("POST", "photo/add");

            // Создаем экземпляр многосоставного тела запроса
            GridmiAPI.Multipart multipart = new GridmiAPI.Multipart(getContentResolver());
            multipart.appendData("photo", data.getData());

            // Установить тело запроса
            request.setBody(multipart, true);

            // Отпраивть запрос
            GridmiAPI.onRequest(this, request, new GridmiAPI.Handler.OUT() {

                @Override
                protected void onSuccess(GridmiAPI.Response response) {
                    try {

                        // Получить результат с тела запроса
                        boolean result = ((JSONObject) response.getData()).getBoolean("result");

                        // Уведомление
                        Toast.makeText(MainActivity.this, result ? "Загружено!" : "Ошибка!", Toast.LENGTH_LONG).show();

                    } catch (Exception exception) {
                        this.onFailed(exception);
                    }
                }

                @Override
                protected void onFailed(Exception exception) {
                    // Уведомление о текущем исключении
                    Toast.makeText(MainActivity.this, exception.getMessage(), Toast.LENGTH_LONG).show();
                }

            }).start();

        } catch (Exception exception) {
            Toast.makeText(this, exception.getMessage(), Toast.LENGTH_LONG).show();
        }

    }

}
