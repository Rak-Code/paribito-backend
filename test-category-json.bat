@echo off
echo Testing Category API JSON Response...
echo.
echo Making GET request to http://localhost:8080/api/categories
echo.
curl -X GET http://localhost:8080/api/categories -H "Content-Type: application/json"
echo.
echo.
echo Expected format: [{"id":"...","name":"..."},...]
echo NOT: ["java.util.ArrayList",[...]]
