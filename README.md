# LeoVegas - Wallet Microservice

It is a simple wallet microservice that manages debit and credit transaction of users owing Wallet. 
In this microservice, balance in the wallet can be updated by transactions which is either debit or credit.

Technologies:
  * Java 14.0.1
  * Maven 3.6.3
  * Spring Boot 2.7.0
  * Spring Data JPA
  * Spring Web
  * H2
  * Lombok

## RUN
1. Clone the repository.
2. Run `mvn clean install` command in the terminal.
3. Run `java -jar target/wallet-0.0.1-SNAPSHOT.jar` in the terminal.
4. Then [link](http://localhost:8080) to query


## Example Request and Response

1. User Balance Check [link](http://localhost:8080/wallet/userbalance)
```
- Request
{
    "userId": "5fc03087-d265-11e7-b8c6-83e29cd24f4c"
}
- Response
{
    "balance": 100.00
}
  
```
2. All Balance Check [link](http://localhost:8080/wallet/allbalance)
```
- No Request required
- Response
{
    "allBalanceList": [
        {
            "balance": 100.00
        },
        {
            "balance": 200.00
        },
        {
            "balance": 300.00
        },
        {
            "balance": 50.00
        }
    ]
}
```
3. Debit [link](http://localhost:8080/payment/debit)
```
- Request
{
    "userId": "5fc03087-d265-11e7-b8c6-83e29cd24f4c",
    "amount": 50,
    "transactionId":"f3a0a672-ec22-11ec-8ea0-0242ac120002"
}

- Response
{
    "balance": 50.00
}

```
4. Credit [link](http://localhost:8080/payment/credit)
```
- Request
{
    "userId": "5fc03087-d265-11e7-b8c6-83e29cd24f4c",
    "amount": 50,
    "transactionId":"06cd8332-ec23-11ec-8ea0-0242ac120002"
}

{
    "balance": 250.00
}

```
5. Transaction History [link](http://localhost:8080/transaction/history)
```
- Request
{
    "userId": 1
}

- Response
{
    "transactionHistoryList": [
        {
            "transactionType": "DEBIT",
            "amount": 50.00,
            "transactionTime": "2022-06-01T18:36:17.070+00:00"
        },
        {
            "transactionType": "CREDIT",
            "amount": 50.00,
            "transactionTime": "2022-06-01T18:37:43.476+00:00"
        }
    ]
}
```
