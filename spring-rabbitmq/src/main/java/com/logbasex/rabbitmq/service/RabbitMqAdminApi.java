package com.logbasex.rabbitmq.service;

import com.logbasex.rabbitmq.dto.RabbitMqPermissionRequest;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface RabbitMqAdminApi {
	@PUT("vhosts/{vhost}")
	Call<Void> createVhost(@Path("vhost") String vhost);

	@PUT("permissions/{vhost}/{user}")
	Call<Void> setPermissions(@Path("vhost") String vhost,
							  @Path("user") String user,
							  @Body RabbitMqPermissionRequest body);
}
