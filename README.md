# Requirements:
Please implement a method that can fetch and return the temperature of one certain county in China. Here are the specific features:
1. The method signature is `public Optional<Integer> getTemperature(String province, String city, String county)`.
2. If the location is invalid, return reasonable value.
3. Add reasonable retry mechanism, cause there might be connection exception when calling the weather API.
4. The method need block invocation if the TPS(transactions per second) is more than 100.

## Module Descriptions
1. commons(major): common utils and tools, contains the okhttp retry mechanism implements.
2. service（major）: Main implements service.
3. test（major）: Junit test module, contains commons and service test. 
4. endpoint: Controller module with one simple rest api, but no test.
5. model: Data entities module.
6. server: Boot Entrance.

## How to start?
 server->com.temperature->DemoApplication
 
## Test Module
Just run the *RetryTest* & *TPSLimitedServiceTest* to check the JunitTest result or coverage.
