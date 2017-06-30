/* Copyright 2017 Ping Identity Corporation

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License. */
package com.pingidentity.labs.rapport.bridge.swirlds.converter;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.Base64;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import javax.json.JsonString;
import javax.json.JsonValue;
import javax.json.JsonWriter;

import com.pingidentity.labs.rapport.spi.SocketAddressUtils;

public class Converter {
	public static void main(String[] args) throws IOException {
		if (args.length == 1 && args[0].contains("-h")) {
			printHelp();
			System.exit(0);
		}
		Reader configReader = null;
		PrintWriter configWriter = null;
		if (args.length >= 1) {
			configReader = new FileReader(args[0]);
		}
		else {
			configReader = new InputStreamReader(System.in, Charset.forName("UTF-8"));
		}
		if (args.length == 2) {
			configWriter = new PrintWriter(new FileWriter(args[1]));
		} else {
			configWriter = new PrintWriter(new OutputStreamWriter(System.out));
		}
		JsonReader reader = Json.createReader(configReader);
		JsonObject constitution = reader.readObject();
		JsonArray addresses = constitution.getJsonArray("addresses");
		constitution = removeAddresses(constitution);
		String base64Constitution = base64EncodeJson(constitution);
		configWriter.println("rapport-impl-swirlds" + "," + base64Constitution);
		for (JsonValue addressValue:addresses) {
			JsonObject address = (JsonObject) addressValue;
			String name = address.getString("name");
			String externalAddressStr = address.getString("externalAddress");
			JsonString internalAddressJson = address.getJsonString("internalAddress");
			String internalAddressStr = internalAddressJson != null ? internalAddressJson.toString() : externalAddressStr;

			InetSocketAddress externalAddress = SocketAddressUtils.stringToSocket(externalAddressStr);
			InetSocketAddress internalAddress = SocketAddressUtils.stringToSocket(internalAddressStr);

			configWriter.println(
					String.join(
							",",
							name,
							name,
							Integer.toString(internalAddress.getPort()),
							internalAddress.getAddress().getHostAddress(),
							Integer.toString(externalAddress.getPort()),
							externalAddress.getAddress().getHostAddress()
							));
		}
		configWriter.close();
	}
	
	private static JsonObject removeAddresses(JsonObject constitution) {
		JsonObjectBuilder builder = Json.createObjectBuilder();
		constitution.keySet().stream()
			.filter((key) -> !key.equals("addresses"))
			.forEach((key) -> builder.add(key, constitution.get(key)));
		return builder.build();
	}

	private static String base64EncodeJson(JsonObject constitution) {
		StringWriter stringWriter = new StringWriter();
		JsonWriter writer = Json.createWriter(stringWriter);
		writer.writeObject(constitution);
		return Base64.getEncoder().encodeToString(
				stringWriter.toString().getBytes(Charset.forName("UTF-8")));
	}
	
	private static void printHelp() {
		System.err.println("Usage:");
		System.err.println("\tapp [filename [output]");
		System.err.println("If output is omitted, will use stdout");
		System.err.println("If filename is omitted, will use stdin");
	}

}
