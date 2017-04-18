package com.enigma;


import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;
import java.util.Random;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;




public class GenerateKey {

    public SecretKey getFileKey() {

        KeyGenerator keyGenerator;
        SecretKey fileKey = null;
        try {

            keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(256);
            fileKey = keyGenerator.generateKey();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();    
        }
        return fileKey;

    }


    public SecretKey generateKeyFromPassword(String password, byte salt[], int ITERATIONS) {
        SecretKey generatedKey = null;
        try {password = password.trim();
            SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            KeySpec keySpec = new PBEKeySpec(password.toCharArray(), salt, ITERATIONS, 256);
            SecretKey tmp = secretKeyFactory.generateSecret(keySpec);
            generatedKey = new SecretKeySpec(tmp.getEncoded(), "AES");
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
        }

        return generatedKey;
    }


    public byte[][] splitKey(SecretKey secretKey) {
        //String keyText = Base64.encode(key.getEncoded()).trim();
        byte keyBytes[] = secretKey.getEncoded();
        byte part1[] = Arrays.copyOfRange(keyBytes, 0, (keyBytes.length) / 2);
        byte part2[] = Arrays.copyOfRange(keyBytes, keyBytes.length / 2, keyBytes.length);

        byte keySplit[][] = new byte[][]{part1, part2};

        return keySplit;
    }


    public byte[] getSalt() {
        byte[] salt = new byte[64];
        Random random = new SecureRandom();
        random.nextBytes(salt);
        return salt;
    }

    public byte[] getAAD() {
        byte AAD[] = new byte[16];
        Random random = new SecureRandom();
        random.nextBytes(AAD);
        return AAD;
    }


    public byte[] getIV() {
        byte[] IV = new byte[16];
        Random random = new SecureRandom();
        random.nextBytes(IV);
        return IV;
    }

    public SecretKey getKeyFromString(String keyString) {
        //byte [] decodedKey= Base64.getDecoder().decode(keyString);
        byte decodedKey[] = Base64.decode(keyString, Base64.DEFAULT);
        SecretKey originalKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
        return originalKey;
    }

    public SecretKey getKeyFromByte(String keyByte,byte[] salt,int ITERATIONS) {
        SecretKey generatedKey=null;
        try {
            SecretKeyFactory secretKeyFactory=SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            KeySpec keySpec = new PBEKeySpec(keyByte.toCharArray(), salt, ITERATIONS, 256);
            SecretKey tmp = secretKeyFactory.generateSecret(keySpec);
            generatedKey = new SecretKeySpec(tmp.getEncoded(), "AES");


        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
        }

        return generatedKey;
    }

    public SecretKey getStoredKeyFromByte(byte[] fileKeyBytes) {

        SecretKey originalKey = new SecretKeySpec(fileKeyBytes, 0, fileKeyBytes.length, "AES");
        return originalKey;
    }
}
