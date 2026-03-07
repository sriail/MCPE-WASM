package com.microsoft.onlineid.internal;

import android.net.Uri;
import android.os.Bundle;
import com.facebook.applinks.AppLinkData;
import com.microsoft.onlineid.RequestOptions;
import java.util.List;

public class Intents {

    public static class DataBuilder {
        private final Uri.Builder _builder = new Uri.Builder();

        public DataBuilder() {
            this._builder.scheme(AppLinkData.ARGUMENTS_EXTRAS_KEY);
        }

        public DataBuilder add(String component) {
            this._builder.appendPath("str").appendPath(component);
            return this;
        }

        public DataBuilder add(List<String> components) {
            this._builder.appendPath("list");
            if (components != null) {
                for (String component : components) {
                    this._builder.appendPath(component);
                }
            } else {
                this._builder.appendPath("null");
            }
            return this;
        }

        /* Debug info: failed to restart local var, previous not found, register: 3 */
        public DataBuilder add(RequestOptions options) {
            this._builder.appendPath("options");
            if (options == null) {
                this._builder.appendPath("null");
                return this;
            }
            Bundle optionsBundle = options.asBundle();
            if (optionsBundle != null) {
                return add(optionsBundle.toString());
            }
            this._builder.appendPath("empty");
            return this;
        }

        public Uri build() {
            return this._builder.build();
        }
    }
}
