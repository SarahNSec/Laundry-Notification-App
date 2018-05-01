package com.lab.home.laundrynotification;

import android.content.Intent;
import android.security.KeyPairGeneratorSpec;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Calendar;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import javax.security.auth.x500.X500Principal;

public class APIActivity extends AppCompatActivity {
    private KeyStore keyStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_api);



        // add on-click listener for save key button
        findViewById(R.id.saveAPIKey_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get Description field
                EditText descField = (EditText)findViewById(R.id.apiDesc_input);
                String desc = descField.getText().toString();

                EditText keyField = (EditText)findViewById(R.id.APIKeyEntry);
                String key = keyField.getText().toString();

                try {
                    KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
                    keyStore.load(null);

                    if (!keyStore.containsAlias(desc)) {
                        // Key does not exist, so create it
                        Calendar notBefore = Calendar.getInstance();
                        Calendar notAfter = Calendar.getInstance();
                        notAfter.add(Calendar.YEAR, 1); // Key is valid for 1 year
                        KeyPairGeneratorSpec spec = new KeyPairGeneratorSpec.Builder(getApplicationContext()).setAlias(desc).setKeyType("RSA")
                                .setKeySize(2048)
                                .setSubject(new X500Principal("CN=test"))
                                .setSerialNumber(BigInteger.ONE)
                                .setStartDate(notBefore.getTime())
                                .setEndDate(notAfter.getTime())
                                .build();
                        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA", "AndroidKeyStore");
                        generator.initialize(spec);

                        KeyPair keyPair = generator.generateKeyPair();
                    }
                    // Retrieve the keys
                    KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry)keyStore.getEntry(desc, null);
                    RSAPrivateKey privateKey = (RSAPrivateKey) privateKeyEntry.getPrivateKey();
                    RSAPublicKey publicKey = (RSAPublicKey) privateKeyEntry.getCertificate().getPublicKey();

                    // Encrypt the text
                    String dataDirectory = getApplicationInfo().dataDir;
                    String filesDirectory = getFilesDir().getAbsolutePath();
                    String encryptedDataFilePath = filesDirectory + File.separator + "secrets";

                    Cipher inCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding", "AndroidOpenSSL");
                    inCipher.init(Cipher.ENCRYPT_MODE, publicKey);

                    Cipher outCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding", "AndroidOpenSSL");
                    outCipher.init(Cipher.DECRYPT_MODE, privateKey);

                    CipherOutputStream cipherOutputStream =
                            new CipherOutputStream(
                                    new FileOutputStream(encryptedDataFilePath), inCipher);
                    cipherOutputStream.write(key.getBytes("UTF-8"));
                    cipherOutputStream.close();
                } catch (Exception e) {

                }
            }
        });
    }
}
