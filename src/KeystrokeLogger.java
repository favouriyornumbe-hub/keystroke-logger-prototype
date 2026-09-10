import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class KeystrokeLogger {

    public static final class Entry {

        public final String key;
        public final String timestamp;

        Entry(String key, String timestamp) {
            this.key = key;
            this.timestamp = timestamp;
        }
    }

    private static final DateTimeFormatter TIME_FORMAT =
            DateTimeFormatter.ofPattern("HH:mm:ss");

    private final List<Entry> entries =
            new ArrayList<>();

    private boolean recording = false;

    public boolean isRecording() {
        return recording;
    }

    public void startRecording() {
        recording = true;
    }

    public void stopRecording() {
        recording = false;
    }

    public void clearLog() {
        entries.clear();
    }

    public void recordPrintableKey(char c) {

        String label =
                (c == ' ')
                        ? "[SPACE]"
                        : String.valueOf(c);

        entries.add(
                new Entry(label, currentTimestamp())
        );
    }

    public void recordSpecialKey(String label) {

        entries.add(
                new Entry(label, currentTimestamp())
        );
    }

    private String currentTimestamp() {

        return LocalTime.now()
                .format(TIME_FORMAT);
    }

    public List<Entry> getEntries() {

        return Collections.unmodifiableList(entries);
    }

    public String getFormattedLog() {

        StringBuilder sb = new StringBuilder();

        for (Entry entry : entries) {

            sb.append(entry.timestamp)
              .append(" ")
              .append(entry.key)
              .append(System.lineSeparator());
        }

        return sb.toString();
    }
}