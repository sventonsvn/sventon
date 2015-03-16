If enabled, the built in mail notifier sends a mail to the specified list of users each time a new repository revision is detected. The parameter `revisionCountThreshold` (below) makes sure to limit the number of mails per update.

To enable the notifier, remove the remark from bean `mailNotifier` in the file `WEB-INF/applicationContext.xml`

```
  <!-- NOTE: Make sure to change the parameters below! -->
  <bean id="mailNotifier" class="org.sventon.repository.observer.MailNotifier" init-method="init">
    <property name="baseUrl" value="http://yourserver.com/svn/"/>
    <property name="host" value="smtp.yourserver.com"/>
    <property name="from" value="sventon@yourserver.com"/>
    <property name="subject" value="`instanceName` - revision `revision`"/>
    <property name="port" value="25"/>
    <property name="receivers" ref="mailNotificationReceivers"/>
    <property name="dateFormat" value="yyyy-MM-dd HH:mm:ss"/>
    <property name="revisionCountThreshold" value="100"/>
    <property name="bodyTemplateFile" value="/mailtemplate.html"/>
    <property name="auth" value="false"/>
    <property name="ssl" value="false"/>
    <property name="user" value=""/>
    <property name="password" value=""/>
  </bean>

  <!-- List of notification mail receivers -->
  <util:list id="mailNotificationReceivers">
    <value>you@yourserver.org</value>
  </util:list>
```

Make sure to remove the remark in the `observerList` aswell.

```
 <ref local="mailNotifier"/>
```