package net.corda.samples.supplychain.contracts;

import net.corda.core.contracts.CommandData;
import net.corda.core.contracts.Contract;
import net.corda.core.transactions.LedgerTransaction;

public class PurchaseOrderContract implements Contract {
    // This is used to identify our contract when building a transaction.
    public static final String ID = "net.corda.samples.supplychain.contracts.PurchaseOrderContract";

    // A transaction is valid if the verify() function of the contract of all the transaction's input and output states
    // does not throw an exception.
    @Override
    public void verify(LedgerTransaction tx) {}

    // Used to indicate the transaction's intent.
    public interface Commands extends CommandData {
        class Create implements Commands {}
        class Update implements Commands {}
    }
}
