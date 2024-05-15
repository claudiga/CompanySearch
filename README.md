# A spring boot application that searches for company.

Before you can run the application. You will need to either
set the api key variable tru_proxy_apikey or set the tru_proxy_apikey property in application.properties.


## Run instruction
you can run ./mvnw spring-boot:run to run the application.

## Sample request
curl --location 'http://localhost:8080/search' \
--header 'Content-Type: application/json' \
--data '{
"companyName" : "BBC LIMITED",
"companyNumber" : "06500244"
}'

