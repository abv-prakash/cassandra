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

import static com.abhinavp.examples.api.CColumnFamilyDefinitions.ClickEvent;
import static com.abhinavp.examples.api.CColumnFamilyDefinitions.CompositeEvents;
import static com.abhinavp.examples.api.CColumnFamilyDefinitions.GoalEvent;

import com.abhinavp.examples.api.CColumnFamilyDefinitions.Composites;
import com.netflix.astyanax.Keyspace;
import com.netflix.astyanax.MutationBatch;
import com.netflix.astyanax.connectionpool.OperationResult;
import com.netflix.astyanax.connectionpool.exceptions.ConnectionException;
import com.netflix.astyanax.model.Column;
import com.netflix.astyanax.model.ColumnList;
import com.netflix.astyanax.model.CqlResult;
import com.netflix.astyanax.model.Equality;
import com.netflix.astyanax.model.Row;
import com.netflix.astyanax.model.Rows;
import com.netflix.astyanax.query.RowQuery;
import com.netflix.astyanax.serializers.StringSerializer;
import com.netflix.astyanax.util.RangeBuilder;

/**
 */
public class CassandraAccessor {

	private static final String INSERT_STATEMENT = String
			.format("INSERT INTO clickevent (id,eventrecievedat,eventrequestid) values(?,?,?) ");

	public void mutateCQLQuery(final Keyspace keyspace) {

		OperationResult<CqlResult<String, String>> result = null;

		try {
			result = keyspace.prepareQuery(ClickEvent).withCql(INSERT_STATEMENT).asPreparedStatement()
					.withStringValue("appId1#DeviceId1#clk").withStringValue("eventTime1")
					.withStringValue("clickReqId1").execute();
		} catch (ConnectionException e) {

			e.printStackTrace();
		}

	}

	public void mutate(final Keyspace keyspace) {
		MutationBatch m = keyspace.prepareMutationBatch();

		m.withRow(GoalEvent, "appId2#DeviceId2#gle").putColumn("eventTime1", "goalEventId1", null)
				.putColumn("eventTime2", "goalEventId2", null);

		try {
			OperationResult<Void> result = m.execute();
		} catch (ConnectionException e) {
			e.printStackTrace();
		}

	}

	public void readOp(final Keyspace keyspace) {

		OperationResult<ColumnList<String>> result = null;
		try {
			result = keyspace.prepareQuery(GoalEvent).getKey("appId2#DeviceId2#gle").execute();
		} catch (ConnectionException e) {
			e.printStackTrace();
		}

		ColumnList<String> columns = result.getResult();

		String eventReqId = columns.getColumnByName("eventTime1").getStringValue();
		System.out.println("Value for column - eventTime1 is " + eventReqId);

		for (Column<String> column : columns) {

			System.out.println("Found columnName:" + column.getName());
			System.out.println("Found columnValue :" + column.getValue(StringSerializer.get()));
		}

	}

	public void readNonContiguousRow(final Keyspace keyspace) {

		OperationResult<Rows<String, String>> result = null;

		try {
			result = keyspace.prepareQuery(ClickEvent).getKeySlice("appId1#deviceId1#clk", "appId2#deviceId2#clk")
					.execute();
		} catch (ConnectionException e) {
			e.printStackTrace();
		}

		for (Row<String, String> row : result.getResult()) {

			for (Column<String> column : row.getColumns()) {
				System.out.println(column.getName());
			}

		}

	}

	public void readCompositeColumns(final Keyspace keyspace) {

		Composites startColumn = new CColumnFamilyDefinitions().new Composites(1234L, "id1");
		Composites endColumn = new CColumnFamilyDefinitions().new Composites(6789L, "id1");

		OperationResult<ColumnList<Composites>> result = null;

		try {
			result = keyspace
					.prepareQuery(CompositeEvents)
					.getKey("Key1")
					.withColumnRange(
							CColumnFamilyDefinitions.getCompositeEventSerializer(Composites.class)
									.makeEndpoint(startColumn, Equality.EQUAL).toBytes(),
							CColumnFamilyDefinitions.getCompositeEventSerializer(Composites.class)
									.makeEndpoint(endColumn, Equality.LESS_THAN_EQUALS).toBytes(), false, 100)
					.execute();
		} catch (ConnectionException e) {

			e.printStackTrace();
		}

	}

	/*
	 * This will query a row and return 10 columns at a time */
	/**
	 * @param keyspace
	 */
	public void pagination(final Keyspace keyspace) {

		ColumnList<String> columns = null;

		RowQuery<String, String> rows = keyspace.prepareQuery(ClickEvent).getKey("appId1#deviceId1#clk")
				.autoPaginate(true).withColumnRange(new RangeBuilder().setLimit(10).build());

		try {
			while (!(columns = rows.execute().getResult()).isEmpty()) {
				for (Column<String> c : columns) {
					System.out.println(c.getName());
				}
			}
		} catch (ConnectionException e) {

			e.printStackTrace();
		}

	}

}
