#!/bin/bash

dapr stop service-1
dapr run --app-id service-1 --app-port 8080 --dapr-http-port 3500 --dapr-grpc-port 50000 -- sleep infinity &

dapr stop service-2
dapr run --app-id service-2 --app-port 8090 --dapr-http-port 3501 --dapr-grpc-port 50001 -- sleep infinity &

dapr stop service-3
dapr run --app-id service-3 --app-port 8100 --dapr-http-port 3502 --dapr-grpc-port 50002 -- sleep infinity &
