package com.matas.test;

import com.microsoft.azure.sdk.iot.device.ClientOptions;
import com.microsoft.azure.sdk.iot.device.DeviceClient;
import com.microsoft.azure.sdk.iot.device.IotHubClientProtocol;
import com.microsoft.azure.sdk.iot.provisioning.device.*;
import com.microsoft.azure.sdk.iot.provisioning.security.SecurityProviderSymmetricKey;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;

/**
 * 通过Iot Central的组设备注册来实现
 * 1. 在 UI -> 权限 -> 设备连接组 ： 自己创建一个SAS 对称秘钥验证方式的 设备组分配【DPS】
 * 2.  从组密钥生成设备令牌 ： 通过ComputeDerivedSymmetricKeySample 或者 az iot central device compute-device-key --primary-key <enrollment group primary key> --device-id <device ID>
 * 生成设备的SAS秘钥
 * <p>
 * 3. 配置Main 函数中的参数，注册设备到DPS ,返回连接字符串
 *
 * @author shengming.zhang
 * @email shengming.zhang@legrand.com
 * @link IoT Central 中的设备身份验证概念 {https://learn.microsoft.com/zh-cn/azure/iot-central/core/concepts-device-authentication}
 * @link  参考  { TemperatureController.java }
 */
@Slf4j
public class AzureIoTCentralDeviceConnectionStringGenerator2
{
    /**
     * 将设备注册到 Iot Central的DPS中，同时设置设备模板，然后返回连接字符串
     *
     * @param args
     *
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        // dps的id scope
        String dpsIdScope = "0ne00C2D62B";
        // 设备id
        String dpsDeviceId = "mine-001";
        //设备对称秘钥
        String deviceSymmetricKey = "kvPiHm+H1bxUzwkN4v8=";
        // 设备模板Id
        String modelId = "dtmi:com:example:ConnectedCoffeeMaker;1";

        String dpsGlobalEndpoint = "global.azure-devices-provisioning.net";


        AdditionalData additionalData = new AdditionalData();
        additionalData.setProvisioningPayload(com.microsoft.azure.sdk.iot.provisioning.device.plugandplay.PnpHelper.createDpsPayload(modelId));



        SecurityProviderSymmetricKey securityClientSymmetricKey = new SecurityProviderSymmetricKey(deviceSymmetricKey.getBytes(StandardCharsets.UTF_8), dpsDeviceId);
        ProvisioningDeviceClient provisioningDeviceClient;

        provisioningDeviceClient = ProvisioningDeviceClient.create(dpsGlobalEndpoint, dpsIdScope, ProvisioningDeviceClientTransportProtocol.MQTT, securityClientSymmetricKey);

        ProvisioningDeviceClientRegistrationResult registrationResult = provisioningDeviceClient.registerDeviceSync(additionalData);

        ClientOptions options = ClientOptions.builder().modelId(modelId).build();

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

 /*           log.debug("Opening the device client.");
            DeviceClient  deviceClient = new DeviceClient(iotHubUri, deviceId, securityClientSymmetricKey, IotHubClientProtocol.MQTT, options);
            deviceClient.open(true);*/
        }

        provisioningDeviceClient.close();

    }
}
