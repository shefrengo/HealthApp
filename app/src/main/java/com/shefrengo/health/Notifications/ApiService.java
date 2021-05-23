package com.shefrengo.health.Notifications;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ApiService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:Key=AAAA2J4mZlg:APA91bG-E9GOqu41FpA_RMW96EFz7nMp0HOFKdYrDgjnb6DrdDV-aNYB-C5U5SkK_tdPnxeu6TpM5WL6cccu9DeOWVPQu5_Tv9vs9Siq3QBoNvyTqX3gmLfneQqMGrWZT7zBOgYlk3x6"

            }

    )
    @POST("fcm/send")
    Call<MyResponse>sendNotification(@Body Sender body);
}
