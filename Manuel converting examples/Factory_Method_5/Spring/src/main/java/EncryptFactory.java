import java.util.Map;

public class EncryptFactory {
    private Map<EncryptMethod, Encrypt> encryptMap;

    public Encrypt createEncrypt(EncryptMethod parsConst) {
        Encrypt parser = encryptMap.get(parsConst);
        if (encryptMap.get(parsConst) != null) {
            return parser;
        }
        throw new IllegalArgumentException("Unknown Parser");
    }

    public void setEncryptsMap(Map<EncryptMethod, Encrypt> parserMap) {
        this.encryptMap = parserMap;
    }
}
