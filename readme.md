VoiceIn API Service
==
VoiceIn API Web Service using jersey on Beanstalk.  
https://voicein-web-service.us-west-2.elasticbeanstalk.com
Prerequisite
==
- Jre 1.8
- Java 1.8

Build
==
```sh
mvn clean install
```

Set up HTTPS with beanstalk using sslforfree
==
1. Put the credential file provided by sslforfree in `/usr/share/tomcat7/webapps/.well-known/acme-challenge` folder if using Tomcat as web server.  

2. Add HTTPS(443) inboud/outbound rule in Security Group which binds to the load balancer used by beanstalk's application.

3. Go to the settings of `Listeners` in EC2 -> Load Balancers console.

4. Change the `Load Balancer Protocal` to `HTTPS` and upload the SSL Certificates provided by sslforfree.  

5. Go to the settings of `Load Balancer` under beanstalk application's `Configuration` and enable the `Secure listener port`.

Doc
==
Refer to [wiki](https://github.com/lockys/voicein-web-service/wiki)
