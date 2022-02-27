# Starlify connector for mule gateway
Exports the wso2 api details to starlify as Service, System and Flow.

## Dependencies
1. Java-8 +

### spring-boot-starter-web
For exposure of connector etc. on http.



## Start
First clone the project using below link
https://github.com/entiros/starlify-wso2-connector.git

## Configuration
Make sure proper Wso2 api gateway and starlify url's configured properly in properties file like this

```
starlify:
  url: https://api.starlify.com
wso2:
  server:
    url: https://gateway.api.cloud.wso2.com/api/am

```

Go to cloned location and run below command to start the process
mvn clean spring-boot:run

## import mule api details to Starlify
Use below endpoint to start importing api details to starlify as services, systems and flows

```
	Method : POST
	URL : http://localhost:8080/process/wso2
	Body : 
			{
				"starlifyKey":"starlify-api-key",
				"apiKey":"wso2-api-key",
				"networkId":"starlify-network-id-to-create-services-systems-and-flows"
			}
```

## Output
After successful request submission, you should be able to see all the systems and services from wso2 in give starlify network.