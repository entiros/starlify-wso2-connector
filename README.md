# Starlify connector for WSO2 API Gateway
Exports the WSO2 API Gateway details to Starlify as systems and services. 

## Dependencies
1. Java-8 +
2. Maven

### spring-boot-starter-web
For exposure of connector etc. on http.

## Start
Start by cloning the project by using the link below:  
https://github.com/entiros/starlify-wso2-connector.git

## Configuration
Put the text below in your property file to configure your URL for WS02 API Gateway and Starlify:

```
starlify:
	url: https://api.starlify.com
wso2:
	server:
		url: https://gateway.api.cloud.wso2.com/api/am

```

Go to cloned location and run the command below to start the process:

```
mvn clean spring-boot:run
```

## Import WS02 API Gateway details to Starlify
Put the text below in your property file to configure your URL for WS02 API Gateway and Starlify:
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
After successful request submission, you should be able to see all the systems and services from WSO2 in your Starlify network.
