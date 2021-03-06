package de.urkallinger.restclient.utils;

import java.util.regex.Pattern;

public class Constants {
	public final static String[] HTTP_HEADER = { "Accept", "Accept-Charset", "Accept-Encoding", "Accept-Language",
			"Authorization", "Cache-Control", "Connection", "Cookie", "Content-Length", "Content-MD5", "Content-Type",
			"Date", "Expect", "From", "Host", "If-Match", "If-Modified-Since", "If-None-Match", "If-Range",
			"If-Unmodified-Since", "Max-Forwards", "Pragma", "Proxy-Authorization", "Range", "Referer", "TE",
			"Transfer-Encoding", "Upgrade", "User-Agent", "Via", "Warning" };
	
	public final static String VARIABLE_TEMPLATE = "#%s#";
	public final static Pattern PROPERTY_PATTERN = Pattern.compile("#([^#]*)#");
}
