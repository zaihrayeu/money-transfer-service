# Money Transfer Service

The service implements money transfers between accounts. The service is exposed through the following RESTful APIs:

- Request client name by client ID;
- Request client balance by client ID;
- Request a money transfer from one client to another. The actual money transfer is not made on the request, but a money transfer request ID is returned to the client;
- Request a money transfer request status given a request ID.  

Architecturally, the service consists of two parts:

- Client, which initiates money transfer requests, checks money request statuses, checks clients' balances, etc; and
- Server, which has two main components: the above mentioned RESTFul APIs and Money Transfer Manager (MTM) which is responsible for processing money transfer requests.

When a money transfer request is made by the client, MTM checks if the requested amount is valid (i.e., greater than zero) and that the client who requests the money transfer has enough funds on his/her account. If this is the case, the requested amount is debited from the client's account and saved with the money transfer request. A money transfer request ID is generated and returned to the client. The request itself is put in a FIFO queue for a greater scalability of the service. 

Money transfer requests are polled from the queue and processed one by one by MTM, which checks that the client who receives the money has an active account. If the account is active, the funds are credited to the account. Otherwise, the funds are returned to the client who requested the money transfer.

Below is a high level architecture of the service:

![mts](https://github.com/zaihrayeu/money-transfer-service/blob/master/img/mts.png?raw=true)

## Demo
The demo follows this scenario:
1. Print names and current balances of three predefined clients
   * Bob with the initial balance of 200
   * Alice with the initial balance of 150
   * Jack with the initial balance of 0. Jack's account is not active.
2. Transfer 150 from client Bob to client Alice - success
3. Transfer 1500 from client Bob to client Alice - failure, not enough funds
4. Transfer -10 from client Bob to client Alice - failure, wrong amount
5. Transfer 30 from client Alice to client Jack - money returned to Alice as Jack's account is not active
6. Transfer 40 from client Jack to a non existent client - failure, client does not exist

For the purpose of the demo, threads are put on sleep after each step.

In order to run the demo:

`mvn clean compile assembly:single`

`cd target`

`java -jar MoneyTransfer-1.0-SNAPSHOT-jar-with-dependencies.jar`
