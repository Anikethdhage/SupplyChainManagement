package net.corda.samples.supplychain.flows;

import co.paralleluniverse.fibers.Suspendable;
import com.r3.corda.lib.accounts.contracts.states.AccountInfo;
import com.r3.corda.lib.accounts.workflows.flows.RequestKeyForAccount;
import com.r3.corda.lib.accounts.workflows.services.AccountService;
import com.r3.corda.lib.accounts.workflows.services.KeyManagementBackedAccountService;
import com.sun.istack.NotNull;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.crypto.TransactionSignature;
import net.corda.core.flows.*;
import net.corda.core.identity.AnonymousParty;
import net.corda.core.identity.Party;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.transactions.TransactionBuilder;
import net.corda.samples.supplychain.contracts.PurchaseOrderContract;
import net.corda.samples.supplychain.helper.PurchaseOrderData;
import net.corda.samples.supplychain.states.PurchaseOrderState;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@InitiatingFlow
@StartableByRPC
public class UpdatePurchaseOrderFlow extends FlowLogic<String> {

    private int purchaseOrderNumber;
    private String productCode;
    private String productName;
    private String buyer;
    private String seller;

    public UpdatePurchaseOrderFlow(int purchaseOrderNumber, String productCode, String productName, String buyer, String seller) {
        this.purchaseOrderNumber = purchaseOrderNumber;
        this.productCode = productCode;
        this.productName = productName;
        this.buyer = buyer;
        this.seller = seller;
    }

    @Suspendable
    @Override
    public String call() throws FlowException {

        //grab account service
        AccountService accountService = getServiceHub().cordaService(KeyManagementBackedAccountService.class);
        //grab the account information
        AccountInfo buyerAcc = accountService.accountInfo(buyer).get(0).getState().getData();
        AccountInfo sellerAcc = accountService.accountInfo(seller).get(0).getState().getData();

        AnonymousParty buyerAcctAnonymousParty = subFlow(new RequestKeyForAccount(buyerAcc));
        AnonymousParty sellerAcctAnonymousParty = subFlow(new RequestKeyForAccount(sellerAcc));

        StateAndRef<PurchaseOrderState> purchaseOrderStatePage = getServiceHub().getVaultService().queryBy(PurchaseOrderState.class).getStates()
                .stream().filter(it -> it.getState().getData().getPurchaseOrderData().getPurchaseOrderNumber() == purchaseOrderNumber).findFirst().get();

//        PurchaseOrderState purchaseOrderState = purchaseOrderStatePage.getState().getData();
        PurchaseOrderData purchaseOrderData = purchaseOrderStatePage.getState().getData().getPurchaseOrderData();
        purchaseOrderData.setProductName(productName);
        purchaseOrderData.setProductCode(productCode);

        PurchaseOrderState purchaseOrderState1 = new PurchaseOrderState(purchaseOrderData, buyerAcctAnonymousParty, sellerAcctAnonymousParty);

        Party notary = getServiceHub().getNetworkMapCache().getNotaryIdentities().get(0);

        TransactionBuilder tx = new TransactionBuilder(notary)
                .addInputState(purchaseOrderStatePage)
                .addOutputState(purchaseOrderState1)
                .addCommand(new PurchaseOrderContract.Commands.Update(), Arrays.asList(buyerAcctAnonymousParty.getOwningKey(), sellerAcctAnonymousParty.getOwningKey()));

        tx.verify(getServiceHub());

        SignedTransaction stx = getServiceHub().signInitialTransaction(tx, Collections.singleton(buyerAcctAnonymousParty.getOwningKey()));

        FlowSession flowSession = initiateFlow(sellerAcc.getHost());

        List<TransactionSignature> accountToMoveToSignature = (List<TransactionSignature>) subFlow(new CollectSignatureFlow(stx, flowSession, sellerAcctAnonymousParty.getOwningKey()));

        SignedTransaction fullySignedTx = stx.withAdditionalSignatures(accountToMoveToSignature);

        subFlow(new FinalityFlow(fullySignedTx, flowSession));

        return "Updated the records";
    }
}

@InitiatedBy(UpdatePurchaseOrderFlow.class)
class UpdatePurchaseOrderFlowResp extends FlowLogic<Void> {
    //private variable
    private FlowSession counterpartySession;

    //Constructor
    public UpdatePurchaseOrderFlowResp(FlowSession counterpartySession) {
        this.counterpartySession = counterpartySession;
    }

    @Override
    @Suspendable
    public Void call() throws FlowException {
        subFlow(new SignTransactionFlow(counterpartySession) {
            @Override
            protected void checkTransaction(@NotNull SignedTransaction stx) throws FlowException {
                // Custom Logic to validate transaction.
            }
        });
        subFlow(new ReceiveFinalityFlow(counterpartySession));
        return null;
    }
}
