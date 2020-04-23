package FilesUtil;

import java.io.File;

public class FilesUtil {

    public File openFile(String filePath) {
        try {
            return new File(filePath);
        }
        catch(Exception e) {

        }
        return null;
    }
}
