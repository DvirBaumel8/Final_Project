public abstract class Encrypt implements Encryptors {
    private String method;
    private int strong;

    public Encrypt(String method, int strong) {
        this.method = method;
        this.strong = strong;
    }

    @Override
    public String toString() {
        return String.format("Name: %s, Encrypt method: %s", strong, method);
    }
}
