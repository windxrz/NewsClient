package babydriver.newsclient.controller;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

/**
 * download pictures
 */

interface PictureService
{
    @GET
    Call<ResponseBody> downloadPic(@Url String picUrl);
}
