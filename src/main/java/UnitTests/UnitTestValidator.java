package UnitTests;

import FilesUtil.FilesUtil;
import org.junit.runner.JUnitCore;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class UnitTestValidator {
    private File testDir;
    private List<File> testClasses;

    public UnitTestValidator() {
        testClasses = new ArrayList<>();
    }

    public boolean runUnitTests(File[] projectFiles) {
        this.testDir = findTestDir(projectFiles);
        findTestClasses(testDir);
        JUnitCore junit = new JUnitCore();
        for(File file : testClasses) {
            //junit.addListener();
        }

        return true;
    }

    private void findTestClasses(File file) {
        File[] files = testDir.listFiles();
        File currFile = file;

        for(int i = 0; i < files.length; i++) {
            currFile = files[i];
            if(currFile.isDirectory()) {
                findTestClasses(currFile);
            }
            else {
                if(checkIfTestClass(currFile)) {
                    testClasses.add(currFile);
                }
            }
        }
    }


    private boolean checkIfTestClass(File file) {
        if(!file.getName().endsWith("Test")) {
            return false;
        }
       return true;
    }

    private File findTestDir(File[] projectFiles) {
        try {
            File srcDir = FilesUtil.getSrcDir(projectFiles);
            return FilesUtil.getTestDir(srcDir);
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }
    }


