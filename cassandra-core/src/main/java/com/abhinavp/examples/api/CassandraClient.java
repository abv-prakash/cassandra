// ========================================================================
// Copyright (c) 2009-2009 Mort Bay Consulting Pty. Ltd.
// ------------------------------------------------------------------------
// All rights reserved. This program and the accompanying materials
// are made available under the terms of the Eclipse Public License v1.0
// and Apache License v2.0 which accompanies this distribution.
// The Eclipse Public License is available at
// http://www.eclipse.org/legal/epl-v10.html
// The Apache License v2.0 is available at
// http://www.opensource.org/licenses/apache2.0.php
// You may elect to redistribute this code under either of these licenses.
// ========================================================================

package com.abhinavp.examples.api;

import com.netflix.astyanax.Keyspace;

/**
 */
public class CassandraClient {

	public static void mutateCql(final Keyspace keyspace) {
		new CassandraAccessor().mutateCQLQuery(keyspace);
	}

	public static void mutateCli(final Keyspace keyspace) {
		new CassandraAccessor().mutate(keyspace);
	}

	public static void readCli(final Keyspace keyspace) {
		new CassandraAccessor().readOp(keyspace);
	}

	public static void main(final String... args) {

		Keyspace keyspace = new CassandraContext().initializeContext();
		readCli(keyspace);

	}

}
