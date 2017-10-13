# SD_TOPK_approach

   &emsp; The cloud allows users and companies to efficiently store and process their data in third-party data centers. However, users typically loose physical access control to their data. Thus, potentially sensitive data gets at risk of security attacks, e.g., from employees of the cloud provider. According to a recent report published by the Cloud Security Alliance, security attacks are one of the main concerns for cloud users.

  &emsp; One solution for protecting user data is to encrypt the data before sending to the cloud nodes. Then, the challenge is to answer user queries over the encrypted data. A naive solution for answering queries is to retrieve the encrypted database from the cloud to the client, decrypt it, and then evaluate the query over plaintext (non encrypted) data. This solution is not practical, because it does not allow us to take advantage of the cloud computing power for evaluating queries.

&emsp;  Our work is interested in processing top-k queries over encrypted data in distributed environments. Top-k queries have attracted much attention in several areas of information technology such as sensor networks, data stream management systems, crowdsourcing, spatial data analysis, temporal databases, graph databases, etc. A top-k query allows the user to specify a number k, and the system returns the k tuples which are most relevant to the query. The relevance degree of tuples to the query is determined by a scoring function. There are many different approaches for processing top-k queries. One of the best known approaches is TA that works on sorted lists of attribute values. TA can find efficiently the top-k results because of a smart strategy for deciding when to stop reading the database.

&emsp;  In our work, we consider the case of distributed systems where a dataset (.g., a relation) is vertically fragmented and distributed over multiples nodes. The problem of top-k query processing in distributed systems has been yet addressed over plaintext data. However, the proposed approaches assume the existence of local scores of the data items (i.e., their attribute values) in plaintext, and there is no efficient solution capable of evaluating efficiently top-k queries over encrypted data in distributed environments.

&emsp;  When we think about top-k query processing over encrypted data, the first idea that comes to mind is to use a fully homomorphic encryption cryptosystem which allows doing arithmetic operations over encrypted data. Using this type of encryption allows to compute the overall score of data items over encrypted data. However, existing fully homomorphic encryption methods are very expensive in terms of encryption and decryption time. In addition, they do not allow to compare the encrypted data, and find the top-k results.

 &emsp; To resolve the problem of evaluating top-k queries over encrypted data in distributed environments, we propose an efficient approach, called SD-TOPK (Secure Distributed TOPK), that is coordinated and executed in the nodes of the cloud. SD-TOPK includes a top-k query processing algorithm that finds a set of encrypted data that is proven to include the top-k data items. In addition, we propose a powerful filtering algorithm that filters the false positives as much as possible in the cloud side, and returns a small set of encrypted data that will be decrypted in the client side. We theoretically prove the correctness of SD-TOPK and its filtering algorithm. 

&emsp;  We evaluated the performance of our solution over synthetic and real databases. The results show excellent response time and communication cost for the SD-TOPK compared to TA-based approaches. They also show the efficiency of our filtering algorithm that eliminates almost all false positives in the cloud, and reduces the communication cost between the cloud and the client. To the best of our Knowledge, SD-TOPK is the first efficient solution able to evaluate top-k queries over encrypted data in distributed environments.

# Implementation 

&emps; We have implemented our solution using JAVA with PEERSIM simulator whiwh allow as to simulate the distributed environement. PEERSIM is written in JAVA. to use it, we had download it from <a /href:https://sourceforge.net/projects/peersim/files/latest/download> <a>















