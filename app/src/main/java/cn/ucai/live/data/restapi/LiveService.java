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

    @GET(I.REQUEST_CREATE_CHATROOM)
    Call<String> createLiveRoom(
            @Query("auth")String auth,
            @Query("name")String name,
            @Query("description")String description,
            @Query("owner")String owner,
            @Query("maxusers")int maxusers,
            @Query("members") String members
    );

    @GET(I.REQUEST_DELETE_CHATROOM)
    Call<String> deleteChatRoom(
            @Query("auth") String auth,
            @Query("chatRoomId") String chatRoomId
    );

}
