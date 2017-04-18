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

public class DecryptThreadClass extends Thread {

    private SecretKey mFileKey;
    private byte[] mEncryptedFileContents;
    private byte[] mIV;
    private final static String AES_MODE = "AES/CBC/PKCS5Padding";
    private byte[] mPlainTextFile;


    public DecryptThreadClass(SecretKey fileKey,byte IV[],byte encryptedFile[])
    {
        mFileKey=fileKey;
        mEncryptedFileContents=encryptedFile;
        mIV=IV;

    }

    @Override
    public void run() {
        decrypt();
    }

    private void decrypt() {
        try {
            Cipher fileCipher = Cipher.getInstance(AES_MODE);
            IvParameterSpec ivParameterSpec = new IvParameterSpec(mIV);
            fileCipher.init(Cipher.DECRYPT_MODE, mFileKey, ivParameterSpec);
            mPlainTextFile = fileCipher.doFinal(mEncryptedFileContents);
        }

        catch (NoSuchAlgorithmException | NoSuchPaddingException | BadPaddingException | InvalidKeyException | IllegalBlockSizeException | InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
    }
}
