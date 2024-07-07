# Revenium Spring Boot Starter

This is a Spring Boot starter to easily integrate Revenium metering with a Spring Boot application.

## Installation

You'll need to add the Revenium dependency to either your pom.xml or build.gradle file.

### Maven

Add the following dependency to your `pom.xml`:

```xml

<dependency>
    <groupId>io.revenium.metering</groupId>
    <artifactId>revenium-spring-boot-starter</artifactId>
    <version>1.0.0</version>
</dependency>
   ```

### Gradle

Add the following dependency to your `build.gradle`:

```groovy   
implementation 'io.revenium.metering:revenium-spring-boot-starter:1.0.0'
```

## Configuration

You'll need to add the following properties to your `application.properties` or `application.yml` file:

```properties
revenium.metering.api-key=hak_api_key
revenium.metering.url=https://api.revenium.io/meter/v1/api
```

```yaml
revenium:
  metering:
    api-key: hak_api_key
    url: https://api.revenium.io/meter/v1/api
```

## Usage

The Revenium Spring Boot starter provides an aspect that can be annotated on a method to send metering data to the
Revenium platform. This can be applied to any method in your Spring Boot application but is typically used to meter REST
controller or service method invocations.

The annotation accepts the following parameters:

- `subscriptionId` - The subscription id identifying who is consuming the data
- `sourceId` - The source (asset) id of the metering data
- `elements` - A JSON string representing the metering elements to send to the Revenium platform. This can be a JSON
  string or a SpEL expression that evaluates to a JSON string. The SpEL expression can reference the method arguments
  and return value.

The metered aspect provides "result" and "args" context variables ino the SpEL expression. The "result" variable
contains the return value of the method being metered and the "args" variable contains the method arguments.

### Example Usage in a REST Controller

Here's an example of how to use the `@Metered` annotation on a REST controller POST operation:

```java

@PostMapping
@Metered(
        subscriptionId = "#args[1]",
        sourceId = "'sentiment-analysis'",
        elements = "{'tokens': #result.tokensConsumed}"
)
public SummarizationResponse analyzeSentiment(@RequestBody SummarizationRequest request,
                                              @RequestHeader("x-api-key") String apiKey) {
    SummarizationResult result = summarizationService.summarize(request.getText());
    SummarizationResponse response = new SummarizationResponse();
    response.setText(result.getText());
    response.setTokensConsumed(result.getTokensConsumed());
    return response;
}
```

This example sends metering data to the Revenium platform when the `analyzeSentiment` method is invoked. The sourceId is statically set to "sentiment-analysis" and the subscriptionId is dynamically set to the second argument of the method. The metering elements are set to a JSON object containing the number of LLM tokens consumed by the method.