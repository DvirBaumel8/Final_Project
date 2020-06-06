public class ReverseSkipFirstEncrypt extends Encrypt{
    public ReverseSkipFirstEncrypt() {
        super("Reverse and skip first element", 3);
    }

    public int[] encrypt(int[] arr) {
        int indexStart = 1;
        int indexEnd = arr.length - 1;

        while (indexStart < indexEnd)
        {
            int temp = arr[indexStart];
            arr[indexStart] = arr[indexEnd];
            arr[indexEnd] = temp;
            indexStart++;
            indexEnd--;
        }

        return arr;
    }
}
