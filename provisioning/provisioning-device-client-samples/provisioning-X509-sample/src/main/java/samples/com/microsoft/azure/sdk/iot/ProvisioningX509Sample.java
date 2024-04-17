// Copyright (c) Microsoft. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

package samples.com.microsoft.azure.sdk.iot;

import com.microsoft.azure.sdk.iot.device.DeviceClient;
import com.microsoft.azure.sdk.iot.device.IotHubClientProtocol;
import com.microsoft.azure.sdk.iot.device.Message;
import com.microsoft.azure.sdk.iot.provisioning.device.ProvisioningDeviceClient;
import com.microsoft.azure.sdk.iot.provisioning.device.ProvisioningDeviceClientRegistrationResult;
import com.microsoft.azure.sdk.iot.provisioning.device.ProvisioningDeviceClientStatus;
import com.microsoft.azure.sdk.iot.provisioning.device.ProvisioningDeviceClientTransportProtocol;
import com.microsoft.azure.sdk.iot.provisioning.security.SecurityProvider;
import com.microsoft.azure.sdk.iot.provisioning.security.hsm.SecurityProviderX509Cert;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.Security;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Scanner;

/**
 * Provisioning Sample for X509
 * 官方推荐使用 ProvisioningX509CertGen 来生成证书。
 * 我这里使用的是 azure-iot-sdk-c中的powershell 脚本来执行证书创建。路径是：azure-iot-sdk-c\tools\CACertificates ， 中间有说明文档
 * 通过openssl 查看证书信息：
 * openssl x509 -in ca-sign-device-0001-public.pem  -text
 * openssl x509 -in ca-sign-device-0001.pfx  -text
 * <p>
 * 证书中已经包含了要注册的deviceId
 *
 * 查看 @link samples.com.microsoft.azure.sdk.iot.SendEventX509  发送消息
 *
 */
@SuppressWarnings("CommentedOutCode") // Ignored in samples as we use these comments to show other options.
public class ProvisioningX509Sample
{
    private static final String ID_SCOPE = "0ne00C87B3E";
    private static final String GLOBAL_ENDPOINT = "global.azure-devices-provisioning.net";
    private static final ProvisioningDeviceClientTransportProtocol PROVISIONING_DEVICE_CLIENT_TRANSPORT_PROTOCOL = ProvisioningDeviceClientTransportProtocol.HTTPS;
    //private static final ProvisioningDeviceClientTransportProtocol PROVISIONING_DEVICE_CLIENT_TRANSPORT_PROTOCOL = ProvisioningDeviceClientTransportProtocol.AMQPS;
    //private static final ProvisioningDeviceClientTransportProtocol PROVISIONING_DEVICE_CLIENT_TRANSPORT_PROTOCOL = ProvisioningDeviceClientTransportProtocol.AMQPS_WS;
    //private static final ProvisioningDeviceClientTransportProtocol PROVISIONING_DEVICE_CLIENT_TRANSPORT_PROTOCOL = ProvisioningDeviceClientTransportProtocol.MQTT;
    //private static final ProvisioningDeviceClientTransportProtocol PROVISIONING_DEVICE_CLIENT_TRANSPORT_PROTOCOL = ProvisioningDeviceClientTransportProtocol.MQTT_WS;
    private static final String leafPublicPem = "-----BEGIN CERTIFICATE-----\n" +
            "MIIDdDCCAlygAwIBAgIQXWXPOGW0/q5Egx9kLGX+xzANBgkqhkiG9w0BAQsFADAo\n" +
            "MSYwJAYDVQQDDB1BenVyZSBJb1QgQ0EgVGVzdE9ubHkgUm9vdCBDQTAeFw0yNDA0\n" +
            "MTcwMDU4NDJaFw0yNDA1MTcwMTA4NDJaMB4xHDAaBgNVBAMME2NhLXNpZ24tZGV2\n" +
            "aWNlLTAwMDIwggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQDf8rsL6hcn\n" +
            "f9G8xrs1T4ZqOqG8DU2dkHXpCRPmfbeQJN+7TtO5slf++peoad/vo/EFmBWxdCEB\n" +
            "k2D+O8FUY9JTqrWbqMRt04v9jgNNA1QSmx5yvIVwquAfRq9jAC/i1rxm426/BmQD\n" +
            "eB1YDfVfkpXMDeGVEudtJ7YmZb5DS9eXnkdxW5hdCX+ioB3AJlyFJt/5h39I050A\n" +
            "P0GXg+7ew3UDFr0pw3FOSqB+RLTtLQsKbxtPMsQdIs+VidTCzMIdJJNXyZzirYK/\n" +
            "4WBmt7fqXTNMby0VSpqrIEI1+kpLmHzXIkNCkXKuh9ZKuQoOOsLYqKLqqVspCOjf\n" +
            "lRpqGCNPqjL9AgMBAAGjgaMwgaAwDgYDVR0PAQH/BAQDAgWgMB4GA1UdEQQXMBWC\n" +
            "E2NhLXNpZ24tZGV2aWNlLTAwMDIwHQYDVR0lBBYwFAYIKwYBBQUHAwIGCCsGAQUF\n" +
            "BwMBMA8GA1UdEwEB/wQFMAMCAQAwHwYDVR0jBBgwFoAUzYJZHnnpA30EVDhx0hR5\n" +
            "0y1X/+EwHQYDVR0OBBYEFNbqPyfp4T0dzMgyHbWpQq/SQSqYMA0GCSqGSIb3DQEB\n" +
            "CwUAA4IBAQBIGa+ugs7HXT5ln0qkKCPmyccRhTAzDpfAKNJqHw/BK/WFwEe0nF3x\n" +
            "qtfo6yDXEzStv1dxwv58PW4soT++mBzSiEh1hUi2axlE18EaTdDKmKxpALdS7sMA\n" +
            "THQRdQbfyqjmP97du4PwG7UcBnJeBd/ELgaXyAtSytKrFl3sFQSRo2CK/m1L5VHm\n" +
            "UNYx2tiHBY0hqBfa0gQtoompEQWvVfT1d8kbINd7Fvzo5vpLbXDRzc7tw4G4qhIP\n" +
            "NWhFb2rp4yd9zY5ZhboqazCBnNppL2mwh09St4hDmG9oHN0wS+d2AMiuSjBYg7M/\n" +
            "EYrwqidIBzuhouQuKTOjGQLIcuZfh7Sr\n" +
            "-----END CERTIFICATE-----\n";
    private static final String leafPrivateKeyPem = "-----BEGIN PRIVATE KEY-----\n" +
            "MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQDf8rsL6hcnf9G8\n" +
            "xrs1T4ZqOqG8DU2dkHXpCRPmfbeQJN+7TtO5slf++peoad/vo/EFmBWxdCEBk2D+\n" +
            "O8FUY9JTqrWbqMRt04v9jgNNA1QSmx5yvIVwquAfRq9jAC/i1rxm426/BmQDeB1Y\n" +
            "DfVfkpXMDeGVEudtJ7YmZb5DS9eXnkdxW5hdCX+ioB3AJlyFJt/5h39I050AP0GX\n" +
            "g+7ew3UDFr0pw3FOSqB+RLTtLQsKbxtPMsQdIs+VidTCzMIdJJNXyZzirYK/4WBm\n" +
            "t7fqXTNMby0VSpqrIEI1+kpLmHzXIkNCkXKuh9ZKuQoOOsLYqKLqqVspCOjflRpq\n" +
            "GCNPqjL9AgMBAAECggEAd0SXl03RjQjG08nnNAopZPPa5QMUvgCMu1JJVezIUS1C\n" +
            "NkhR/EjeEn61PE0+pSrjlv4bN4nIdkVeV6fNW7tZ7ZMx6zejfXY3zQ7P9Tj7knko\n" +
            "ayS50HpAqIeFqq0T07RXXFhtx7CInUxgHGA6uO6hdq5B4JnTxM9sc+ClxsNii20R\n" +
            "9hiaizUoeNYPxjoZj+778AjEvtfyju0Z5HwDulLDFpvhtts090jq+cQ5nmzQKlAj\n" +
            "6p0ytTxmkGWjmYzjCvbzP+6+HdwoHFgZK4244BF8HuzaBKlg3dr06S4YL3d+rYJA\n" +
            "d/WvrUln7HGv8AUlb1kocVLF2ObYn15zhromcbZ+5QKBgQDjx6h7L3nFfeTujwTH\n" +
            "v30gr0bV0jYwYS/8mfu1x+9AMyck4qOAFTrl+WEkI3SimH5jpA5OfsxnD1rhGa8a\n" +
            "i1Yaik766QLNrkD6LjxhfwIwf7nVX7Bpl838n8h74pRfAW7S6EPRi/3MH9grj5dj\n" +
            "yn6ltpiNdHg69tjKSgRIXpsg8wKBgQD7sYsmesot0zXbzXZIZYRXdgy6KOZOLwEB\n" +
            "R76SKIJTtdMtnMsrjSz2YvNAYZSRFdKZj84zuJ3dEpiC9n/w/Xirhbl9b+f3WRuw\n" +
            "vEr8EriuDXGhP/HjRohurJgsPOD0DomQ1DBO7cQdF4Zz2f9b0xCPvtPdxyGdYfYn\n" +
            "jmZcbYrYTwKBgFuYhEXVF5C1SYQs+u0gMb8c/M0rFSNrUZKwkSnOVoVojIsmoDz3\n" +
            "TJICMHAJ2fMwg1KqPB6Qmr2uzQrL+0AfW+acS5pWbQws0HBKe3lxS34ZPq9xJU2w\n" +
            "/+JgloxK1wNFXj1trSfstYiKHbGWsngsi7UzsjDf7yE29oKSNRqtAJDFAoGAdrxy\n" +
            "HawNlwKtxMyvwUWK4kvBg0zqIPYRrk3vPDo6CU2cm0b9ncUS8gUKJlQiZzN5T5JE\n" +
            "v6eXaYRtSFMLVl/tPlVuhRt2vfxekMizQyl90DZtZZmp/gL3N+baPvxVTy1Qfm9r\n" +
            "fsCyJNtFRYAQ9Huks3tdraFUXU+qdUy7Q102BAcCgYA9VJKc/lNaO8SacO0X8spP\n" +
            "83SO5P89G+tEg/u3SgzeduFRE6AHRJjrMMiVqjf585sYPu7K4EG+xHwrMgk+4NAQ\n" +
            "/PyDCqfup19Pis7GJWsaPd6sfzMLovb0yzMzqGGh8sIRMe/hiRdBht1QHnScllBq\n" +
            "A+hBSb1J4IHJcyIdhBxrhA==\n" +
            "-----END PRIVATE KEY-----\n";

    private static final Collection<String> signerCertificatePemList = new LinkedList<>();

    public static void main(String[] args) throws Exception
    {
        System.out.println("Starting...");
        System.out.println("Beginning setup.");

        // For group enrollment uncomment this line
        //signerCertificatePemList.add("<Your Signer/intermediate Certificate Here>");

        X509Certificate leafPublicCert = parsePublicKeyCertificate(leafPublicPem);
        Key leafPrivateKey = parsePrivateKey(leafPrivateKeyPem);
        Collection<X509Certificate> signerCertificates = new LinkedList<>();
        for (String signerCertificatePem : signerCertificatePemList)
        {
            signerCertificates.add(parsePublicKeyCertificate(signerCertificatePem));
        }

        SecurityProvider securityProviderX509 = new SecurityProviderX509Cert(leafPublicCert, leafPrivateKey, signerCertificates);
        ProvisioningDeviceClient provisioningDeviceClient = ProvisioningDeviceClient.create(
                GLOBAL_ENDPOINT,
                ID_SCOPE,
                PROVISIONING_DEVICE_CLIENT_TRANSPORT_PROTOCOL,
                securityProviderX509);

        ProvisioningDeviceClientRegistrationResult provisioningDeviceClientRegistrationResult = provisioningDeviceClient.registerDeviceSync();
        provisioningDeviceClient.close();

        if (provisioningDeviceClientRegistrationResult.getProvisioningDeviceClientStatus() == ProvisioningDeviceClientStatus.PROVISIONING_DEVICE_STATUS_ASSIGNED)
        {
            System.out.println("IotHub Uri : " + provisioningDeviceClientRegistrationResult.getIothubUri());
            System.out.println("Device ID : " + provisioningDeviceClientRegistrationResult.getDeviceId());

            // connect to iothub
            String iotHubUri = provisioningDeviceClientRegistrationResult.getIothubUri();
            String deviceId = provisioningDeviceClientRegistrationResult.getDeviceId();
            DeviceClient deviceClient = new DeviceClient(iotHubUri, deviceId, securityProviderX509, IotHubClientProtocol.MQTT);
            deviceClient.open(false);

            System.out.println("Sending message from device to IoT Hub...");
            deviceClient.sendEvent(new Message("Hello world!"));
            deviceClient.close();
        }

        System.out.println("Press any key to exit...");
        new Scanner(System.in, StandardCharsets.UTF_8.name()).nextLine();
    }

    private static Key parsePrivateKey(String privateKeyString) throws IOException
    {
        Security.addProvider(new BouncyCastleProvider());
        PEMParser privateKeyParser = new PEMParser(new StringReader(privateKeyString));
        Object possiblePrivateKey = privateKeyParser.readObject();
        return getPrivateKey(possiblePrivateKey);
    }

    private static X509Certificate parsePublicKeyCertificate(String publicKeyCertificateString) throws IOException, CertificateException
    {
        Security.addProvider(new BouncyCastleProvider());
        PemReader publicKeyCertificateReader = new PemReader(new StringReader(publicKeyCertificateString));
        PemObject possiblePublicKeyCertificate = publicKeyCertificateReader.readPemObject();
        CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
        return (X509Certificate) certFactory.generateCertificate(new ByteArrayInputStream(possiblePublicKeyCertificate.getContent()));
    }

    private static Key getPrivateKey(Object possiblePrivateKey) throws IOException
    {
        if (possiblePrivateKey instanceof PEMKeyPair)
        {
            return new JcaPEMKeyConverter().getKeyPair((PEMKeyPair) possiblePrivateKey)
                    .getPrivate();
        }
        else if (possiblePrivateKey instanceof PrivateKeyInfo)
        {
            return new JcaPEMKeyConverter().getPrivateKey((PrivateKeyInfo) possiblePrivateKey);
        }
        else
        {
            throw new IOException("Unable to parse private key, type unknown");
        }
    }
}
