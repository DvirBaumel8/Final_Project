package Manager;

import java.nio.file.Files;
import java.nio.file.Paths;

public class ProjectValidator {
    public boolean validateProjectDirectoryPath(String projectDirectoryPath) {
        boolean isValid = true;

        if (!validateDirectoryContainPomFile(projectDirectoryPath)) {
            isValid = false;
        }
        if (!Files.exists(Paths.get(projectDirectoryPath))) {
            isValid = false;
        }
        return isValid;
    }

    private boolean validateDirectoryContainPomFile(String projectDirectoryPath) {
        return true;
    }
}
