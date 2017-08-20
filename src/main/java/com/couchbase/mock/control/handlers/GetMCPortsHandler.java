/*
 * Copyright 2017 Couchbase, Inc.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.couchbase.mock.control.handlers;

import com.couchbase.mock.Bucket;
import com.couchbase.mock.CouchbaseMock;
import com.couchbase.mock.control.CommandStatus;
import com.couchbase.mock.control.MockCommand;
import com.couchbase.mock.memcached.MemcachedServer;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.jetbrains.annotations.NotNull;

/**
 * Needed to get a list of numeric ports for a given
 *
 */
public class GetMCPortsHandler extends MockCommand {
    @Override
    @NotNull
    public CommandStatus execute(@NotNull CouchbaseMock mock, @NotNull Command command, @NotNull JsonObject payload) {
        String name;
        if (payload.has("bucket")) {
            name = payload.get("bucket").getAsString();
        } else {
            name = "default";
        }



        JsonArray arr = new JsonArray();
        Bucket bucket = mock.getBuckets().get(name);
        if (bucket == null) {
            return new CommandStatus().fail("No such bucket: " + name);
        }
        for (MemcachedServer server : bucket.getServers()) {
            int port = server.getPort();
            arr.add(new JsonPrimitive(port));
        }
        CommandStatus status = new CommandStatus();
        status.setPayload(arr);
        return status;
    }
}
