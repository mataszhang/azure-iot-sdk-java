package samples.com.microsoft.azure.sdk.iot.device;

import com.microsoft.azure.sdk.iot.provisioning.device.ProvisioningDeviceClient;
import com.microsoft.azure.sdk.iot.provisioning.device.ProvisioningDeviceClientRegistrationResult;
import com.microsoft.azure.sdk.iot.provisioning.device.ProvisioningDeviceClientStatus;
import com.microsoft.azure.sdk.iot.provisioning.device.ProvisioningDeviceClientTransportProtocol;
import com.microsoft.azure.sdk.iot.provisioning.security.SecurityProviderSymmetricKey;

import java.nio.charset.StandardCharsets;

/**
 * @author shengming.zhang
 * @email shengming.zhang@legrand.com
 */
public class AzureIoTCentralDeviceConnectionStringGenerator
{
    public static void main(String[] args) throws Exception {
        String dpsIdScope = "x";
        String deviceSymmetricKey = "x";
        String dpsDeviceId = "x";

        String dpsGlobalEndpoint = "global.azure-devices-provisioning.net";

        SecurityProviderSymmetricKey securityClientSymmetricKey = new SecurityProviderSymmetricKey(deviceSymmetricKey.getBytes(StandardCharsets.UTF_8), dpsDeviceId);
        ProvisioningDeviceClient provisioningDeviceClient;

        provisioningDeviceClient = ProvisioningDeviceClient.create(dpsGlobalEndpoint, dpsIdScope, ProvisioningDeviceClientTransportProtocol.MQTT, securityClientSymmetricKey);

        ProvisioningDeviceClientRegistrationResult registrationResult = provisioningDeviceClient.registerDeviceSync();

        if (registrationResult.getProvisioningDeviceClientStatus() == ProvisioningDeviceClientStatus.PROVISIONING_DEVICE_STATUS_ASSIGNED)
        {
            System.out.println("IotHUb Uri : " + registrationResult.getIothubUri());
            System.out.println("Device ID : " + registrationResult.getDeviceId());

            String iotHubUri = registrationResult.getIothubUri();
            String deviceId = registrationResult.getDeviceId();

            // HostName=east-asia-iot-hub.azure-devices.net;DeviceId=d1;SharedAccessKey=O9TQ+PmaKk51YYtUqLc3m5MG4lB88XuoHAIoTEwz9RI=

            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder
                    .append("HostName=" + iotHubUri + ";")
                    .append("DeviceId=" + deviceId + ";")
                    .append("SharedAccessKey=" + deviceSymmetricKey + ";");

            String deviceConnectionString = stringBuilder.toString();
            System.out.println(deviceConnectionString);
        }

        provisioningDeviceClient.close();

    }
}
