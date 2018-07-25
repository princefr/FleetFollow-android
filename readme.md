# FleetFollow android SDK


Fleetfollow is a real-time gps tracking sdk to integrate with your applications that will help you improve the efficiency of your team's operations management.

FleetFollow allows you to efficiently manage the tracking and sequencing of your collaborators' missions, which allows you to better understand operational difficulties. It will offer you the ability to adapt to any situation by giving you a high operational visibility.


## installation 


```
allprojects {
    repositories {
    ...
    maven { url 'https://www.jitpack.io' }
    }
}
```



```
dependencies {
    implementation 'com.github.princefr:FleetFollow-java:0.0.9'
}

```


## Initialisation


```
FleetFollow fleetFollow = new FleetFollow();
fleetFollow.Init(Context, "ApiKey", new User("Firstname", "LastName", "PhoneNumber", "ID", ""));
```
