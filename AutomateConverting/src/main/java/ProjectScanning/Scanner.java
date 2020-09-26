package ProjectScanning;

import Manager.Manager;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Scanner {
    private Map<String, InnerClassDetails> classNameToClassDetails;
    private Map<String, String> packagesToPath;
    private Map<String, BeanDetails> instanceNameToBeanDetails;
    private Set<String> classesToAddAppCox;
    private ScannerUtils scannerUtils;
    private ClassScanner classScanner;
    private ConfigurationService configurationService;
    private BeanMethodService beanMethodService;

    private static String OS = System.getProperty("os.name").toLowerCase();
    private static boolean isWindows() {
        return (OS.indexOf("win") >= 0);
    }
    private static boolean isMac() {
        return (OS.indexOf("mac") >= 0);
    }

    public Scanner() {
        classNameToClassDetails = new HashMap<>();
        packagesToPath = new HashMap<>();
        instanceNameToBeanDetails = new HashMap<>();
        classesToAddAppCox = new HashSet<>();
        scannerUtils = new ScannerUtils();
        classScanner = new ClassScanner();
        configurationService = new ConfigurationService();
        beanMethodService = new BeanMethodService();
    }

    public void scan(String javaDir) throws IOException {
        findClassesAndDirectories(javaDir, null);
        handlePackagesConfigurationFiles();
        findNewProjectInstancesCreation();
        beanMethodService.createBeanMethods(classNameToClassDetails, instanceNameToBeanDetails);
        scannerUtils.handleAppCtxWriteToNeededClasses(classNameToClassDetails, classesToAddAppCox);
    }

    private void handlePackagesConfigurationFiles() throws IOException {
        for(Map.Entry<String, String> entry: packagesToPath.entrySet()) {
            String confFileName = scannerUtils.replaceFirstLetterToUpperCase(entry.getKey());
            String createPath = String.format("%s/%s/%sConfiguration.java", Manager.getInstance().getJavaDirectory().getPath(), entry.getKey(), confFileName);
            File packageConfiguration = new File(createPath);
            InnerClassDetails innerClassDetails = new InnerClassDetails(createPath, entry.getKey());
            classNameToClassDetails.put(String.format("%sConfiguration", confFileName), innerClassDetails);
            beanMethodService.addNewConfFile(String.format("%sConfiguration.java", confFileName));
            scannerUtils.populatePackageConfigurationFile(packageConfiguration, entry.getKey());
            scannerUtils.addImportConfigurationToMainConf(String.format("%sConfiguration", confFileName), entry.getKey());
        }
    }

    private void findNewProjectInstancesCreation() throws IOException {
        for (Map.Entry<String, InnerClassDetails> entry : classNameToClassDetails.entrySet()) {
            if (entry.getKey().equals("MainConfiguration")) {
                continue;
            }
            classScanner.handleInternalClassScan(entry.getKey(), entry.getValue().getPath(), classNameToClassDetails, instanceNameToBeanDetails, classesToAddAppCox);
            configurationService.handleConfigurationFieldsImportToClass(entry.getKey(), entry.getValue().getPath());
        }
    }

    private void findClassesAndDirectories(String javaDir, String underPackage) throws IOException {
        File file = new File(javaDir);
        File[] files = file.listFiles();
        File currFile;

        for(int i = 0; i < files.length; i++) {
            currFile = files[i];
            if(currFile.isDirectory()) {
                packagesToPath.put(currFile.getName(), currFile.getPath());
                findClassesAndDirectories(currFile.getPath(), currFile.getName());
            }
            else {
                if(classScanner.scanClass(currFile)) {
                    InnerClassDetails innerClassDetails = new InnerClassDetails(currFile.getPath(), underPackage);
                    classNameToClassDetails.put(scannerUtils.getDisplayClassName(currFile.getName()), innerClassDetails);
                }
            }
        }
    }

}
