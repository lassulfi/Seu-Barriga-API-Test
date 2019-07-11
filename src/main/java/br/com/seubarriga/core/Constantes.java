package br.com.seubarriga.core;

import io.restassured.http.ContentType;

public interface Constantes {

	String APP_BASE_URL = "https://barrigarest.wcaquino.me/";
	Integer APP_PORT = 443; // http -> use port 80
	String APP_BASE_PATH = "";
	
	ContentType APP_CONTENT_TYPE = ContentType.JSON;
	
	Long MAX_TIMEOUT = 7000L;
}
