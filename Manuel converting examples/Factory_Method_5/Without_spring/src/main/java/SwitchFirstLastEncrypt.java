public class SwitchFirstLastEncrypt extends Encrypt{
    public SwitchFirstLastEncrypt() {
        super("Switch First and Last digits", 2);
    }

    public int[] encrypt(int[] arr) {
        int temp = arr[0];
        arr[0] = arr[arr.length - 1];
        arr[arr.length - 1] = temp;
        return arr;
    }
}
