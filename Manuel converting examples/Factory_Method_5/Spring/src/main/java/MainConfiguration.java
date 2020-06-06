import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class MainConfiguration {
    @Bean
    public EncryptFactory encryptCreatorFactory() {
        EncryptFactory factory = new EncryptFactory();
        Map<EncryptMethod, Encrypt> encrypts = new HashMap<>();
        encrypts.put(EncryptMethod.REVERSE, new ReverseEncrypt());
        encrypts.put(EncryptMethod.REVERSE_SKIP_FIRST, new ReverseSkipFirstEncrypt());
        encrypts.put(EncryptMethod.SWITCH_FIRST_LAST, new SwitchFirstLastEncrypt());
        factory.setEncryptsMap(encrypts);
        return factory;
    }

    @Bean
    public Encrypt reverseEncrypt() {
        return encryptCreatorFactory().createEncrypt(EncryptMethod.REVERSE);
    }

    @Bean
    public Encrypt reverseSkipFirstEncrypt() {
        return encryptCreatorFactory().createEncrypt(EncryptMethod.REVERSE_SKIP_FIRST);
    }

    @Bean
    public Encrypt switchFirstLastEncrypt() {
        return encryptCreatorFactory().createEncrypt(EncryptMethod.SWITCH_FIRST_LAST);
    }
}
