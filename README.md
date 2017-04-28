# EncSIM


## Brife Introduction
EncSIM is an encrypted and scalable similarity search service for distributed data stores. The architecture of EncSIM enables parallel query processing over distributed, encrypted data records. To reduce user overhead, EncSIM resorts to a variant of the state-of-the-art similarity search algorithm, called all-pairs locality-sensitive hashing (LSH). We describe a novel encrypted index construction for EncSIM based on searchable encryption to guarantee the security of service while preserving performance benefits of all-pairs LSH. Currently, the prototype of EncSIM is deployed at Redis. 

## Publication
Xiaoning Liu, Xingliang Yuan, and Cong Wang, “EncSIM: An Encrypted Similarity Search Service for Distributed High-dimensional Datasets”, In the 25th IEEE/ACM International Symposium on Quality of Service (IWQoS’17).

## Librabies
- JDK-1.8
- OpenCSV-3.9 https://sourceforge.net/projects/opencsv/
- jedis-2.4.2 https://mvnrepository.com/artifact/redis.clients/jedis
- Redis http://download.redis.io/releases/redis-3.2.0.tar.gz

## Installation
Redis installation:

```
 * wget http://download.redis.io/releases/redis-3.2.0.tar.gz
 * tar zxvf redis-3.2.0.tar.gz
 * cd redis-3.2.0
 * make
 * make install
```

## Tweets dataset
Sentiment140 Twitter Data http://help.sentiment140.com/for-students/

The data is a CSV with emoticons removed. Data file format has 6 fields:
- 0 - the polarity of the tweet (0 = negative, 2 = neutral, 4 = positive)
- 1 - the id of the tweet (2087)
- 2 - the date of the tweet (Sat May 16 23:58:44 UTC 2009)
- 3 - the query (lyx). If there is no query, then this value is NO_QUERY.
- 4 - the user that tweeted (robotickilldozr)
- 5 - the text of the tweet (Lyx is cool)

In this project, we parse the CSV file to use pure tweet text(field 5th).

## How to use
1. Import this project in your IDE.

2. Copy the property file inside the **config** folder into **src** folder as a running parameter. We use the setting under linux system with 1,000,000 tweets by default.

3. Before building index, you should preprocess the source file by run **Preprocessing.java** under **src\dist\main**. Noticed that the source file path needs to be passed as an arguement.

4. Run **PlaintextSetup.java** to setup plaintext index and run **PlaintextSearch.java** to search.

5. Run **IndexSetup.java** to setup EncSIM index. Run **IndexSearch.java** to search a single query and **IndexSearchBatch.java** to search a set of queries. Run **IndexInsertion.java** to add new records into index.

6. Run **LshSSESetup.java** to setup baseline index and run **LshSSESetup.java** to search.

## MAINTAINER
- Maggie LIU xiaoning.trust@gmail.com
- Xingliang YUAN xyuancs@gmail.com
