package cn.ucai.live.data.restapi;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import cn.ucai.live.I;
import cn.ucai.live.LiveApplication;
import cn.ucai.live.data.model.Gift;
import cn.ucai.live.data.model.LiveRoom;
import cn.ucai.live.data.model.Result;
import cn.ucai.live.data.restapi.model.LiveStatusModule;
import cn.ucai.live.data.restapi.model.ResponseModule;
import cn.ucai.live.data.restapi.model.StatisticsType;

import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.domain.User;

import java.io.IOException;
import java.util.List;

import cn.ucai.live.utils.L;
import cn.ucai.live.utils.ResultUtils;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Created by wei on 2017/2/14.
 */

public class ApiManager {
    private String appkey;
    private ApiService apiService;

    private static ApiManager instance;

    private LiveService mLiveService;

    private ApiManager() {
        try {
            ApplicationInfo appInfo = LiveApplication.getInstance().getPackageManager().getApplicationInfo(
                    LiveApplication.getInstance().getPackageName(), PackageManager.GET_META_DATA);
            appkey = appInfo.metaData.getString("EASEMOB_APPKEY");
            appkey = appkey.replace("#", "/");
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException("must set the easemob appkey");
        }

        //HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        //httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient httpClient = new OkHttpClient.Builder()
                .addInterceptor(new RequestInterceptor())
                //.addInterceptor(httpLoggingInterceptor)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://a1.easemob.com/" + appkey + "/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient)
                .build();

        apiService = retrofit.create(ApiService.class);

        Retrofit retrofit1 = new Retrofit.Builder()
                .baseUrl(I.SERVER_ROOT)
                .addConverterFactory(ScalarsConverterFactory.create())
                .client(httpClient)
                .build();
        mLiveService = retrofit1.create(LiveService.class);
    }

    public List<Gift> getAllGifts() throws IOException {
        Call<String> call = mLiveService.getAllGifts();
        Result<List<Gift>> result = responseToResultList(call, Gift.class);
        if (result != null && result.isRetMsg()) {
            return result.getRetData();
        }
        return null;
    }

    public User loadUserInfo(String userName) throws IOException {
        User user = null;
        Call<String> call = mLiveService.loadUserInfo(userName);
        Result<User> result = responseToResult(call, User.class);
        if (result != null && result.isRetMsg()) {
            user = result.getRetData();
        }
        return user;
    }
    public String createLiveRoom(String auth,String name,String description,String owner,int maxusers,String members) {
        Call<String> call = mLiveService.createLiveRoom(auth, name, description, owner, maxusers, members);
        try {
            String body = call.execute().body();
            L.e("create","createLiveRoom body:"+body);
            return ResultUtils.getResultFromJson(body);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    public String createLiveRoom(String name,String description) {
        L.e("create","createLiveRoom:"+name+","+description);
        String user = EMClient.getInstance().getCurrentUser();
        return createLiveRoom("1IFgE",name,description, user,300,user);

    }

    public void deleteChatRoom(String id) {
        Call<String> call = mLiveService.deleteChatRoom("1IFgE", id);
//        try {
//            String body = call.execute().body();
//            return ResultUtils.getResultBooleanFromJson(body);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                boolean isDelChatRoom = ResultUtils.getResultBooleanFromJson(response.body());
                L.e("delete","isDelChatRoom："+isDelChatRoom);
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                L.e("delete","onFailure："+t.toString());
            }
        });
//        return false;
    }

    private <T> Result<T> responseToResult(Call<String> call, Class<T> tClass) throws IOException {
        Response<String> response = call.execute();
        if (!response.isSuccessful()) {
            try {
                throw new LiveException(response.code(), response.errorBody().string());
            } catch (LiveException e) {
                e.printStackTrace();
            }
        }
        return ResultUtils.getResultFromJson(response.body(), tClass);
    }

    private <T> Result<List<T>> responseToResultList(Call<String> call, Class<T> tClass) throws IOException {
        Response<String> response = call.execute();
        if (!response.isSuccessful()) {
            try {
                throw new LiveException(response.code(), response.errorBody().string());
            } catch (LiveException e) {
                e.printStackTrace();
            }
        }
        return ResultUtils.getListResultFromJson(response.body(), tClass);
    }

    static class RequestInterceptor implements Interceptor {

        @Override
        public okhttp3.Response intercept(Chain chain) throws IOException {
            Request original = chain.request();
            Request request = original.newBuilder()
                    .header("Authorization", "Bearer " + EMClient.getInstance().getAccessToken())
                    .header("Accept", "application/json")
                    .header("Content-Type", "application/json")
                    .method(original.method(), original.body())
                    .build();
            okhttp3.Response response = chain.proceed(request);
            return response;
        }
    }

    public static ApiManager get() {
        if (instance == null) {
            instance = new ApiManager();
        }
        return instance;
    }


    public LiveRoom createLiveRoom(String name, String description, String coverUrl) throws LiveException {
        return createLiveRoomWithRequest(name, description, coverUrl, null);
    }

    public LiveRoom createLiveRoom(String name, String description, String coverUrl, String liveRoomId) throws LiveException {
        return createLiveRoomWithRequest(name, description, coverUrl, liveRoomId);
    }

    private LiveRoom createLiveRoomWithRequest(String name, String description, String coverUrl, String liveRoomId) throws LiveException {
        LiveRoom liveRoom = new LiveRoom();
        liveRoom.setName(name);
        liveRoom.setDescription(description);
        liveRoom.setAnchorId(EMClient.getInstance().getCurrentUser());
        liveRoom.setCover(coverUrl);

        //id代替room.getId
        String id = createLiveRoom(name, description);
        L.e("create", "creat id:" + id);
        if (id != null) {
            liveRoom.setId(id);
            liveRoom.setChatroomId(id);
        } else {
            liveRoom.setId(liveRoomId);
        }


//        Call<ResponseModule<LiveRoom>> responseCall;
//        if (liveRoomId != null) {
//            responseCall = apiService.createLiveShow(liveRoomId, liveRoom);
//
//        } else {
//            responseCall = apiService.createLiveRoom(liveRoom);
//        }
//        ResponseModule<LiveRoom> response = handleResponseCall(responseCall).body();
//        LiveRoom room = response.data;
//        if (room.getId() != null) {
//            liveRoom.setId(room.getId());
//        } else {
//            liveRoom.setId(liveRoomId);
//        }
//        liveRoom.setChatroomId(room.getChatroomId());
//        //liveRoom.setAudienceNum(1);
//        liveRoom.setLivePullUrl(room.getLivePullUrl());
//        liveRoom.setLivePushUrl(room.getLivePushUrl());
        return liveRoom;
    }

    public void updateLiveRoomCover(String roomId, String coverUrl) throws LiveException {
        JSONObject jobj = new JSONObject();
        JSONObject picObj = new JSONObject();
        try {
            picObj.put("cover_picture_url", coverUrl);
            jobj.put("liveroom", picObj);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Call<ResponseModule> responseCall = apiService.updateLiveRoom(roomId, jsonToRequestBody(jobj.toString()));
        handleResponseCall(responseCall);
    }


    //public void joinLiveRoom(String roomId, String userId) throws LiveException {
    //    JSONObject jobj = new JSONObject();
    //    String[] arr = new String[]{userId};
    //    JSONArray jarr = new JSONArray(Arrays.asList(arr));
    //    try {
    //        jobj.put("usernames", jarr);
    //    } catch (JSONException e) {
    //        e.printStackTrace();
    //    }
    //    handleResponseCall(apiService.joinLiveRoom(roomId, jsonToRequestBody(jobj.toString())));
    //}


    //public void updateLiveRoom(LiveRoom liveRoom) throws LiveException {
    //    Call respCall = apiService.updateLiveRoom(liveRoom.getId(), liveRoom);
    //    handleResponseCall(respCall);
    //}

    public LiveStatusModule.LiveStatus getLiveRoomStatus(String roomId) throws LiveException {
        Call<ResponseModule<LiveStatusModule>> respCall = apiService.getStatus(roomId);
        return handleResponseCall(respCall).body().data.status;
    }

    public void terminateLiveRoom(String roomId) throws LiveException {
        LiveStatusModule module = new LiveStatusModule();
        module.status = LiveStatusModule.LiveStatus.completed;
        handleResponseCall(apiService.updateStatus(roomId, module));
    }

    //public void closeLiveRoom(String roomId) throws LiveException {
    //    Call respCall = apiService.closeLiveRoom(roomId);
    //    handleResponseCall(respCall);
    //}

    public List<LiveRoom> getLiveRoomList(int pageNum, int pageSize) throws LiveException {
        Call<ResponseModule<List<LiveRoom>>> respCall = apiService.getLiveRoomList(pageNum, pageSize);

        ResponseModule<List<LiveRoom>> response = handleResponseCall(respCall).body();
        return response.data;
    }

    public ResponseModule<List<LiveRoom>> getLivingRoomList(int limit, String cursor) throws LiveException {
        Call<ResponseModule<List<LiveRoom>>> respCall = apiService.getLivingRoomList(limit, cursor);

        ResponseModule<List<LiveRoom>> response = handleResponseCall(respCall).body();

        return response;
    }

    public LiveRoom getLiveRoomDetails(String roomId) throws LiveException {
        return handleResponseCall(apiService.getLiveRoomDetails(roomId)).body().data;
    }

    public List<String> getAssociatedRooms(String userId) throws LiveException {
        ResponseModule<List<String>> response = handleResponseCall(apiService.getAssociatedRoom(userId)).body();
        return response.data;
    }

    //public void grantLiveRoomAdmin(String roomId, String adminId) throws LiveException {
    //    GrantAdminModule module = new GrantAdminModule();
    //    module.newAdmin = adminId;
    //    handleResponseCall(apiService.grantAdmin(roomId, module));
    //}
    //
    //public void revokeLiveRoomAdmin(String roomId, String adminId) throws LiveException {
    //    handleResponseCall(apiService.revokeAdmin(roomId, adminId));
    //}
    //
    //public void grantLiveRoomAnchor(String roomId, String anchorId) throws LiveException {
    //    handleResponseCall(apiService.grantAnchor(roomId, anchorId));
    //}
    //
    //public void revokeLiveRoomAnchor(String roomId, String anchorId) throws LiveException {
    //    handleResponseCall(apiService.revokeAdmin(roomId, anchorId));
    //}
    //
    //public void kickLiveRoomMember(String roomId, String memberId) throws LiveException {
    //    handleResponseCall(apiService.kickMember(roomId, memberId));
    //}

    public void postStatistics(StatisticsType type, String roomId, int count) throws LiveException {
        JSONObject jobj = new JSONObject();
        try {
            jobj.put("type", type);
            jobj.put("count", count);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        handleResponseCall(apiService.postStatistics(roomId, jsonToRequestBody(jobj.toString())));
    }

    public void postStatistics(StatisticsType type, String roomId, String username) throws LiveException {
        JSONObject jobj = new JSONObject();
        try {
            jobj.put("type", type);
            jobj.put("count", username);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        handleResponseCall(apiService.postStatistics(roomId, jsonToRequestBody(jobj.toString())));
    }

    private <T> Response<T> handleResponseCall(Call<T> responseCall) throws LiveException {
        try {
            L.e("chatRoom","responseCall:"+responseCall.toString());
            Response<T> response = responseCall.execute();
            L.e("chatRoom","response:"+response.toString());
            if (!response.isSuccessful()) {
                throw new LiveException(response.code(), response.errorBody().string());
            }
            return response;
        } catch (IOException e) {
            throw new LiveException(e.getMessage());
        }
    }

    private RequestBody jsonToRequestBody(String jsonStr) {
        return RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonStr);
    }
}
