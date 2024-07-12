# Supply Chain Management using Corda Accounts


This sample describes a mock/simple supply chain business flow.

<p align="center">
  <img src="./Business%20Flow.png" alt="Corda" width="500">
</p>


## Pre-Requisites

For development environment setup, please refer to: [Setup Guide](https://docs.r3.com/en/platform/corda/4.9/community/getting-set-up.html).

## Runnning the nodes
Go into the project directory and build the project
```
./gradlew clean build deployNodes
```
Or

Go to maven tool in Intellij IDE and build the project
```
gradle clean build deployNodes
```
Run the project from root folder
```
./build/nodes/runnodes
```
Now, you should have four Corda terminals opened automatically.

## Shell Instructions (Part 1) - Creating & Sharing Accounts
Go to the Buyer's node terminal and paste in the following code: (You can select all 7 lines and copy to the terminal all at once)
```
flow start CreateNewAccount acctName: BuyerOrderSender
flow start CreateNewAccount acctName: BuyerProcurement
flow start CreateNewAccount acctName: BuyerFinance
flow start CreateNewAccount acctName: BuyerWarehouse

flow start ShareAccountTo acctNameShared: BuyerProcurement, shareTo: Seller
flow start ShareAccountTo acctNameShared: BuyerFinance, shareTo: Seller
flow start ShareAccountTo acctNameShared: BuyerWarehouse, shareTo: ShippingCo
flow start ShareAccountTo acctNameShared: BuyerWarehouse, shareTo: Seller
```
This is creating 4 accounts under Buyer's node and sharing with their specific counterparty's node or account.

Go to the Seller's node terminal and paste in the following code: 
```
flow start CreateNewAccount acctName: sellerOrderReceiver
flow start CreateNewAccount acctName: SellerSales
flow start CreateNewAccount acctName: SellerFinance
flow start CreateNewAccount acctName: SellerInventory

flow start shareAccountTo acctNameShared: sellerOrderReceiver, shareTo: Buyer
flow start ShareAccountTo acctNameShared: SellerSales, shareTo: Buyer
flow start ShareAccountTo acctNameShared: SellerFinance, shareTo: Buyer
flow start ShareAccountTo acctNameShared: SellerInventory, shareTo: ShippingCo
```
This is creating 3 accounts under Seller's node and sharing with their specific counterparty's node or account.

[Optional]: You can run a vaultQuery to see the [AccountInfo](https://training.corda.net/libraries/accounts-lib/#design) that been stored at each node by using: 
```
run vaultQuery contractStateType: com.r3.corda.lib.accounts.contracts.states.AccountInfo
```
## Shell Instructions (Part 2) - Executing Business Flows
### Step 1 : Buyer's Order sender team will send order details to seller's order receivers team
navigate to Buyer's Node terminal and run
```

```
### Step 2 : Seller's order receiver team send a internal message to seller's sales team to prepare for invoice
navigate to Seller's Node Terminal and run
```
flow start InternalMessage fromWho: sellerOrderReceiver, whereTo: SellerSales, message: order received send invoice
```

### Step 3: Seller's sales team send invoice for $500 to Buyer's procurement team
navigate to Seller's node terminal and run
```
flow start SendInvoice whoAmI: SellerSales, whereTo: BuyerProcurement, amount: 500 
```
[Optional]: If you would like to verify the message had been successfully pass into Buyer's procurement team, you can navigate to Buyer's node terminal and type in: 
```
flow start ViewInboxByAccount acctname: BuyerProcurement
```
You see that the invoice state amount 500 is returned. You can also replace the BuyerProcurement with BuyerWarehouse to see that the non-relevant accounts has no visiblity about the invoice state. 

### Step 4: Buyer's procurement team will send an internal message to Buyer's Buyer's finance team
Navigate to Buyer's node terminal and type in: 
```
flow start InternalMessage fromWho: BuyerProcurement, whereTo: BuyerFinance, message: Send 500 to SellerFinance
```
[Optional verification]: run ```flow start ViewInboxByAccount acctname: BuyerFinance``` at Buyer' node terminal

### Step 5: Buyer's finance team send a payment to Seller's finance team
Navigate to Buyer's node terminal and type in:
```
flow start SendPayment whoAmI: BuyerFinance, whereTo: SellerFinance, amount: 500
```
[Optional verification]: run ```flow start ViewInboxByAccount acctname: SellerFinance``` at Seller's node terminal 

### Step 6: Seller's finance team send an internal message to Seller's inventory team to instruct them to send the cargo
Navigate to Seller's node terminal and type in
```
flow start InternalMessage fromWho: SellerFinance, whereTo: SellerInventory, message: send Cargo to Buyer
```
[Optional verification]: run ```flow start ViewInboxByAccount acctname: SellerInventory``` at Seller's node terminal 

### step 7: Seller's inventory team send a shipping work order for shipping company 
Navigate to Seller's node terminal and type in
```
flow start SendShippingRequest whoAmI: SellerInventory, whereTo: BuyerWarehouse, shipper: ShippingCo, Cargo: 10 boxes of Books
```
[Optional verification]: run ```run vaultQuery contractStateType: ShippingRequestState``` at ShippingCo's node terminal 

### Step 8: Shipping company sends the cargo to Buyer's warehouse
Navigate to ShippingCo's node terminal and type in
```
flow start SendCargo pickupFrom: SellerInventory, shipTo: BuyerWarehouse, cargo: Books
```
[Optional verification]: run ```flow start ViewInboxByAccount acctname: BuyerWarehouse``` at Buyer's node terminal 

### Now, the entire business chain is completed. 




