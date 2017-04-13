package cn.ucai.live.data.restapi;

import cn.ucai.live.I;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by xheng on 2017/4/13.
 */

public interface LiveService {
    @GET(I.REQUEST_ALL_GIFTS)
    Call<String> getAllGifts();

    @GET(I.REQUEST_FIND_USER)
    Call<String> loadUserInfo(
            @Query(I.User.USER_NAME) String userName
    );
}
