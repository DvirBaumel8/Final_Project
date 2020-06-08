package FilesUtil;

import com.google.googlejavaformat.java.Formatter;
import com.google.googlejavaformat.java.FormatterException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;


public class JavaFormatter {
    public void FormatFile(String path) throws IOException, FormatterException {
        String content = new String(Files.readAllBytes(Paths.get(path)));
        String formattedSource = new Formatter().formatSource(content);
        Files.write(Paths.get(path), "".getBytes());
        Files.write(Paths.get(path), formattedSource.getBytes());
    }

}