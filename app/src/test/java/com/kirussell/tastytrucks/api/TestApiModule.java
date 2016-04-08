package com.kirussell.tastytrucks.api;

import java.io.IOException;
import java.util.HashMap;

import dagger.Module;
import dagger.Provides;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by russellkim on 08/04/16.
 *
 */
@Module
public class TestApiModule extends ApiModule {

    private final HashMap<String, String> serverResponses;

    public TestApiModule(String token, HashMap<String, String> serverResponses) {
        super(token);
        this.serverResponses = serverResponses;
    }

    @Provides
    @Override
    public OkHttpClient provideOkHttpClient() {
        return new OkHttpClient.Builder().addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                String responseString = "";
                HttpUrl url = chain.request().url();
                if (url.host().equals("data.sfgov.org")) {
                    if (url.encodedPath().equals("/resource/6a9r-agq8.json")) {
                        String parameter = url.queryParameter("$where");
                        responseString = serverResponses.get(parameter);
                    }
                }
                Response response = new Response.Builder().code(200)
                        .message(responseString)
                        .request(chain.request())
                        .protocol(Protocol.HTTP_1_0)
                        .body(ResponseBody.create(MediaType.parse("application/json"), responseString.getBytes()))
                        .build();
                return response;
            }
        }).build();
    }

    @Override
    public String compileWhereParameter(double latitude, double longitude, long distanceInMeters) {
        return super.compileWhereParameter(latitude, longitude, distanceInMeters);
    }
}
