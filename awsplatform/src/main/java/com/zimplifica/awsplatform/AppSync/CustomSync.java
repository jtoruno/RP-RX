package com.zimplifica.awsplatform.AppSync;

import com.amazonaws.mobileconnectors.appsync.AWSAppSyncClient;
import com.amazonaws.mobileconnectors.appsync.AppSyncSubscriptionCall;
import com.apollographql.apollo.GraphQLCall;
import com.apollographql.apollo.api.Query;
import com.apollographql.apollo.api.Subscription;
import com.apollographql.apollo.internal.util.Cancelable;

public class CustomSync {

    private Cancelable handle;
    private AWSAppSyncClient client;

    public CustomSync(AWSAppSyncClient client) {
        this.client = client;
    }

    public Cancelable sync(Query baseQuery, GraphQLCall.Callback baseQueryCallback, Subscription subscription, AppSyncSubscriptionCall.Callback subscriptionCallback,
                           Query deltaQuery, GraphQLCall.Callback deltaQueryCallback, long baseRefreshingIntervalInSeconds) {

        handle = client.sync(baseQuery, baseQueryCallback, subscription, subscriptionCallback, deltaQuery, deltaQueryCallback, baseRefreshingIntervalInSeconds);
        return handle;
    }

    public void closeSync() {
        if (handle != null) {
            handle.cancel();
        }
    }
}