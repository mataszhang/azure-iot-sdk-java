This project has adopted the [Microsoft Open Source Code of Conduct](https://opensource.microsoft.com/codeofconduct/). For more information see the [Code of Conduct FAQ](https://opensource.microsoft.com/codeofconduct/faq/) or contact [opencode@microsoft.com](mailto:opencode@microsoft.com) with any additional questions or comments.

# Microsoft Azure IoT SDK for Java

This repository contains both IoT device SDK and IoT service SDK for Java. The Device SDK enables you connect client devices to Azure IoT Hub. The Service SDK enable you to manage your IoT Hub service instance.

Visit http://azure.com/iotdev to learn more about developing applications for Azure IoT.


## Microsoft Azure IoT device SDKs

The Microsoft Azure IoT device SDKs contain code that facilitate building devices and applications that connect to and are managed by Azure IoT Hub services.

Devices and data sources in an IoT solution can range from a simple network-connected sensor to a powerful, standalone computing device. Devices may have limited processing capability, memory, communication bandwidth, and communication protocol support. The IoT device SDKs enable you to implement client applications for a wide variety of devices.


The API reference documentation is [here](http://azure.github.io/azure-iot-sdks).

### OS platforms and hardware compatibility

Azure IoT device SDKs can be used with a broad range of OS platforms and devices. The minimum requirements are for the device platform to support the following:

- Being capable of establishing an IP connection: only IP-capable devices can communicate directly with Azure IoT Hub.
- Support TLS: required to establish a secure communication channel with Azure IoT Hub.
- Support SHA-256: necessary to generate the secure token for authenticating the device with the service.
- Have a Real Time Clock or implement code to connect to an NTP server: necessary for both establishing the TLS connection and generating the secure token for authentication.
- Having at least 64KB of RAM: the memory footprint of the SDK depends on the SDK and protocol used as well as the platform targeted. The smallest footprint is achieved using the C SDK targeting microcontrollers.

You can find an exhaustive list of the OS platforms the various SDKs have been tested against in the [Azure Certified for IoT device catalog](https://catalog.azureiotsuite.com/). Note that you might still be able to use the SDKs on OS and hardware platforms that are not listed on this page: all the SDKs are open sourced and designed to be portable. If you have suggestions, feedback or issues to report, refer to the Contribution and Support sections below.

## Other Microsoft Azure IoT SDKs

The other Azure IoT Service SDKs can be found in the main SDKs repository:

- [Azure IoT service SDKs](https://github.com/azure/azure-iot-sdks)

## Samples

Whithin the repository, you can find various types of simple samples that can help you get started.
Below is a complete list of all these simple samples.
In addition to these simple samples, you can find a long list of [getting started guides](doc/get_started) that describe all the steps necessary to run the simple samples on a wide variety of devices and platforms.
And if you are looking for end to end samples that show how to do simple analytics and processing of the data generated by your device using Azure services such as Stream Analytics, Big Data, Machine Learning and others, check out our [E2E samples gallery](http://aka.ms/azureiotsamples).

- Java device SDK:
   - [Simple send sample](device/samples/send-event): Shows how to connect and send messages to IoT Hub, passing the protocol of your choices as a parameter.
   - [Simple send/receive sample](device/samples/send-receive-sample): Shows how to connect then send and receive messages to and from IoT Hub, passing the protocol of your choices as a parameter.
   - [Simple send serialized messages sample](device/samples/send-serialized-event): Shows how to connect and send serialized messages to IoT Hub, passing the protocol of your choices as a parameter.
   - [Simple sample handling messages received](device/samples/handle-messages): : Shows how to connect to IoT Hub and manage messages received from IoT Hub, passing the protocol of your choices as a parameter.
- Java service SDK:
   - [Device manager sample](service/samples/device-manager-sample): Shows how to work with the device ID registry of IoT Hub. 
   - [Service client sample](service/samples/service-client-sample): Shows how to send Cloud to Device messages through IoT Hub. 

## Contribution, feedback and issues

If you encounter any bugs, have suggestions for new features or if you would like to become an active contributor to this project please follow the instructions provided in the [contribution guidelines](CONTRIBUTING.md).

## Support

If you are having issues using one of the packages or using the Azure IoT Hub service that go beyond simple bug fixes or help requests that would be dealt within the [issues section](https://github.com/Azure/azure-iot-sdks/issues) of this project, the Microsoft Customer Support team will try and help out on a best effort basis.
To engage Microsoft support, you can create a support ticket directly from the [Azure portal](https://ms.portal.azure.com/#blade/Microsoft_Azure_Support/HelpAndSupportBlade).
Escalated support requests for Azure IoT Hub SDKs development questions will only be available Monday thru Friday during normal coverage hours of 6 a.m. to 6 p.m. PST.
Here is what you can expect Microsoft Support to be able to help with:
* **Client SDKs issues**: If you are trying to compile and run the libraries on a supported platform, the Support team will be able to assist with troubleshooting or questions related to compiler issues and communications to and from the IoT Hub.  They will also try to assist with questions related to porting to an unsupported platform, but will be limited in how much assistance can be provided.  The team will be limited with trouble-shooting the hardware device itself or drivers and or specific properties on that device. 
* **IoT Hub / Connectivity Issues**: Communication from the device client to the Azure IoT Hub service and communication from the Azure IoT Hub service to the client.  Or any other issues specifically related to the Azure IoT Hub.
* **Portal Issues**: Issues related to the portal, that includes access, security, dashboard, devices, Alarms, Usage, Settings and Actions.
* **REST/API Issues**: Using the IoT Hub REST/APIs that are documented in the [documentation]( https://msdn.microsoft.com/library/mt548492.aspx).