package io.sentry.android.core;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.sentry.android.core.adapters.ContextsDeserializerAdapter;
import io.sentry.android.core.adapters.DateDeserializerAdapter;
import io.sentry.android.core.adapters.DateSerializerAdapter;
import io.sentry.android.core.adapters.OrientationDeserializerAdapter;
import io.sentry.android.core.adapters.OrientationSerializerAdapter;
import io.sentry.android.core.adapters.SentryIdDeserializerAdapter;
import io.sentry.android.core.adapters.SentryIdSerializerAdapter;
import io.sentry.android.core.adapters.SentryLevelDeserializerAdapter;
import io.sentry.android.core.adapters.SentryLevelSerializerAdapter;
import io.sentry.android.core.adapters.TimeZoneDeserializerAdapter;
import io.sentry.android.core.adapters.TimeZoneSerializerAdapter;
import io.sentry.core.ILogger;
import io.sentry.core.ISerializer;
import io.sentry.core.SentryEvent;
import io.sentry.core.SentryLevel;
import io.sentry.core.protocol.Contexts;
import io.sentry.core.protocol.Device;
import io.sentry.core.protocol.SentryId;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Date;
import java.util.TimeZone;
import org.jetbrains.annotations.NotNull;

final class AndroidSerializer implements ISerializer {

  private final @NotNull ILogger logger;
  private final Gson gson;

  public AndroidSerializer(final @NotNull ILogger logger) {
    this.logger = logger;

    gson = provideGson();
  }

  private Gson provideGson() {
    return new GsonBuilder()
        .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
        .registerTypeAdapter(SentryId.class, new SentryIdSerializerAdapter(logger))
        .registerTypeAdapter(SentryId.class, new SentryIdDeserializerAdapter(logger))
        .registerTypeAdapter(Date.class, new DateSerializerAdapter(logger))
        .registerTypeAdapter(Date.class, new DateDeserializerAdapter(logger))
        .registerTypeAdapter(TimeZone.class, new TimeZoneSerializerAdapter(logger))
        .registerTypeAdapter(TimeZone.class, new TimeZoneDeserializerAdapter(logger))
        .registerTypeAdapter(
            Device.DeviceOrientation.class, new OrientationSerializerAdapter(logger))
        .registerTypeAdapter(
            Device.DeviceOrientation.class, new OrientationDeserializerAdapter(logger))
        .registerTypeAdapter(SentryLevel.class, new SentryLevelSerializerAdapter(logger))
        .registerTypeAdapter(SentryLevel.class, new SentryLevelDeserializerAdapter(logger))
        .registerTypeAdapter(Contexts.class, new ContextsDeserializerAdapter(logger))
        .registerTypeAdapterFactory(UnknownPropertiesTypeAdapterFactory.get())
        .create();
  }

  @Override
  public SentryEvent deserializeEvent(Reader eventReader) {
    return gson.fromJson(eventReader, SentryEvent.class);
  }

  @Override
  public void serialize(SentryEvent event, Writer writer) throws IOException {
    gson.toJson(event, SentryEvent.class, writer);
    writer.flush();
    writer.close();
  }
}
