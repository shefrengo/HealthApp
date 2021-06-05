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
                    "Authorization:Key=AAAA1xs7jzA:APA91bEkIX81Sp1nnDU3txiRQXFK8BK8KOpOZOqWjveSBaZUn1Rs9n-SZkJG90irqk7Vw7cPILdSSN6Xkr-S0VAkMZEcMa1FGS-fNhlekN-4asefLjPJFzyjTC1fMBAhNvhrnpvVHZzv"

            }

    )
    @POST("fcm/send")
    Call<MyResponse>sendNotification(@Body Sender body);
}
