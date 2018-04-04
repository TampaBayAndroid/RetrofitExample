package org.tbadg.retrofitexample;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

interface TagsApi {
    @GET("tags?pagesize=5&order=desc&sort=popular&site=stackoverflow")
    Call<Tags> getTagsByPopularity();

    @GET("tags?pagesize=5&sort=name&site=stackoverflow")
    Call<Tags> getTagsByName(@Query("order") String order);
}
