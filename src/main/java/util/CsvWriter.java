package util;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

public class CsvWriter {

    private static final Logger logger = LoggerFactory.getLogger(CsvWriter.class);
    private int recordsWritten = 0;
    private final String filePrefix;
    private final String dataName;
    private final String filePath;
    private CSVPrinter printer;
    private final int recordsPerFile;

    public CsvWriter(String dataName, String filePath, String filePrefix, int recordsPerFile) {
        this.dataName = dataName;
        this.filePath = filePath;
        this.filePrefix = filePrefix;
        this.recordsPerFile = recordsPerFile;
        this.printer = buildPrinter();
    }

    private CSVPrinter buildPrinter() {
        try {
            String path = String.format("%s/%s-%d.csv", filePath, filePrefix, recordsWritten);
            BufferedWriter writer = Files.newBufferedWriter(Paths.get(path));

            return new CSVPrinter(writer, CSVFormat.DEFAULT);

        } catch (IOException e) {
            logger.error("CsvWriter failure: Could not build printer for files " + filePrefix, e);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


    public void write(Object... values) {
        try {
            printer.printRecord(values);
            recordsWritten++;
            printer.flush();

            if (recordsWritten % recordsPerFile == 0) {
                printer = buildPrinter();
            }

            if (recordsWritten % 1000 == 0) {
                logger.info("Data Type : {} | # Entries : {} | File Name : {}", dataName, recordsWritten, filePrefix);
            }

        } catch (IOException e) {
            logger.error("CsvWriter failure : could not write values " + Arrays.toString(values));
        }

    }

    public void flush() throws IOException {
        printer.flush();
    }

}
