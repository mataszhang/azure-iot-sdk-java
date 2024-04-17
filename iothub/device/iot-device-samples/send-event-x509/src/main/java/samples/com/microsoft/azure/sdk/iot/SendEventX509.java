// Copyright (c) Microsoft. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

package samples.com.microsoft.azure.sdk.iot;

import com.microsoft.azure.sdk.iot.device.*;
import com.microsoft.azure.sdk.iot.device.exceptions.IotHubClientException;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

/**
 *  查看 ProvisioningX509Sample ： 在DPS中使用X509自签名证书来实现设备注册
 *  连接字符串通过 Azure IoT Explorer Preview 查看 ， 例如： HostName=east-asia-iot-hub.azure-devices.net;DeviceId=ca-sign-device-0002;x509=true
 *
 *
 */

/** Sends a number of event messages to an IoT Hub. */
public class SendEventX509
{
    //PEM encoded representation of the public key certificate
    private static final String publicKeyCertificateString =
            "-----BEGIN CERTIFICATE-----\n" +
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

    //PEM encoded representation of the private key
    private static final String privateKeyString =
            "-----BEGIN PRIVATE KEY-----\n" +
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

    // The maximum amount of time to wait for a message to be sent. Typically, this operation finishes in under a second.
    private static final int D2C_MESSAGE_TIMEOUT_MILLISECONDS = 10000;

    /**
     * Sends a number of messages to an IoT Hub. Default protocol is to 
     * use MQTT transport.
     *
     * @param args
     * args[0] = IoT Hub connection string
     * args[1] = number of requests to send
     * args[2] = IoT Hub protocol to use, optional and defaults to MQTT
     */
    public static void main(String[] args) throws IOException, URISyntaxException, GeneralSecurityException, IotHubClientException, InterruptedException
    {
        System.out.println("Starting...");
        System.out.println("Beginning setup.");
 
        if (!(args.length == 2 || args.length == 3))
        {
            System.out.format(
                    "Expected 2 or 3 arguments but received: %d.\n"
                            + "The program should be called with the following args: \n"
                            + "1. [Device connection string] - String containing Hostname, Device Id & Device Key in one of the following formats: HostName=<host_name>;DeviceId=<device_id>;x509=true\n"
                            + "2. [number of requests to send]\n"
                            + "3. (mqtt | https | amqps | amqps_ws | mqtt_ws)\n",
                    args.length);
            return;
        }

        String connectionString = args[0];

        int numRequests;
        try
        {
            numRequests = Integer.parseInt(args[1]);
        }
        catch (NumberFormatException e)
        {
            System.out.format(
                    "Could not parse the number of requests to send. "
                            + "Expected an int but received:\n%s.\n", args[1]);
            return;
        }

        IotHubClientProtocol protocol;
        if (args.length == 2)
        {
            protocol = IotHubClientProtocol.MQTT;
        }
        else
        {
            String protocolStr = args[2].toLowerCase();
            if (protocolStr.equals("https"))
            {
                protocol = IotHubClientProtocol.HTTPS;
            }
            else if (protocolStr.equals("amqps"))
            {
                protocol = IotHubClientProtocol.AMQPS;
            }
            else if (protocolStr.equals("mqtt"))
            {
                protocol = IotHubClientProtocol.MQTT;
            }
            else if (protocolStr.equals("mqtt_ws"))
            {
                protocol = IotHubClientProtocol.MQTT_WS;
            }
            else
            {
                System.out.format(
                        "Expected argument 3 to be one of 'mqtt', 'mqtt_ws', 'https', or 'amqps' but received %s\n"
                                + "The program should be called with the following args: \n"
                                + "1. [Device connection string] - String containing Hostname, Device Id & Device Key in one of the following formats: HostName=<host_name>;DeviceId=<device_id>;x509=true\n"
                                + "2. [number of requests to send]\n"
                                + "3. (mqtt | https | amqps | amqps_ws | mqtt_ws)\n",
                        protocolStr);
                return;
            }
        }

        System.out.println("Successfully read input parameters.");
        System.out.format("Using communication protocol %s.\n", protocol.name());

        SSLContext sslContext = SSLContextBuilder.buildSSLContext(publicKeyCertificateString, privateKeyString);
        ClientOptions clientOptions = ClientOptions.builder().sslContext(sslContext).build();
        DeviceClient client = new DeviceClient(connectionString, protocol, clientOptions);

        System.out.println("Successfully created an IoT Hub client.");

        client.open(true);

        System.out.println("Opened connection to IoT Hub.");
        System.out.println("Sending the following event messages:");

        for (int i = 0; i < numRequests; ++i)
        {
            double temperature = 20 + Math.random() * 10;
            double humidity = 30 + Math.random() * 20;

            String msgStr = "{\"temperature\":"+ temperature +",\"humidity\":"+ humidity +"}";
            
            try
            {
                Message msg = new Message(msgStr);
                msg.setContentType("application/json");
                msg.setProperty("temperatureAlert", temperature > 28 ? "true" : "false");
                msg.setMessageId(java.util.UUID.randomUUID().toString());
                System.out.println(msgStr);

                client.sendEvent(msg, D2C_MESSAGE_TIMEOUT_MILLISECONDS);
                System.out.println("Successfully sent the message");
            }
            catch (IotHubClientException e)
            {
                System.out.println("Failed to send the message. Status code: " + e.getStatusCode());
            }
        }
        
        // close the connection
        System.out.println("Closing the client...");
        client.close();
    }
}
