package com.vkv.learnmaths;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;

public class TextHelper {
	
	public static String GetText(HttpResponse response) throws IllegalStateException, IOException {
	    String text = "";
	    InputStream inputStream = response.getEntity().getContent();
	    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
	    StringBuilder sb = new StringBuilder();
	    String line = null;
	    try {
	      while ((line = reader.readLine()) != null) {
	        sb.append(line);
	      }
	      text = sb.toString();
	    } catch (Exception ex) {} 
	    finally {
	      try {
	    	  inputStream.close();
	      } catch (Exception ex) {}
	    }
	    
	    return text;
	  }
}
