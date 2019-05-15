/*
 * Copyright 2012-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.goinsecure.jerseyapp;

import javax.validation.constraints.NotNull;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.springframework.stereotype.Component;
import org.springframework.util.Base64Utils;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Component
@Path("/")
public class HttpEndpoint {

	private List<String> SYSTEMS = Arrays.asList("web1","web2","db1");

	@GET
	@Path("/")
	@Produces("text/html")
	public String index() throws IOException {
		return getFileContent("index.html");
	}

	@GET
	@Path("/images/{filename}")
	public Response images(@PathParam("filename") String filename) throws IOException {
		if(filename.contains("..")) throw new FileNotFoundException("Image not found");
		URL url = Resources.getResource("images/"+filename);
		return Response.ok(url.openStream(), MediaType.TEXT_PLAIN_TYPE).build();
	}

	@POST
	@Path("/rpc")
	public String rpc(@FormParam("input") @NotNull String input) {
		byte[] payload = Base64Utils.decodeFromString(input);
		return "ACK";
	}

	@GET
	@Path("/getStatus")
	public String getStatus(@QueryParam("systemid") @NotNull String input) throws InterruptedException {
		Thread.sleep(500);
		if(SYSTEMS.contains(input)) {
			return "UP";
		}
		if(input.equals("db2") || input.equals("db3")) {
			return new Random().nextBoolean() ? "UP" : "DOWN";
		}
		return "DOWN";
	}

	@GET
	@Path("/getProcessList")
	@Produces("text/plain")
	public String getStatus() throws IOException {

		return execCommand("ps -ef");
	}

	@POST
	@Path("/uploadBackup")
	public String uploadBackup(@FormParam("backup") @NotNull String input) {
		byte[] payload = Base64Utils.decodeFromString(input);

		try {
			ObjectInputStream inputStream = new ObjectInputStream(new ByteArrayInputStream(payload));
			inputStream.readObject();
		}
		catch (IOException | ClassNotFoundException e) {
			//e.printStackTrace();
		}

		return "SEND_TO_QUEUE";
	}

	public static String execCommand(String command) throws IOException {
		Process proc = Runtime.getRuntime().exec(command);
		java.io.InputStream is = proc.getInputStream();
		java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
		String val = "";
		if (s.hasNext()) {
			val = s.next();
		}
		else {
			val = "";
		}
		return val;
	}

	private String getFileContent(String filename) throws IOException {
		URL url = Resources.getResource(filename);
		return Resources.toString(url, Charsets.UTF_8);
	}
}
