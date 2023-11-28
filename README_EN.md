## Description
##### This project was carried out for messaging purposes using the mqtt protocol.





## Sample

![login](http://pic.song0123.com/img/login.png)

##### You can connect to the broker with user and password authentication or anonymously. The image shows an example of connection with user and password authentication.

"jack Nortek" here is an alias and can be empty. "192.168.1.184" is the mqtt server address. It can be another device or a client device. The premise is that the mqtt service must be turned on, otherwise the connection Not on.





Firebase anonymous authentication takes place when you successfully connect to your broker. Your unique authentication id and other information is saved in the Firestore database. If you do not log out, your information will be remembered. If you log out, your anonymous identity and connection information will be deleted.





![main_page](http://pic.song0123.com/img/main_page.png)



After the connection is successful, you will jump to the page above. This page will display three Views, namely Dashboard, Subscribe, and Publish;

The **Dashboard page** is used to display received messages. The top shows the address and port number of the connected device, as well as the connection status;

The **Subscribe page** is used to subscribe to messages. In the picture above, I subscribed to a topic "beijing", which will be displayed in the listview below. This makes it easy to see what topics are subscribed;

The **Publish page** sends a message. In the picture above, a string "this message" is sent, and the topic is: "beijimg"







### unsubscribe

 <img src="http://pic.song0123.com/img/un_subscribe.png" alt="un_subscribe" style="zoom:50%;" />

Long press on the Subscribe page to cancel the subscription





##### Publish and subscribe example can be seen in the pictures. You can choose qos(Quality of Service) when publishing and subscribing. You can publish the message as retain (Remember, each topic can only have one retained message). 

 ![image-20231124182048230](http://pic.song0123.com/img/image-20231124182048230.png)





##### If you want to delete your information and session, you can exit from the menu at the top or use the back button of your device.

 <img src="http://pic.song0123.com/img/clear_msg.png" alt="clear_msg" style="zoom:50%;" />









## Sources

Eclipse Paho Android Service

Online Websocket Client http://www.hivemq.com/demos/websocket-client/

Firebase Anonymous Authentication https://firebase.google.com/docs/auth/android/anonymous-auth

Firabase Firestore https://firebase.google.com/docs/firestore

Icon made by Freepik from www.flaticon.com







## Contact

Andysung

Mail: hiandysong@gmail.com
