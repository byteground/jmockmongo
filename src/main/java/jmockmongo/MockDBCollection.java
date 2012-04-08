/**
 * Copyright (c) 2012, Thilo Planz. All rights reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the Apache License, Version 2.0
 * as published by the Apache Software Foundation (the "License").
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 * You should have received a copy of the License along with this program.
 * If not, see <http://www.apache.org/licenses/LICENSE-2.0>.
 */

package jmockmongo;

import java.math.BigInteger;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.bson.BSONObject;
import org.bson.BasicBSONObject;
import org.bson.types.ObjectId;

import com.mongodb.DBRef;

public class MockDBCollection {

	private final String name;

	private final Map<Object, BSONObject> data = new ConcurrentHashMap<Object, BSONObject>();

	MockDBCollection(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	private Object fixForHash(Object id) {
		// byte[] don't hash...
		if (id instanceof byte[]) {
			return new BigInteger((byte[]) id);
		}
		// DBRef don't hash
		if (id instanceof DBRef) {
			return new BasicBSONObject("$ref", ((DBRef) id).getRef()).append(
					"$id", ((DBRef) id).getId());
		}
		return id;
	}

	public BSONObject findOne(Object id) {

		return data.get(fixForHash(id));
	}

	public void insert(BSONObject b) {
		if (!b.containsField("_id")) {
			b.put("_id", new ObjectId());
		}
		Object id = b.get("_id");

		data.put(fixForHash(id), b);
	}

	public Collection<BSONObject> documents() {
		return data.values();
	}
}
