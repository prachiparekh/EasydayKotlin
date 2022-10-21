package com.app.easyday.app.sources.remote.apis

import com.app.easyday.app.sources.ApiResponse
import com.app.easyday.app.sources.remote.model.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*
import rx.Observable

interface EasyDayApi {

    @FormUrlEncoded
    @POST("user/send-otp")
    fun sendOTP(
        @Field("phone_number") phone_number: String,
        @Field("country_code") country_code: String
    ): Observable<ApiResponse<OTPRespModel>>

    @FormUrlEncoded
    @POST("user/verify-otp")
    fun verifyOTP(
        @Field("phone_number") phone_number: String,
        @Field("otp") otp: String,
        @Field("country_code") country_code: String,
        @Field("device_token") device_token: String,
        @Field("device_id") device_id: String,
        @Field("device_name") device_name: String
    ): Observable<ApiResponse<VerifyOTPRespModel>>

    @Multipart
    @POST("user/create-user")
    fun createUser(
        @Part("fullname") fullname: RequestBody,
        @Part("profession") profession: RequestBody,
        @Part("phone_number") phone_number: RequestBody,
        @Part("country_code") country_code: RequestBody,
        @Part profile_image: MultipartBody.Part?,
        @Part("device_token") device_token: String,
        @Part("device_id") device_id: String,
        @Part("device_name") device_name: String
    ): Observable<ApiResponse<UserModel>>

    @GET("user/get-profile")
    fun getProfile(): Observable<ApiResponse<UserModel>>

    @GET("project/get-projects")
    fun getAllProject(): Observable<ApiResponse<ArrayList<ProjectRespModel>>>

    @GET("project/get-project")
    fun getProject(@Query("project_id") project_id: Int): Observable<ApiResponse<ProjectRespModel>?>

    @GET("user/delete-user")
    fun deleteUser(): Observable<ApiResponse<UserModel>>


    @POST("project/create-project")
    fun createProject(
        @Body addProjectRequestModel: AddProjectRequestModelToPass
    ): Observable<ApiResponse<ProjectRespModel>>

    @Multipart
    @POST("task/add-task")
    fun addTask(
        @Part("project_id") project_id: RequestBody,
        @Part("title") title: RequestBody,
        @Part("description") description: RequestBody,
        @Part("priority") priority: RequestBody,
        @Part("red_flag") red_flag: RequestBody,
        @Part("due_date") due_date: RequestBody,
        @Part("tags[]") tags: ArrayList<Int>?,
        @Part("zones[]") zones: ArrayList<Int>?,
        @Part("spaces[]") spaces: ArrayList<Int>?,
        @Part task_media: ArrayList<MultipartBody.Part>,
        @Part("task_participants") task_participants: ArrayList<Int>?,
    ): Observable<ApiResponse<TaskResponse>>

    @GET("task/get-task")
    fun getTask(
        @Query("project_id") project_id: Int,
        @Query("zones") zones: ArrayList<Int>? = null,
        @Query("tags") tags: ArrayList<Int>?= null,
        @Query("spaces") spaces: ArrayList<Int>?= null,
        @Query("assigned_to") assigned_to: ArrayList<Int>?= null,
        @Query("red_flag") red_flag: Int?= null,
        @Query("due_date") due_date: Int?= null,
        @Query("priority") priority: Int?= null,
        @Query("date_range") date_range: ArrayList<String>?= null,
    ): Observable<ApiResponse<ArrayList<TaskResponse>>>

   /* @GET("task/get-task")
    fun getTask(
        @Query("project_id") project_id: Int
    ): Observable<ApiResponse<ArrayList<TaskResponse>>>*/

    @GET("project/get-attributes")
    fun getAttributes(
        @Query("project_id") project_id: Int,
        @Query("type") type: Int
    ): Observable<ApiResponse<ArrayList<AttributeResponse>>>

    @FormUrlEncoded
    @POST("project/add-attribute")
    fun addAttribute(
        @Field("type") type: Int,
        @Field("attribute_name") attribute_name: String,
        @Field("project_id") project_id: Int
    ): Observable<ApiResponse<AttributeResponse>>

    @GET("project/get-participants")
    fun getProjectParticipants(
        @Query("project_id") project_id: Int
    ): Observable<ApiResponse<ArrayList<ProjectParticipantsModel>>>

}

