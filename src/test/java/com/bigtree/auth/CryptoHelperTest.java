package com.bigtree.auth;

import com.bigtree.auth.security.CryptoHelper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Disabled
public class CryptoHelperTest {

    String secretKey = "1234567891234567";
    String salt = "MySalt";

    // String to be encrypted
    String originalString = "Hello, this is a secret message.";
    private String encryptedText = "O28lddE0v0wFmWBt4ludSN+RMYf8dUA9yTafIH95GHHaA0veWmE+UTjYHx5kdQlENQv6Q4F35e/WiGUaxgAXGNnBeZiQ0Tjg7PiUNY6ozDxkM9k66jgBHMvDNI5A/K5yYJzFmQuiPm26Nt3ehwnifnTZnhELZKRIeT2/IzXRDae5cU3L9xlIpXjgYD7qSxDCcfRko0ZqPV6l/4a75G5BJg==";

    // Encrypt the string
    @Test
    public void testEncryption(){
        CryptoHelper helper = new CryptoHelper();
        String encryptedString = helper.encryptAndEncode(originalString);
        Assertions.assertNotNull(encryptedString);
    }

    @Test
    public void testDecryption() throws Exception {
        CryptoHelper helper = new CryptoHelper();
        String encryptedString = helper.decodeAndDecrypt(encryptedText);
        Assertions.assertNotNull(encryptedString);
    }
}
