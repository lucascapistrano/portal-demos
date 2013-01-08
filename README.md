Demo applications with [portal.js](https://github.com/flowersinthesand/portal) / [portal-java](https://github.com/flowersinthesand/portal-java) / [atmosphere](https://github.com/Atmosphere/atmosphere)

Online
  
  * Apache <-- AJP --> Tomcat       http://portal-demos.rasc.ch/ajp/
  * Apache <-- HTTP BIO --> Tomcat  http://portal-demos.rasc.ch/bio/
  * Apache <-- HTTP NIO --> Tomcat  http://portal-demos.rasc.ch/nio/  
  
  * HAProxy <-- HTTP BIO --> Tomcat http://ha-bio.rasc.ch/portal-demos/
  * HAProxy <-- HTTP NIO --> Tomcat http://ha-nio.rasc.ch/portal-demos/
  
Websocket only works with the two HAProxy links. 


Chat
  This is the sample application from [portal-java](https://github.com/flowersinthesand/portal-java)
  
Scheduler
  This is a port from a Node.js application described in this blog post: 
  http://www.bryntum.com/blog/nodejs-ext-scheduler-realtime-updates/
  