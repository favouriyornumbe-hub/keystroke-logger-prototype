import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public final class LogFileWriter {

    private LogFileWriter() {
        // Utility class; not instantiable.
    }

    public static void writeCsv(
            File file,
            List<KeystrokeLogger.Entry> entries)
            throws IOException {

        try (BufferedWriter writer =
                     new BufferedWriter(
                             new FileWriter(file))) {

            writer.write("Key,Timestamp");
            writer.newLine();

            for (KeystrokeLogger.Entry entry : entries) {

                writer.write(
                        escapeCsv(entry.key)
                                + ","
                                + escapeCsv(entry.timestamp)
                );

                writer.newLine();
            }
        }
    }

    private static String escapeCsv(String field) {

        if (field.contains(",")
                || field.contains("\"")
                || field.contains("\n")) {

            return "\""
                    + field.replace("\"", "\"\"")
                    + "\"";
        }

        return field;
    }
}