# data-reconciliation

## Summary

* The data reconciliation app verifies consistency across different data sets used by Companies House including Oracle,
  MongoDB and Elasticsearch.
* Groups of comparators are responsible for comparing data sets, aggregating results and then publishing a message to a
  Kafka topic.
* Comparators are configured using [environment variables](#comparison-groups).

## System requirements

* [Git](https://git-scm.com/downloads)
* [Java](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
* [Maven](https://maven.apache.org/download.cgi)
* [MongoDB](https://www.mongodb.com/)
* [Apache Kafka](https://kafka.apache.org/)
* [Elasticsearch](https://www.elastic.co/)
* [AWS](https://aws.amazon.com/)
* Oracle database

## Building and Running Locally

1. From the command line, in the same folder as the Makefile run `make clean build`
1. Configure project environment variables where necessary (see below).
1. Ensure MongoDB and Elasticsearch are running within the Companies House developer environment
1. Start the service in the CHS developer environment

## Architecture

* The data reconciliation app is implemented using Apache Camel.
* A comparison is triggered when a timer elapses.
* The route triggers the desired function, which fetches the required data sets.
* Retrieved data sets are marshalled into a suitable model and compared with each other.
* Supported comparisons include:
    * Count number of resources in a particular data set.
    * Calculate symmetric difference between two data sets.
    * Identify discrepancies between resources in two data sets.
* The result is transformed into a CSV file and uploaded to S3.
* A message is sent to Kafka after all required comparisons in the group have run.

## Maintenance

* Queries used to [retrieve data from Oracle](src/main/resources/sql)
  and [retrieve search hits from Elasticsearch](src/main/resources/elasticsearch) are located on the classpath.

## Environment variables

### Oracle

|Variable                           |Description                                                                        |Example                            |
|-----------------------------------|-----------------------------------------------------------------------------------|-----------------------------------|
|SPRING_DATASOURCE_URL              |The URL of the Oracle instance where CHIPS application data is stored              |jdbc:oracle:thin@oraclehost:1521:db|
|SPRING_DATASOURCE_USERNAME         |The username that will be used to connect to Oracle                                |username                           |
|SPRING_DATASOURCE_PASSWORD         |The password that will be used to connect to Oracle                                |password                           |
|SPRING_DATASOURCE_DRIVER_CLASS_NAME|The fully qualified class name of the driver that will be used to connect to Oracle|oracle.jdbc.OracleDriver           |

### MongoDB

|Variable                                        |Description                                                                         |Example                  |
|------------------------------------------------|------------------------------------------------------------------------------------|-------------------------|
|SPRING_DATA_MONGODB_URI                         |The URL of the MongoDB instance where CHS application data is stored                |mongodb://mongohost:27017|
|ENDPOINT_MONGODB_COMPANY_PROFILE_DB_NAME        |The name of the MongoDB database used to store company profiles                     |db_name                  |
|ENDPOINT_MONGODB_COMPANY_PROFILE_COLLECTION_NAME|The name of the MongoDB collection used to store company profiles                   |collection_name          |
|ENDPOINT_MONGODB_READ_PREFERENCE                |Determines how the MongoDB client routes read operations to members of a replica set|PRIMARY                  |
|ENDPOINT_MONGODB_DSQ_OFFICER_DB_NAME            |The name of the MongoDB database used to store disqualified officers                |db_name                  |
|ENDPOINT_MONGODB_DSQ_OFFICER_COLLECTION_NAME    |The name of the MongoDB collection used to store disqualified officers              |collection_name          |
|ENDPOINT_MONGODB_INSOLVENCY_DB_NAME             |The name of the MongoDB database used to store company insolvency data              |db_name                  |
|ENDPOINT_MONGODB_INSOLVENCY_COLLECTION_NAME     |The name of the MongoDB collection used to store company insolvency data            |collection_name          |

### Elasticsearch

|Variable                          |Description                                                                                  |Example    |
|----------------------------------|---------------------------------------------------------------------------------------------|-----------|
|ELASTICSEARCH_ALPHA_HOST          |The hostname that will be used to connect to the Elasticsearch alphabetical search cluster   |example.com|
|ELASTICSEARCH_ALPHA_INDEX         |The name of the index that alphabetical search hits will be retrieved from                   |index_name |
|ELASTICSEARCH_ALPHA_PORT          |The port number that will be used to connect to the Elasticsearch alphabetical search cluster|9200       |
|ELASTICSEARCH_ALPHA_PROTOCOL      |The protocol that will be used to connect to the Elasticsearch alphabetical search cluster   |https      |
|ELASTICSEARCH_ALPHA_SEGMENTS      |The number of slices that the scrolling search will be split into                            |3          |
|ELASTICSEARCH_ALPHA_SLICE_SIZE    |The number of hits that the scrolling search will return in each response                    |10000      |
|ELASTICSEARCH_ALPHA_SLICE_FIELD   |The field that will be used to split results of a scrolling search                           |_uid       |
|ELASTICSEARCH_PRIMARY_HOST        |The hostname that will be used to connect to the Elasticsearch primary search cluster        |example.com|
|ELASTICSEARCH_PRIMARY_INDEX       |The name of the index that primary search hits will be retrieved from                        |index_name |
|ELASTICSEARCH_PRIMARY_PORT        |The port number that will be used to connect to the Elasticsearch primary search cluster     |9200       |
|ELASTICSEARCH_PRIMARY_PROTOCOL    |The protocol that will be used to connect to the Elasticsearch primary search cluster        |https      |
|ELASTICSEARCH_PRIMARY_SEGMENTS    |The number of slices that the scrolling search will be split into                            |3          |
|ELASTICSEARCH_PRIMARY_SLICE_SIZE  |The number of hits that the scrolling search will return in each response                    |10000      |
|ELASTICSEARCH_PRIMARY_SLICE_FIELD |The field that will be used to split results of a scrolling search                           |_uid       |
|ENDPOINT_ELASTICSEARCH_LOG_INDICES|Used to log a tally of the number of Elasticsearch search hits that have been processed      |10000      |

### AWS

|Variable                     |Description                                                              |Example          |
|-----------------------------|-------------------------------------------------------------------------|-----------------|
|RESULTS_BUCKET               |The S3 bucket to which results will be uploaded                          |bucket_name      |
|AWS_ACCESS_KEY_ID            |The access key that will be used to connect to AWS                       |access_key       |
|AWS_SECRET_ACCESS_KEY        |The secret access key that will be used to connect to AWS                |secret_access_key|
|AWS_REGION                   |The AWS region that the S3 client will connect to                        |eu-west-2        |
|RESULTS_EXPIRY_TIME_IN_MILLIS|The duration in milliseconds for which comparison results can be accessed|600000           |

### Kafka

|Variable           |Description                         |Example    |
|-------------------|------------------------------------|-----------|
|SCHEMA_REGISTRY_URL|The URL of the Kafka schema registry|example.com|
|KAFKA_BROKER_ADDR  |The URL of the Kafka broker         |example.com|

### Caffeine Cache

|Variable               |Description                                                       |Example|
|-----------------------|------------------------------------------------------------------|-------|
|CACHE_EXPIRY_IN_SECONDS|The duration in seconds after which cached results will be evicted|300    |

## Comparison Groups

### Description

The following tables contain toggles (for enabling/disabling each comparison) and timer delays (after application startup for each comparison) for their corresponding comparison groups.

### Company Profile Comparisons - MongoDB-Oracle

|Variable                                      |Description                                                  |Example            |
|----------------------------------------------|-------------------------------------------------------------|-------------------|
|COMPANY_COUNT_MONGO_ORACLE_ENABLED            |Company count comparator toggle                              | "true" / "false"  |
|COMPANY_COUNT_MONGO_ORACLE_DELAY              |Company count comparator delay                               | "30s"             |
|COMPANY_NUMBER_MONGO_ORACLE_ENABLED           |Company number comparator toggle                             | "true" / "false"  |
|COMPANY_NUMBER_MONGO_ORACLE_DELAY             |Company number comparator delay                              | "1m30s"           |
|COMPANY_STATUS_MONGO_ORACLE_ENABLED           |Company status comparator toggle                             | "true" / "false"  |
|COMPANY_STATUS_MONGO_ORACLE_DELAY             |Company status comparator delay                              | "9m30s"           |

### Disqualified Officer Comparisons - MongoDB-Oracle

|Variable                                      |Description                                                  |Example            |
|----------------------------------------------|-------------------------------------------------------------|-------------------|
|DSQ_OFFICER_ID_MONGO_ORACLE_ENABLED           |Disqualified officer comparator toggle                       | "true" / "false"  |
|DSQ_OFFICER_ID_MONGO_ORACLE_DELAY             |Disqualified officer comparator delay                        | "4m30s"           |

### Elasticsearch Comparisons - MongoDB-Elasticsearch
|Variable                                      |Description                                                  |Example            |
|----------------------------------------------|-------------------------------------------------------------|-------------------|
|COMPANY_NUMBER_MONGO_PRIMARY_ENABLED          |Primary index company number comparator toggle               | "true" / "false"  |
|COMPANY_NUMBER_MONGO_PRIMARY_DELAY            |Primary index company number comparator delay                | "2m30s"           |
|COMPANY_NUMBER_MONGO_ALPHA_ENABLED            |Alpha index company number comparator toggle                 | "true" / "false"  |
|COMPANY_NUMBER_MONGO_ALPHA_DELAY              |Alpha index company number comparator delay                  | "3m30s"           |
|COMPANY_NAME_MONGO_PRIMARY_ENABLED            |Primary index company name comparator toggle                 | "true" / "false"  |
|COMPANY_NAME_MONGO_PRIMARY_DELAY              |Primary index company name comparator delay                  | "5m30s"           |
|COMPANY_NAME_MONGO_ALPHA_ENABLED              |Alpha index company name comparator toggle                   | "true" / "false"  |
|COMPANY_NAME_MONGO_ALPHA_DELAY                |Alpha index company name comparator delay                    | "6m30s"           |
|COMPANY_STATUS_MONGO_PRIMARY_ENABLED          |Primary index company status comparator toggle               | "true" / "false"  |
|COMPANY_STATUS_MONGO_PRIMARY_DELAY            |Primary index company status comparator delay                | "7m30s"           |
|COMPANY_STATUS_MONGO_ALPHA_ENABLED            |Primary index company status comparator toggle               | "true" / "false"  |
|COMPANY_STATUS_MONGO_ALPHA_DELAY              |Primary index company status comparator delay                | "8m30s"           |

### Company Insolvency Comparisons
|Variable                                      |Description                                                  |Example            |
|----------------------------------------------|-------------------------------------------------------------|-------------------|
|INSOLVENCY_COMPANY_NUMBER_MONGO_ORACLE_ENABLED|Insolvency company number comparator toggle                  | "true" / "false"  |
|INSOLVENCY_COMPANY_NUMBER_MONGO_ORACLE_DELAY  |Insolvency company number comparator delay                   | "10m30s"          |

## Output aggregation configuration

|Variable                                 |Description                                                                          |Example         |
|-----------------------------------------|-------------------------------------------------------------------------------------|----------------|
|EMAIL_RECIPIENT_LIST                     |The email accounts that will be notified when results from a comparison are available|user@example.com|
|EMAIL_APPLICATION_ID                     |Template configuration for the email sender                                          |application_id  |
|EMAIL_MESSAGE_ID                         |Template configuration for the email sender                                          |message_id      |
|EMAIL_MESSAGE_TYPE                       |Template configuration for the email sender                                          |message_type    |
|EMAIL_SENDER                             |The value of the email's To field                                                    |user@example.com|

## Miscellaneous

|Variable                  |Description                                                    |Example|
|--------------------------|---------------------------------------------------------------|-------|
|RESULTS_INITIAL_CAPACITY  |Used to optimise collections for the number of expected results|1000000|

## Building the docker image

    mvn compile jib:dockerBuild -Dimage=169942020521.dkr.ecr.eu-west-1.amazonaws.com/local/data-reconciliation

## Running Locally using Docker

1. Clone [Docker CHS Development](https://github.com/companieshouse/docker-chs-development) and follow the steps in the
   README.

1. Enable the `data-reconciliation` module

1. Run `tilt up` and wait for all services to start

### To make local changes

Development mode is available for this service
in [Docker CHS Development](https://github.com/companieshouse/docker-chs-development).

    ./bin/chs-dev development enable data-reconciliation

This will clone the data reconciliation app into the repositories folder. Any changes to the code, or resources will
automatically trigger a rebuild and reluanch.
