package org.example.ibb_ecodation_javafx.security;

import org.mindrot.jbcrypt.BCrypt;
import javax.xml.bind.DatatypeConverter;  // HEX formatı için DatatypeConverter kullanıyoruz
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class BcryptEncoder {

    /**
     * Güvenli yoldan rastgele salt (tuz) oluşturma işlemidir.
     * Diğer normal yoldan sayı oluşturma işlemi kullanılmamalıdır. Veri açığına sebep olabilir.
     * @return Salt (tuz) byte dizisi
     * @throws NoSuchAlgorithmException Rastgele sayı üretme algoritması bulunamazsa hata fırlatılır.
     */
    public static byte[] generateSalt() throws NoSuchAlgorithmException {
        SecureRandom secureRandom = SecureRandom.getInstanceStrong();  // Güçlü bir rastgele sayı üretecidir
        byte[] salt = new byte[16];  // 16 Byte'lık salt (tuzlama anahtarı) oluşturuluyor.
        secureRandom.nextBytes(salt);  // Rastgele sayılar ile tuz dizisi oluşturuluyor
        return salt;
    }

    /**
     * Salt byte dizisini HEX formatına dönüştürür.
     * @param salt Salt byte dizisi
     * @return HEX formatında salt
     */
    public static String encodeSaltToHex(byte[] salt) {
        return DatatypeConverter.printHexBinary(salt);  // HEX formatına dönüştürülür
    }

    /**
     * BCrypt kullanarak şifreyi manuel olarak saltlama işlemidir.
     * BCrypt algoritması şifrenizi güvenli bir şekilde hash'ler.
     * Salt otomatik olarak üretilir.
     * @param password Kullanıcı şifresi
     * @return Hashlenmiş şifre
     */
    public static String hashPassword(String password) throws NoSuchAlgorithmException {
        // Güvenli bir rastgele salt (tuz) oluşturuluyor
        byte[] salt = generateSalt();

        // Salt byte dizisini HEX formatına dönüştür
        String saltHex = encodeSaltToHex(salt);

        // BCrypt, tuz (salt) için özel bir format bekler.
        // Cost faktörü ve salt ile kombine edilir.
        String saltString = "$2a$12$" + saltHex;  // $2a$12$ : BCrypt için standart versiyon ve cost factor

        // Şifreyi BCrypt ile hash'liyoruz
        return BCrypt.hashpw(password, saltString);  // Hashleme işlemi gerçekleştirilir
    }

    /**
     * Şifrenin doğru olup olmadığını kontrol etmek için BCrypt algoritmasını kullanır.
     * @param password Kullanıcı girişi şifresi
     * @param hashedPassword Hashlenmiş şifre
     * @return Şifre geçerli mi? (true/false)
     */
    public static boolean verifyPassword(String password, String hashedPassword) {
        return BCrypt.checkpw(password, hashedPassword);  // BCrypt ile şifre doğrulama
    }
}
