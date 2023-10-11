

**Service description**<br/>
The service responsible reads instruments data from the stream and processes these data and finally shows <br/>
in 1-minute candles data in 30 minutes window. <br/>

**Requirements**<br/>
JDK 11, Gradle

**Compiling and Test and Execute**<br/>
Use the following command to work with project:<br/>

```diff
./gradlew build
./gradlew test
./gradlew run
```

**Assumptions**<br/>
* I assume that if we do not receive the quote for an instrument for more than 1 minute we should use the last candle data for that in 30 minutes window, not about the data before or out of the 30 minutes window;<br/>
* I assume in this task I should write a test that covers my changes area and not about the existing code area already;<br/>
* I used the in-memory cache approach, and I know if we receive huge data in long-term up-time may be a memory leak will happen. for the in-memory approach, we can write a job that clears the in-memory cache to prevent a memory leak, but I skipped this part because is outside the task requirement;<br/>
* I assume for human-readable, it is better to add two other fields to show that;<br/>


## Future Development Discussion (Part 2 of assignment)

**Scalability**<br/>
In simple terms, scalability is your application's ability to cope with an increasing number of users concurrently interacting with the app.<br/>
There is some approach that we can achieve to scalability :<br/>
1. X-axis: scaling consists of running multiple copies of an application behind a load balancer. If there are N copies then each copy handles 1/N of the load. This is a simple, commonly used approach to scaling an application.<br/>
2. Y-axis: consists of running multiple, identical copies of the application, Y-axis scaling splits the application into multiple, different services. Each service is responsible for one or more closely related functions.
   There are a couple of different ways of decomposing the application into services. for example, we can split this service into two different services, one service responsible read from the stream and putting data into a thing like Kafka,
   and another service reading from Kafka and processing on data. both of them can be scaled Up in spike times.<br/>

**Failover**<br/>
* Run multiple application that read from the stream and takes a snapshot from data and stores it in S3 (for example)
and if the stream goes down the main service can work correctly and use the old data as well.<br/>
* Circuit Breaker Pattern


