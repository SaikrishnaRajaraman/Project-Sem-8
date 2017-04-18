package com.enigma;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

/**
 * Created by krishna-pt1251 on 31/03/17.
 */

public class EncryptThreadClass extends Thread {

    private SecretKey mFileKey;
    private byte[] mFileContents;
    private byte[] mIV;
    private final static String AES_MODE = "AES/CBC/PKCS5Padding";
    private byte[] cipherFile;


    public EncryptThreadClass(SecretKey fileKey,byte[] IV,byte[] fileContents)
    {
           mFileContents=fileContents;
           mFileKey=fileKey;
           mIV=IV;
    }

    public void encrypt()
    {
        try {
            Cipher fileCipher = Cipher.getInstance(AES_MODE);
            IvParameterSpec ivParameterSpec = new IvParameterSpec(mIV);
            fileCipher.init(Cipher.ENCRYPT_MODE, mFileKey, ivParameterSpec);
            cipherFile = fileCipher.doFinal(mFileContents);
            }

        catch (NoSuchAlgorithmException | NoSuchPaddingException | BadPaddingException | InvalidKeyException | IllegalBlockSizeException | InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        encrypt();

    }

    public byte[] getEncryptedFile()
    {
        return cipherFile;
    }
}
