package com.microsoft.cll.android;

import com.microsoft.cll.android.SettingsStore;
import java.util.Arrays;
import java.util.zip.Deflater;
import net.hockeyapp.android.utils.HttpURLConnectionBuilder;

public class EventCompressor {
    private final String TAG = "AndroidCll-EventCompressor";
    private final ILogger logger;

    public EventCompressor(ILogger logger2) {
        this.logger = logger2;
    }

    public byte[] compress(String events) {
        try {
            byte[] input = events.getBytes(HttpURLConnectionBuilder.DEFAULT_CHARSET);
            byte[] output = new byte[SettingsStore.getCllSettingsAsInt(SettingsStore.Settings.MAXEVENTSIZEINBYTES)];
            Deflater compressor = new Deflater(-1, true);
            compressor.setInput(input);
            compressor.finish();
            int compressedDataLength = compressor.deflate(output);
            if (compressedDataLength < SettingsStore.getCllSettingsAsInt(SettingsStore.Settings.MAXEVENTSIZEINBYTES)) {
                return Arrays.copyOfRange(output, 0, compressedDataLength);
            }
            this.logger.error("AndroidCll-EventCompressor", "Compression resulted in a string of at least the max event buffer size of Vortex. Most likely this means we lost part of the string.");
            return null;
        } catch (Exception e) {
            this.logger.error("AndroidCll-EventCompressor", "Could not compress events");
            return null;
        }
    }
}
