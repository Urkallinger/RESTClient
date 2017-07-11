package de.urkallinger.restclient.communication;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.urkallinger.restclient.data.DataManager;
import de.urkallinger.restclient.data.Property;
import de.urkallinger.restclient.data.RestData;
import de.urkallinger.restclient.utils.RestDataUtils;
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
		
		Map<String, Property> properties = DataManager.loadData().getProperties();
		String host = RestDataUtils.replaceProperties(data.getHost(), properties);
		String path = RestDataUtils.replaceProperties(data.getPath(), properties);
		String payload = RestDataUtils.replaceProperties(data.getPayload(), properties);
		
		OkHttpClient client = new OkHttpClient()
				.newBuilder()
				.connectTimeout(2, TimeUnit.SECONDS)
				.readTimeout(2, TimeUnit.SECONDS)
				.writeTimeout(2, TimeUnit.SECONDS)
				.build();
				
		MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
		String url = host + path;
		
		RequestBody body = RequestBody.create(mediaType, payload);
		Builder requestBilder = new Request.Builder().url(url);
		
		data.getHeaders().forEach(header -> {
			String name = RestDataUtils.replaceProperties(header.getName(), properties);
			String value = RestDataUtils.replaceProperties(header.getValue(), properties);
			requestBilder.addHeader(name, value);
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
