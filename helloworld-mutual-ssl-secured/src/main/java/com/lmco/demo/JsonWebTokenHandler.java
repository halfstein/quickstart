package com.lmco.demo;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.KeyStoreException;
import java.util.Base64;

import javax.json.Json;
import javax.json.JsonObject;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.HttpString;

public class JsonWebTokenHandler implements HttpHandler {
	
	static HttpString AUTHORIZATION = new HttpString("Authorization");
	
	HttpHandler next;
	
	String subject = "quickstartUser";
	
	public JsonWebTokenHandler(HttpHandler next) throws IOException, KeyStoreException {
		this.next = next;	
	}
	
	@Override
	public void handleRequest(HttpServerExchange exchange) throws Exception {

		exchange.getRequestHeaders().add(AUTHORIZATION, String.format("Bearer %s", new JsonWebToken(this.subject)));
		this.next.handleRequest(exchange);
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	class JsonWebToken {
		
		String subject;
		public JsonWebToken(String subject) {
			this.subject = subject;
		}
		
		private String encode(JsonObject json) {
			return new String(Base64.getUrlEncoder().encode(json.toString().getBytes()), StandardCharsets.UTF_8);
		}
		
		private JsonObject header() {
			return Json.createObjectBuilder()
					.add("typ", "JWT")
					.build();
		}
		
		private JsonObject claims() {
			return Json.createObjectBuilder()
					.add("sub", this.subject)
					.add("exp", currentTimeInSeconds() + 60 )
					.build();
		}
		
		public String toString(){
			return encode(header()) + "." + encode(claims()) + ".";
		}
		
	    private int currentTimeInSeconds() {
	        return ((int) (System.currentTimeMillis() / 1000));
	    }
	}
}
