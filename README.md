# Money Transfer Service

The service consists of two parts:

- Client, which initiates money transfer requests, check money request statuse by request ID, checks clients' balances and names by client ID; and
- Server, which has two main components: RESTFul APIs exposed to the client, and Money Transfer Manager which is responsible for processing money transfer requests.

Money transfer requests are initially put in a FIFO queue for greater scalability. The client receives a money transfer request ID which can be used to check the money transfer request status.

Money Transfer Manager checks if the requested amount is valid (greater than zero), that the client who requests a money transfer has enough funds on his/her account, that the clien who receives a money transfer has an active account. If the account is not active, the money are returned to the client who was transfering the money.

Below is a high level architecture of the service:

![mts](.\img\mts.png)

## Demo
The demo will follow this scenario:
- Print names and current balances of three predefined clients
-- Bob with the initial balance of 200
-- Alice with the initial balance of 150
-- Jack witht the initial balance of 0. Jack's account is not active
- transfter 150 from client Bob to client Alice - success
- transfter 1500 from client Bob to client Alice - failure, not enough funds
- transfter -10 from client Bob to client Alice - failure, wrong amount
- transfter 30 from client Alice to client Jack - money returned to Alice as Jack's account is not active
- transfter 40 from client Jack to a non existent client - failure, client does not exist

For the purpose of the demo, threads are put on sleep after each step.

In order to run the demo:

`mvn clean compile assembly:single`

`cd target`

`java -jar MoneyTransfer-1.0-SNAPSHOT-jar-with-dependencies.jar`
