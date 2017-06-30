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
package com.pingidentity.labs.rapport.spi;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.pingidentity.labs.rapport.Peer;

/**
 * Helper for parsing name/port IPv4 and IPv6 addresses, for the purpose of parsing {@link Peer}
 * implementations that contain connection info.
 * 
 * Supports addresses in the form of:
 * - name:port
 * - 11.22.33.44:port
 * - [::FF]:port
 */
public class SocketAddressUtils {

	private static Pattern IPV6_ADDRESS_PATTERN;

	static {
		IPV6_ADDRESS_PATTERN = Pattern.compile("\\[([^\\]]+)\\]:([0-9]+)");
	}

	private static InetSocketAddress unmarshalIpv6(String v) {
		Matcher m = IPV6_ADDRESS_PATTERN.matcher(v);
		if (!m.matches()) {
			throw new IllegalArgumentException("Unable to parse as ipv6 address");
		}
		return new InetSocketAddress(m.group(1), Integer.parseInt(m.group(2)));
	}

	/**
	 * Convert a socket address to a string representation. Will attempt to preserve the hostname, if
	 * the socket address was created with one.
	 * 
	 * @param socketAddress IP and port of address
	 * @return string format of socket address
	 */
	public static String socketToString(InetSocketAddress socketAddress) {
		String host = socketAddress.getHostString();
		if (host.contains(":")) {
			host = "[" + host + "]";
		}
		return host + ":" + socketAddress.getPort();
	}

	/**
	 * Convert a string representation of a socket address into a java Object.
	 * @param address string representation of host/ip and port
	 * @return socket address object
	 * 
	 * @throws IllegalArgumentException if the address is not able to be parsed
	 */
	public static InetSocketAddress stringToSocket(String address) throws IllegalArgumentException {
		if (address.startsWith("[")) {
			return unmarshalIpv6(address);
		}
		
		String[] parts = address.split(":");
		if (parts.length != 2) {
			throw new IllegalArgumentException("no port detected on address");
		}

		InetAddress addr;
		int port;
		
		try {
			addr = InetAddress.getByName(parts[0]);
		} catch (Exception e) {
			throw new IllegalArgumentException("Unable to parse host/ip");
		}
		try {
			port = Integer.parseInt(parts[1]);
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("unable to parse port");
		}
		return new InetSocketAddress(addr, port);
	}
}
