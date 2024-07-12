package net.corda.samples.supplychain.states;

import net.corda.samples.supplychain.contracts.ShippingRequestStateContract;
import net.corda.core.contracts.BelongsToContract;
import net.corda.core.contracts.ContractState;
import net.corda.core.identity.AbstractParty;
import net.corda.core.identity.AnonymousParty;
import net.corda.core.identity.Party;
import net.corda.samples.supplychain.helper.PurchaseOrderData;

import java.util.ArrayList;
import java.util.List;

// *********
// * State *
// *********
@BelongsToContract(ShippingRequestStateContract.class)
public class ShippingRequestState implements ContractState {

    private AnonymousParty pickUpFrom;
    private String deliverTo;
    private Party shippper;
    private PurchaseOrderData cargo;
    private List<AbstractParty> participants;

    public ShippingRequestState(AnonymousParty pickUpFrom, String deliverTo, Party shippper, PurchaseOrderData cargo) {
        this.pickUpFrom = pickUpFrom;
        this.deliverTo = deliverTo;
        this.shippper = shippper;
        this.cargo = cargo;
        this.participants = new ArrayList<AbstractParty>();
        participants.add(pickUpFrom);
        participants.add(shippper);
    }

    public AnonymousParty getPickUpFrom() {
        return pickUpFrom;
    }

    public void setPickUpFrom(AnonymousParty pickUpFrom) {
        this.pickUpFrom = pickUpFrom;
    }

    public String getDeliverTo() {
        return deliverTo;
    }

    public void setDeliverTo(String deliverTo) {
        this.deliverTo = deliverTo;
    }

    public Party getShippper() {
        return shippper;
    }

    public void setShippper(Party shippper) {
        this.shippper = shippper;
    }

    public PurchaseOrderData getCargo() {
        return cargo;
    }

    public void setCargo(PurchaseOrderData cargo) {
        this.cargo = cargo;
    }

    @Override
    public List<AbstractParty> getParticipants() {
        return this.participants;
    }
}