package net.corda.samples.supplychain.states;

import net.corda.core.contracts.BelongsToContract;
import net.corda.core.contracts.ContractState;
import net.corda.core.identity.AbstractParty;
import net.corda.core.identity.AnonymousParty;
import net.corda.samples.supplychain.contracts.PurchaseOrderContract;
import net.corda.samples.supplychain.helper.PurchaseOrderData;
import org.jetbrains.annotations.NotNull;
import java.util.Arrays;
import java.util.List;

@BelongsToContract(PurchaseOrderContract.class)
public class PurchaseOrderState implements ContractState {

    private PurchaseOrderData purchaseOrderData;
    private AnonymousParty buyer;
    private AnonymousParty seller;

    @NotNull
    @Override
    public List<AbstractParty> getParticipants() {
        return Arrays.asList(buyer, seller);
    }

    public PurchaseOrderState(PurchaseOrderData purchaseOrderData, AnonymousParty buyer, AnonymousParty seller) {
        this.purchaseOrderData = purchaseOrderData;
        this.buyer = buyer;
        this.seller = seller;
    }

    public PurchaseOrderData getPurchaseOrderData() {
        return purchaseOrderData;
    }

    public AnonymousParty getBuyer() {
        return buyer;
    }

    public AnonymousParty getSeller() {
        return seller;
    }
}
