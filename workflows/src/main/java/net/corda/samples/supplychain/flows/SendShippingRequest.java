package net.corda.samples.supplychain.flows;

import co.paralleluniverse.fibers.Suspendable;
import com.r3.corda.lib.accounts.contracts.states.AccountInfo;
import com.r3.corda.lib.accounts.workflows.flows.RequestKeyForAccount;
import com.r3.corda.lib.accounts.workflows.services.AccountService;
import com.r3.corda.lib.accounts.workflows.services.KeyManagementBackedAccountService;
import com.sun.istack.NotNull;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.identity.CordaX500Name;
import net.corda.samples.supplychain.accountUtilities.NewKeyForAccount;
import net.corda.samples.supplychain.contracts.ShippingRequestStateContract;
import net.corda.samples.supplychain.helper.PurchaseOrderData;
import net.corda.samples.supplychain.states.PurchaseOrderState;
import net.corda.samples.supplychain.states.ShippingRequestState;
import net.corda.core.crypto.TransactionSignature;
import net.corda.core.flows.*;
import net.corda.core.identity.AnonymousParty;
import net.corda.core.identity.Party;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.transactions.TransactionBuilder;
import net.corda.core.utilities.ProgressTracker;

import java.security.PublicKey;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


// ******************
// * Initiator flow *
// ******************
@InitiatingFlow
@StartableByRPC
public class SendShippingRequest extends FlowLogic<String> {

    private final ProgressTracker progressTracker = tracker();

    private static final ProgressTracker.Step GENERATING_TRANSACTION = new ProgressTracker.Step("Generating a HeartState transaction");
    private static final ProgressTracker.Step SIGNING_TRANSACTION = new ProgressTracker.Step("Signing transaction with out private key.");
    private static final ProgressTracker.Step FINALISING_TRANSACTION = new ProgressTracker.Step("Recording transaction") {
        @Override
        public ProgressTracker childProgressTracker() {
            return FinalityFlow.tracker();
        }
    };

    private static ProgressTracker tracker() {
        return new ProgressTracker(
                GENERATING_TRANSACTION,
                SIGNING_TRANSACTION,
                FINALISING_TRANSACTION
        );
    }

    @Override
    public ProgressTracker getProgressTracker() {
        return progressTracker;
    }

    //private variables
    private String whoAmI ;
    private String whereTo;
    private Party shipper;
    private int purchaseOrderNumber;
//    private PurchaseOrderData cargo;


    //public constructor
    public SendShippingRequest(String whoAmI, String whereTo, Party shipper,int purchaseOrderNumber){
        this.whoAmI = whoAmI;
        this.whereTo = whereTo;
        this.shipper = shipper;
        this.purchaseOrderNumber = purchaseOrderNumber;
//        this.cargo = Cargo;
    }

    @Suspendable
    @Override
    public String call() throws FlowException {
        //grab account service
        AccountService accountService = getServiceHub().cordaService(KeyManagementBackedAccountService.class);
        //grab the account information
        AccountInfo myAccount = accountService.accountInfo(whoAmI).get(0).getState().getData();
        AnonymousParty AcctAnonymousParty = subFlow(new RequestKeyForAccount(myAccount));

        AccountInfo targetAccount = accountService.accountInfo(whereTo).get(0).getState().getData();

        StateAndRef<PurchaseOrderState> purchaseOrderStatePage = getServiceHub().getVaultService().queryBy(PurchaseOrderState.class).getStates()
                .stream().filter(it -> it.getState().getData().getPurchaseOrderData().getPurchaseOrderNumber() == purchaseOrderNumber).findFirst().get();

        PurchaseOrderData purchaseOrderData = purchaseOrderStatePage.getState().getData().getPurchaseOrderData();

        //generating State for transfer
        progressTracker.setCurrentStep(GENERATING_TRANSACTION);
        ShippingRequestState output = new ShippingRequestState(AcctAnonymousParty,whereTo,shipper,purchaseOrderData);

        // Obtain a reference to a notary we wish to use.
        /** Explicit selection of notary by CordaX500Name - argument can by coded in flows or parsed from config (Preferred)*/
        final Party notary = getServiceHub().getNetworkMapCache().getNotary(CordaX500Name.parse("O=Notary,L=London,C=GB"));

        TransactionBuilder txbuilder = new TransactionBuilder(notary)
                .addOutputState(output, ShippingRequestStateContract.ID)
                .addCommand(new ShippingRequestStateContract.Commands.Create(), Arrays.asList(shipper.getOwningKey(),AcctAnonymousParty.getOwningKey()));

        txbuilder.verify(getServiceHub());
        //self sign Transaction
        SignedTransaction locallySignedTx = getServiceHub().signInitialTransaction(txbuilder,Arrays.asList(AcctAnonymousParty.getOwningKey()));
        progressTracker.setCurrentStep(SIGNING_TRANSACTION);

        //Collect sigs
        FlowSession sessionForAccountToSendTo = initiateFlow(shipper);
        List<TransactionSignature> accountToMoveToSignature = (List<TransactionSignature>) subFlow(new CollectSignatureFlow(locallySignedTx,
                sessionForAccountToSendTo,shipper.getOwningKey()));
        SignedTransaction signedByCounterParty = locallySignedTx.withAdditionalSignatures(accountToMoveToSignature);
        progressTracker.setCurrentStep(FINALISING_TRANSACTION);

        //Finalize
        subFlow(new FinalityFlow(signedByCounterParty, sessionForAccountToSendTo));
//                Arrays.asList(sessionForAccountToSendTo).stream().filter(it -> it.getCounterparty() != getOurIdentity()).collect(Collectors.toList())));
        return "Request"+ shipper.nameOrNull() +" to send " + purchaseOrderData.toString()+ " to "
                + whereTo + " team";
    }
}


@InitiatedBy(SendShippingRequest.class)
class SendShippingRequestResponder extends FlowLogic<Void> {
    //private variable
    private FlowSession counterpartySession;

    //Constructor
    public SendShippingRequestResponder(FlowSession counterpartySession) {
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

