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
/**
 * SPI for adding back-ends for Rapport, to run and interact with applications. 
 * 
 * The SPI components are mostly optional, however 
 * {@link com.pingidentity.labs.rapport.spi.ApplicationFactory} is the service interface to allow the 
 * {@link com.pingidentity.labs.rapport.ApplicationRunner} and the {@link com.pingidentity.labs.rapport.Main}
 * application within the rapport-api.jar to execute based on your back-end.
 */
package com.pingidentity.labs.rapport.spi;
