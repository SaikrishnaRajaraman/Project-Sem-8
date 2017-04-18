package com.enigma;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;


public class Enigma {


    private final static String AES_MODE = "AES/CBC/PKCS5Padding";
    //private byte[][] cipherFile = new byte[10][];
    //private int encryptFileLength = 0;


    public void encryptFile(SecretKey fileKey, byte IV[], File uploadFile,File encryptedFilePath) {



        IvParameterSpec ivParameterSpec = new IvParameterSpec(IV);
        try {
            Cipher fileCipher = Cipher.getInstance(AES_MODE);
            fileCipher.init(Cipher.ENCRYPT_MODE, fileKey, ivParameterSpec);
            FileInputStream inputFile = new FileInputStream(uploadFile);
            BufferedInputStream bufferedInputStream =new BufferedInputStream(inputFile);

            FileOutputStream outputFile = new FileOutputStream(encryptedFilePath);

            CipherOutputStream cipherOutputStream = new CipherOutputStream(outputFile,fileCipher);

            int flag;
            byte buffer[]=new byte[500*1024];

            while((flag = bufferedInputStream.read(buffer)) != -1)
            {
                cipherOutputStream.write(buffer,0,flag);
            }


            cipherOutputStream.flush();
            cipherOutputStream.close();
            outputFile.flush();
            outputFile.close();
            inputFile.close();


        } catch (InvalidKeyException | InvalidAlgorithmParameterException | NoSuchAlgorithmException | NoSuchPaddingException | IOException e) {
            e.printStackTrace();
        }

        //byte fileParts[][] = splitFile(fileContents);

        /*EncryptThreadClass encryptThread[] = new EncryptThreadClass[10];

        for(int i=0;i<10 ;i++)
        {
            encryptThread[i]=new EncryptThreadClass(fileKey,IV,fileParts[i]);
            encryptThread[i].start();
        }

        for(int j=0;j<10;j++)
        {
            try {
                encryptThread[j].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        for(int k=0;k<10;k++)
        {

            cipherFile[k]=encryptThread[k].getEncryptedFile();
            encryptFileLength+=cipherFile[k].length;
        }

        byte encryptedFile[] = new byte[encryptFileLength];

        for(int j=0,k=0 ; j<10 ;j++,k+=cipherFile[j-1].length)
        {
            System.arraycopy(cipherFile[j],0,encryptedFile,k,cipherFile[j].length);
        }


    */



    }

    public byte[] encryptFileKey(SecretKey fileKey, SecretKey userGeneratedHalfKey, byte IV[]) {
        byte cipherFileKey[] = null;
        String base64FileKey = Base64.encodeToString(fileKey.getEncoded(), Base64.DEFAULT);
        try {
            Cipher fileKeyCipher = Cipher.getInstance(AES_MODE);
            IvParameterSpec ivParameterSpec = new IvParameterSpec(IV);
            fileKeyCipher.init(Cipher.ENCRYPT_MODE, userGeneratedHalfKey, ivParameterSpec);
            cipherFileKey = fileKeyCipher.doFinal(base64FileKey.getBytes());
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidAlgorithmParameterException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
        }

        return cipherFileKey;
    }


    public void decryptFile(SecretKey fileKey, byte[] IV, File encryptedFilePath,File decryptedFilePath) {


        try {
            Cipher fileCipher = Cipher.getInstance(AES_MODE);
            IvParameterSpec ivParameterSpec = new IvParameterSpec(IV);
            fileCipher.init(Cipher.DECRYPT_MODE, fileKey, ivParameterSpec);

            FileInputStream inputFile = new FileInputStream(encryptedFilePath);
            FileOutputStream outputFile = new FileOutputStream(decryptedFilePath);

            CipherInputStream cipherInputStream = new CipherInputStream(inputFile,fileCipher);

            int flag;
            byte buffer[]=new byte[500*1024];

            while((flag = cipherInputStream.read(buffer))!=-1)
            {
                outputFile.write(buffer,0,flag);
            }

            outputFile.flush();
            outputFile.close();
            inputFile.close();
            cipherInputStream.close();

        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidAlgorithmParameterException | InvalidKeyException | IOException e) {
            e.printStackTrace();
        }

    }


    public byte[] decryptFileKey(SecretKey userGeneratedHalfKey, byte IV[], byte encryptedFileKey[]) {
        byte decryptedFileKey[];
        byte fileKey[] = null;

        try {
            Cipher fileKeyCipher = Cipher.getInstance(AES_MODE);
            IvParameterSpec ivParameterSpec = new IvParameterSpec(IV);
            fileKeyCipher.init(Cipher.DECRYPT_MODE, userGeneratedHalfKey, ivParameterSpec);
            decryptedFileKey = fileKeyCipher.doFinal(encryptedFileKey);
            fileKey = Base64.decode(decryptedFileKey, Base64.DEFAULT);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidAlgorithmParameterException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
        }

        return fileKey;

    }

    private byte[][] chunkArray(byte[] array, int chunkSize) {
        int numOfChunks = (int)Math.ceil((double)array.length / chunkSize);
        byte[][] output = new byte[numOfChunks][];

        for(int i = 0; i < numOfChunks; ++i) {
            int start = i * chunkSize;
            int length = Math.min(array.length - start, chunkSize);

            byte[] temp = new byte[length];
            System.arraycopy(array, start, temp, 0, length);
            output[i] = temp;
        }

        return output;
    }

    public byte[][] splitFile(byte fileContents[])
    {
        int CHUNK_SIZE = fileContents.length/10;
        int SIZE_DIFF = fileContents.length - CHUNK_SIZE * 10 ;
        byte[][] fileParts = new byte[10][];
        int start_index = 0;
        int end_index = CHUNK_SIZE;

        for(int i=0;i<10;i++,start_index+=CHUNK_SIZE,end_index+=CHUNK_SIZE)
        {
            fileParts[i] = Arrays.copyOfRange(fileContents,start_index,end_index);
        }

      byte finalPart[] = new byte[fileParts[8].length+SIZE_DIFF];

        System.arraycopy(fileParts[9],0,finalPart,0,fileParts[9].length);
        System.arraycopy(fileContents,start_index,finalPart,fileParts[9].length,SIZE_DIFF);
        fileParts[9] = finalPart;

        return fileParts;
    }
}


