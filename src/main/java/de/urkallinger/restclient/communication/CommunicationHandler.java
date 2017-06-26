package de.urkallinger.restclient.communication;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.urkallinger.restclient.data.RestData;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Request.Builder;
import okhttp3.RequestBody;

public class CommunicationHandler {

	private static final Logger LOGGER = LoggerFactory.getLogger(CommunicationHandler.class);

	public static void sendRequest(RestData data, Callback callback) {
		OkHttpClient client = new OkHttpClient()
				.newBuilder()
				.connectTimeout(2, TimeUnit.SECONDS)
				.readTimeout(2, TimeUnit.SECONDS)
				.writeTimeout(2, TimeUnit.SECONDS)
				.build();
				
		MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
		String url = data.getHost() + data.getPath();
		
		RequestBody body = RequestBody.create(mediaType, data.getPayload());
		Builder requestBilder = new Request.Builder().url(url);
		
		data.getHeaders().forEach(header -> {
			requestBilder.addHeader(header.getName(), header.getValue());
		});
		
		Request request;
		switch(data.getHttpMethod()) {
		case "POST":
			request = requestBilder.post(body).build();
			break;
		case "GET":
			request = requestBilder.get().build();
			break;
		case "DELETE":
			request = requestBilder.delete(body).build();
			break;
		case "PUT":
			request = requestBilder.put(body).build();
			break;
		default:
			LOGGER.warn(String.format("Http method \"%s\" is not supported yet.", data.getHttpMethod()));
			return;
		}
		
		
		Call call = client.newCall(request);
		call.enqueue(callback);
	}
}
