package de.urkallinger.restclient.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

public class WebUtils {
	public static String getPublicIp() throws Exception {
		try (InputStream is = new URL("http://checkip.amazonaws.com").openStream();
				BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
			String ip = br.readLine();
			return ip;
		}
	}
	
}
