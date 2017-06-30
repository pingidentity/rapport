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
package com.pingidentity.labs.rapport;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javax.json.Json;
import javax.json.JsonObject;

/**
 * Main execution entry point for running one or more applications, based on a backend within the classpath.
 * 
 * 
 */
public class Main {
	/**
	 * Execution program for running constitution files via backends supporting an ApplicationFactory
	 * 
	 * @param args String arguments - a constitution configuration file.
	 * @throws FileNotFoundException one of the files was unable to be found  
	 * @throws ClassNotFoundException application class was unable to be found.
	 */
	public static void main(String[] args) throws FileNotFoundException, ClassNotFoundException {
		if (args.length != 1) {
			System.err.println("Usage:");
			System.err.println("app <config-file>");
			System.exit(1);
		}
		JsonObject configurationData = Json.createReader(new FileInputStream(args[0])).readObject();
		String applicationClassName = configurationData.getString("applicationClass");

		@SuppressWarnings("rawtypes")
		Class<? extends Application> applicationClass = Class.forName(applicationClassName).asSubclass(Application.class);
		try {
			ApplicationRunner.newInstance(applicationClass).init(configurationData);
		}
		catch (Exception e) {
			System.err.println("Exception while running application");
			e.printStackTrace(System.err);
		}
	}
}
