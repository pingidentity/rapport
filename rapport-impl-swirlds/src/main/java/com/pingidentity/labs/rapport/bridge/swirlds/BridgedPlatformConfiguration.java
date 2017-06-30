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
package com.pingidentity.labs.rapport.bridge.swirlds;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.Base64;
import java.util.Map;
import java.util.Objects;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonValue;

import com.pingidentity.labs.rapport.Application;

public class BridgedPlatformConfiguration {
	@SuppressWarnings("rawtypes")
	private final Class<? extends Application> applicationClass;
	private final JsonValue constitution;
	private final Map<String, JsonValue> localConfiguration;
	
	public BridgedPlatformConfiguration(@SuppressWarnings("rawtypes") Class<? extends Application> applicationClass, JsonValue constitution, Map<String, JsonValue> localConfiguration) {
		Objects.requireNonNull(applicationClass);		
		this.applicationClass = applicationClass;
		this.constitution = constitution;
		this.localConfiguration = localConfiguration;
	}

	public static BridgedPlatformConfiguration fromParameters(String[] parameters) {
		Objects.requireNonNull(parameters);		
		JsonObject configuration = configurationFromBase64(parameters[0]);
		String applicationClassName = configuration.getString("applicationClass");
		@SuppressWarnings("rawtypes")
		Class<? extends Application> applicationClass;
		try {
			applicationClass = 
			Class.forName(applicationClassName).asSubclass(Application.class);
			
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("Application " + applicationClassName + " not available.", e);
		}
		return new BridgedPlatformConfiguration(
				applicationClass, 
				configuration.get("constitution"), 
				configuration.getJsonObject("localConfiguration"));
	}
	private static JsonObject configurationFromBase64(String base64EncodedConstitution) {
		byte[] constitutionBytes = Base64.getDecoder().decode(base64EncodedConstitution);
		Reader reader = new InputStreamReader(new ByteArrayInputStream(constitutionBytes), Charset.forName("UTF-8"));
		return Json.createReader(reader).readObject();
	}

	public Map<String, JsonValue> getLocalConfiguration() {
		return localConfiguration;
	}

	public JsonValue getConstitution() {
		return constitution;
	}

	@SuppressWarnings("rawtypes")
	public Class<? extends Application> getApplicationClass() {
		return applicationClass;
	}
}
