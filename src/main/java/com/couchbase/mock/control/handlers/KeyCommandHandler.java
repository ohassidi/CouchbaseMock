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
import com.couchbase.mock.control.MissingRequiredFieldException;
import com.couchbase.mock.control.MockCommand;
import com.couchbase.mock.memcached.KeySpec;
import com.couchbase.mock.memcached.VBucketInfo;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;

import java.security.AccessControlException;

/**
 *
 * @author Mark Nunberg
 */
public abstract class KeyCommandHandler extends MockCommand {
    KeySpec keySpec;
    Bucket bucket;
    VBucketInfo vbi;

    @NotNull
    @Override
    public CommandStatus execute(@NotNull CouchbaseMock mock, @NotNull Command command, @NotNull JsonObject payload) {
        short vbIndex = -1;
        String key;

        String bucketString = "default";
        if (payload.has("Bucket")) {
            bucketString = payload.get("Bucket").getAsString();
        }

        bucket = mock.getBuckets().get(bucketString);
        if (bucket == null) {
            throw new AccessControlException("No such bucket: " + bucketString);
        }

        if (!payload.has("Key")) {
            throw new MissingRequiredFieldException("Key");
        }

        key = payload.get("Key").getAsString();
        if (!payload.has("vBucket")) {
            vbIndex = bucket.getVbIndexForKey(key);
        }

        if (vbIndex < 0 || vbIndex >= bucket.getVBucketInfo().length) {
            throw new AccessControlException("Invalid vBucket " + vbIndex);
        }

        keySpec = new KeySpec(key, vbIndex);
        vbi = bucket.getVBucketInfo()[keySpec.vbId];
        // The return from this method is not returned back to the client
        return new CommandStatus().fail();
    }
}
