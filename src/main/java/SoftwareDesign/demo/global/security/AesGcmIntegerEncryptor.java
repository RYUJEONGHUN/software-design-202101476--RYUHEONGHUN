package SoftwareDesign.demo.global.security;

import jakarta.persistence.AttributeConverter;

public class AesGcmIntegerEncryptor implements AttributeConverter<Integer, String> {

    private final AesGcmStringEncryptor stringEncryptor = new AesGcmStringEncryptor();

    @Override
    public String convertToDatabaseColumn(Integer attribute) {
        if (attribute == null) {
            return null;
        }
        return stringEncryptor.convertToDatabaseColumn(String.valueOf(attribute));
    }

    @Override
    public Integer convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isBlank()) {
            return null;
        }
        String decrypted = stringEncryptor.convertToEntityAttribute(dbData);
        return Integer.valueOf(decrypted);
    }
}
