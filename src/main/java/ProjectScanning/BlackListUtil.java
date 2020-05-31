package ProjectScanning;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class BlackListUtil {
    private File blackListFile;
    private String resourcesDirPath;
    private static List<String> blackListInstances;

    public BlackListUtil(String resourcesDirPath) {
        this.resourcesDirPath = resourcesDirPath;
        blackListFile = new File(resourcesDirPath);
    }

    public static List<String> getBlackListInstances() {
        if(blackListInstances == null) {
            return null;
        }
        else {
            return blackListInstances;
        }
    }

    public void parseBlackListFile() {
        List<String> blacklistFileLines = null;
        try {
            blacklistFileLines= Files.readAllLines(blackListFile.toPath(), StandardCharsets.UTF_8);
        }
        catch(Exception e) {
            //
        }

        for(int i = 1; i < blacklistFileLines.size(); i++) {
            blackListInstances.add(blacklistFileLines.get(i));
        }
    }

    public boolean isBlackListFileExist() {
        File[] files = blackListFile.listFiles();
        File currFile;

        for(int i = 0; i < files.length; i++) {
            currFile = files[i];
            if(currFile.isDirectory()) {
                return isBlackListFileExist(currFile);
            }
            else {
                if(currFile.getName().equals("BlackList")) {
                    blackListFile = currFile;
                    return true;
                }
            }
        }

        return false;
    }

    public boolean isBlackListFileExist(File rootDir) {
        File[] files = blackListFile.listFiles();
        File currFile;

        for(int i = 0; i < files.length; i++) {
            currFile = files[i];
            if(currFile.isDirectory()) {
                isBlackListFileExist(currFile);
            }
            else {
                if(currFile.getName().equals("BlackList")) {
                    blackListFile = currFile;
                    return true;
                }
            }
        }
        return false;
    }

    public boolean needEnforceBlackList() {
        List<String> blacklistFileLines = null;
        try {
            blacklistFileLines= Files.readAllLines(blackListFile.toPath(), StandardCharsets.UTF_8);
        }
        catch(Exception e) {
            //
        }
        String firstLine = blacklistFileLines.get(0);
        if(firstLine.equals("BlackList=true")) {
            blackListInstances = new ArrayList<>();
            return true;
        }
        else {
            return false;
        }
    }
}
