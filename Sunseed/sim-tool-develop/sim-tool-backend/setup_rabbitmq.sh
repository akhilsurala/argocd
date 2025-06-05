#!/bin/bash

# Create the necessary directories
mkdir -p ~/.docker-conf/rabbitmq/

# Write the consumer_timeout setting to the rabbitmq.conf file
echo "consumer_timeout = 7200000" > ~/.docker-conf/rabbitmq/rabbitmq.conf

echo "RabbitMQ configuration file created successfully."
