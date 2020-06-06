import java.util.Map;

public class EncryptFactory {
    private Map<EncryptMethod, Encrypt> parserMap;

    public Encrypt createEncrypt(EncryptMethod parsConst) {
        Encrypt parser = parserMap.get(parsConst);
        if (parserMap.get(parsConst) != null) {
            return parser;
        }
        throw new IllegalArgumentException("Unknown Parser");
    }

    public void setEncryptsMap(Map<EncryptMethod, Encrypt> parserMap) {
        this.parserMap = parserMap;
    }
}
