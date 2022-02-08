package com.jdoodle;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;

import com.config.Headers;
import com.config.Origin;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

@WebServlet("/Jdoodle")
public class Jdoodle extends HttpServlet {
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String clientId = ""; //Replace with your client ID
        String clientSecret = ""; //Replace with your client Secret
        String script = request.getParameter("script");
        String language = request.getParameter("language");
        String versionIndex = request.getParameter("version");
        String stdin = request.getParameter("stdin");
        JSONObject json = new JSONObject();
        System.out.println("XXXXXXXXXXXXXXXX");
        System.out.println(script);
        System.out.println("XXXXXXXXXXXXXXXX");
        json.put("clientId", clientId);
        json.put("clientSecret", clientSecret);
        json.put("script", script);
        json.put("language", language);
        json.put("versionIndex", versionIndex);
        json.put("stdin", stdin);
        
        Headers.setRequiredHeaders(response, Origin.getAdmin());
        PrintWriter out = response.getWriter();
        String responseOutput = "";
        try {
            URL url = new URL("https://api.jdoodle.com/v1/execute");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            
            String input = json.toString();

            System.out.println(input);

            OutputStream outputStream = connection.getOutputStream();
            outputStream.write(input.getBytes());
            outputStream.flush();

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new RuntimeException("Please check your inputs : HTTP error code : "+ connection.getResponseCode());
            }

            BufferedReader bufferedReader;
            bufferedReader = new BufferedReader(new InputStreamReader(
            (connection.getInputStream())));
            String output = "";
            System.out.println("Output from JDoodle .... \n");
            while ((output = bufferedReader.readLine()) != null) {
            	responseOutput += output;
                System.out.println(output);
            }
            System.out.println();

            out.println(responseOutput);
            connection.disconnect();
            return;

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
	}

}
