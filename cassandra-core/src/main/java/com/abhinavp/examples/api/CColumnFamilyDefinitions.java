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

import com.netflix.astyanax.annotations.Component;
import com.netflix.astyanax.model.ColumnFamily;
import com.netflix.astyanax.serializers.AnnotatedCompositeSerializer;
import com.netflix.astyanax.serializers.StringSerializer;

/* ------------------------------------------------------------ */
/**
 */
public class CColumnFamilyDefinitions {

	public class Composites {

		private @Component(ordinal = 0)
		final long time;
		private @Component(ordinal = 1)
		final String id;

		public Composites(final long time, final String id) {
			this.time = time;
			this.id = id;
		}

	}

	private static final StringSerializer SS = StringSerializer.get();

	public static final ColumnFamily<String, String> ClickEvent = getColumnFamily("clickevent");
	public static final ColumnFamily<String, Composites> CompositeEvents = getCompositeColumnFamily(Composites.class,
			"CompositeColumnEvents");

	public static final ColumnFamily<String, String> GoalEvent = getColumnFamily("goalEvent");

	private static ColumnFamily<String, String> getColumnFamily(final String name) {

		ColumnFamily<String, String> columnFamily = new ColumnFamily<String, String>(name, SS, SS);

		return columnFamily;

	}

	private static <T> ColumnFamily<String, T> getCompositeColumnFamily(final Class<T> clazz, final String name) {

		AnnotatedCompositeSerializer<T> eventSerializer = new AnnotatedCompositeSerializer<T>(clazz);

		ColumnFamily<String, T> columnFamily = new ColumnFamily<String, T>(name, SS, eventSerializer);

		return columnFamily;
	}

	public static <T> AnnotatedCompositeSerializer<T> getCompositeEventSerializer(final Class<T> clazz) {

		return new AnnotatedCompositeSerializer<T>(clazz);

	}
}
