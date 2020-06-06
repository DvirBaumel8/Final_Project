import java.util.HashMap;
import java.util.Map;

public class Main {
    private final static int ENCRYPTS_SIZE = 3;

    public static void main(String[] args) {
        StringBuilder str = new StringBuilder();
        int[] testArray = new int[10];
        Encrypt[] encrypts = new Encrypt[ENCRYPTS_SIZE];

        EncryptFactory factory = createEncryptFactory();
        encrypts[0] = factory.createEncrypt(EncryptMethod.REVERSE);
        encrypts[1] = factory.createEncrypt(EncryptMethod.REVERSE_SKIP_FIRST);
        encrypts[2] = factory.createEncrypt(EncryptMethod.SWITCH_FIRST_LAST);

        for(int i =0; i < ENCRYPTS_SIZE; i++) {
            initTestArray(testArray);
            str.append(String.format("Encrypt_%d:\n Before: %s\n After: %s\n\n", i + 1, convertIntArrToString(testArray) , convertIntArrToString(encrypts[i].encrypt(testArray))));
        }

        System.out.println(str.toString());
    }

    private static EncryptFactory createEncryptFactory() {
        EncryptFactory factory = new EncryptFactory();
        Map<EncryptMethod, Encrypt> encrypts = new HashMap<>();
        encrypts.put(EncryptMethod.REVERSE, new ReverseEncrypt());
        encrypts.put(EncryptMethod.REVERSE_SKIP_FIRST, new ReverseSkipFirstEncrypt());
        encrypts.put(EncryptMethod.SWITCH_FIRST_LAST, new SwitchFirstLastEncrypt());
        factory.setEncryptsMap(encrypts);
        return factory;
    }

    private static String convertIntArrToString(int[] arr) {
        StringBuilder str = new StringBuilder();

        for(int i = 0; i < arr.length; i++) {
            if(i!= arr.length - 1) {
                str.append(String.format("%d ", arr[i]));
            }
            else {
                str.append(String.format("%d", arr[i]));
            }
        }
        return str.toString();
    }

    private static void initTestArray(int[] arr) {
        arr[0] = 5;
        arr[1] = 9;
        arr[2] = 7;
        arr[3] = 6;
        arr[4] = 3;
        arr[5] = 2;
        arr[6] = 1;
        arr[7] = 8;
        arr[8] = 9;
        arr[9] = 19;
    }
}
