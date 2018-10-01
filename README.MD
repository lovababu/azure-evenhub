# Sample Demo on Azure Event Hub Publisher and Subscriber.

## Pre-requisites.  
1. Azure Account.   
2. Create Azure Event hub resource.  
3. Create Azure Storage Account (needed since we are working with ```EventProcessorHost```)  

## Configure
1. Open src/main/resources/eventhub-config.json and configure accordingly.  

## Run
 open ```Main.java```, and run as main application.  
 monitor the logs, sub/pub threads runs parallel.