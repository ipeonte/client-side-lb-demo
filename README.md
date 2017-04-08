# Client Side Load Balancing Demo

## General Info

### Retries count

When connection or data read timed out the internal ribbon load balancer automatically retries.
The number of host(s) to retry and number of attempts on each controled by next two ribbon configurarion parameters:

<client>.ribbon.MaxAutoRetries=x
<client>.ribbon.MaxAutoRetriesNextServer=y

Internally the total number of retries equals next formula:
(MaxAutoRetries + 1) * (MaxAutoRetriesNextServer + 1)

For example next demo-producer configuration parameters means retry 5 times on same host and than try 2 more hosts with 5 retries on each.
The total number of retries will be 6 * 3 = 18

data-consumer.ribbon.MaxAutoRetries=5
data-consumer.ribbon.MaxAutoRetriesNextServer=2

### Connection and Read timeout emulator

The data-consumer has 2 pre-configure parameter emulating slow network connectivity and/or slow application response

The connection timeout is random in [0,1] second range
The service response timeout is random in same [0,1] range

## Prerequisites:

  1. Build all project

	mvn clean install


  2. Start service registry

	java -jar reg-service/target/reg-service-1.0.0-RELEASE.jar 

## Demo

There are multiple demo scenarios for with good/bad connectivities. Even if ribbon load balancer has a good retry ability (with same payload) 
it will not try forever and can potentially fail when exceed the confitured maximum number of retries (see info section)

### 100% successfull scenario

The configured connection and read timeout for data-producer is set 0.8 of maximum connection timeout and response timeout. So it gives 20% chance on failing 
for single time connection and for 5 retries on same server the chances it totally fail will be 0.2^6 = 0.000064  ~ 0%
Since we running 3 instances and restarting them time by time the chance the request fails will be much much less so I'm expecting the 100% requests successfully 
completed

The demo steps are next

  1. Start 3 data-consumers (all on different hosts)

	java -jar 

  2. Start 3 data-providers with next configuration parameters

	java -jar 

  3. During test randomly stop/start 1 or 2 data consumers, for example stop 1st -> stop 2nd -> wait 5-10 seconds -> start 1st -> wait 5-10 seconds -> 
stop 3rd -> wait 5-10 seconds -> start 2nd -> wait 5-10 seconds -> start 3rd. Repeat in any random order but make sure at least one data-consumer always running.

  4. After all data-producers finished check each portX.txt file for error(s)

	grep ERROR port1.txt
	grep ERROR port2.txt
	grep ERROR port3.txt
 
### Some % failed scenario

The configured connection and read timeout for data-producer is set 0.1 of maximum connection timeout and response timeout.So it gives 90% chance on failing 
for single time connection and for 5 retries on same server the chances it totally fail will be 0.9^5 = 0.59049 ~ 60%
Since we running 3 instances and restarting them time by time the chance the request fails will be much less but still quite possible.

Repeat steps 1 to 4 from previous demo. Now the portX.txt file with high probability might have some ERROR(s). The longer single data-consumer works the higher
number of error will be found in the portX.txt file

