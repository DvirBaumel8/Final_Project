public class ReverseEncrypt extends Encrypt{
    public ReverseEncrypt() {
        super("Reverse", 1);
    }

    public int[] encrypt(int[] arr) {
    int indexStart = 0;
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

