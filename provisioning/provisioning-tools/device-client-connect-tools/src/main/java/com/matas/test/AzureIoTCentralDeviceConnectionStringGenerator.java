package com.matas.test;

import com.microsoft.azure.sdk.iot.provisioning.device.ProvisioningDeviceClient;
import com.microsoft.azure.sdk.iot.provisioning.device.ProvisioningDeviceClientRegistrationResult;
import com.microsoft.azure.sdk.iot.provisioning.device.ProvisioningDeviceClientStatus;
import com.microsoft.azure.sdk.iot.provisioning.device.ProvisioningDeviceClientTransportProtocol;
import com.microsoft.azure.sdk.iot.provisioning.security.SecurityProviderSymmetricKey;

import java.nio.charset.StandardCharsets;

/**
 * 通过 UI 在界面配置的设备
 *
 * @author shengming.zhang
 * @email shengming.zhang@legrand.com
 */
public class AzureIoTCentralDeviceConnectionStringGenerator
{

    /**
     * 将设备注册到 Iot Central的DPS中，然后返回连接字符串
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {

        // dps的id scope
        String dpsIdScope = "0ne00C2D62B";
        // 设备id
        String dpsDeviceId = "ccm-001";
        //设备对称秘钥
        String deviceSymmetricKey = "2dP91pgujuXIfK4+Ebl+4zhdnbk=";


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
