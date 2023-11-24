## Description【描述】
This project was carried out for messaging purposes using the mqtt protocol.

该项目是使用 mqtt 协议进行消息传递的。





## Sample【示例】

![login](http://pic.song0123.com/img/login.png)
You can connect to the broker with user and password authentication or anonymously. The image shows an example of connection with user and password authentication.

您可以通过用户和密码身份验证或匿名方式连接到代理。 该图显示了使用用户和密码身份验证的连接示例。

![publish](http://pic.song0123.com/img/publish.png)

Firebase anonymous authentication takes place when you successfully connect to your broker. Your unique authentication id and other information is saved in the Firestore database. If you do not log out, your information will be remembered. If you log out, your anonymous identity and connection information will be deleted.

当您成功连接到代理时，Firebase 会进行匿名身份验证。 您的唯一身份验证 ID 和其他信息保存在 Firestore 数据库中。 如果您不退出，您的信息将被记住。 如果您退出，您的匿名身份和连接信息将被删除。



Publish and subscribe example can be seen in the pictures. You can choose qos(Quality of Service) when publishing and subscribing. You can publish the message as retain (Remember, each topic can only have one retained message). 

发布和订阅示例可以在图片中看到。 发布和订阅时可以选择qos（服务质量）。 您可以将消息发布为保留（请记住，每个主题只能有一条保留消息）。

 ![image-20231124182048230](http://pic.song0123.com/img/image-20231124182048230.png)





If you want to delete your information and session, you can exit from the menu at the top or use the back button of your device.

如果您想删除您的信息和会话，您可以从顶部菜单退出或使用设备的后退按钮。

 ![logout](http://pic.song0123.com/img/logout.png)



## Sources【源码】

Eclipse Paho Android Service

Online Websocket Client http://www.hivemq.com/demos/websocket-client/

Firebase Anonymous Authentication https://firebase.google.com/docs/auth/android/anonymous-auth

Firabase Firestore https://firebase.google.com/docs/firestore

Icon made by Freepik from www.flaticon.com

Reference projects: https://github.com/emrekobak/Android-MQTT-Client





## Contact【联系】

Andysung

Mail: hiandysong@gmail.com

