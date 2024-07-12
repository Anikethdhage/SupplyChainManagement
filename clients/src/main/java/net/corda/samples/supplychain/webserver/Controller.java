package net.corda.samples.supplychain.webserver;


import net.corda.core.identity.CordaX500Name;
import net.corda.core.identity.Party;
import net.corda.core.messaging.CordaRPCOps;
import net.corda.samples.supplychain.accountUtilities.CreateNewAccount;
import net.corda.samples.supplychain.accountUtilities.ShareAccountTo;
import net.corda.samples.supplychain.flows.AddPurchaseOrderFlow;
import net.corda.samples.supplychain.helper.PurchaseOrderData;
import org.springframework.web.bind.annotation.*;

/**
 * Define your API endpoints here.
 */
@RestController
@RequestMapping("/") // The paths for HTTP requests are relative to this base path.
public class Controller {

    private final CordaRPCOps proxy;

    public Controller(NodeRPCConnection rpc) {
        this.proxy = rpc.proxy;
    }

    @PostMapping(value = "addPurchaseOrder/{buyer}/{seller}")
    public String addPurchaseOrder(@RequestBody PurchaseOrderData purchaseOrderData, @PathVariable(value = "buyer") String buyer,
                                   @PathVariable(value = "seller") String seller) {
        String response = "";
        try{
            response = proxy.startFlowDynamic(AddPurchaseOrderFlow.class, purchaseOrderData, buyer, seller).getReturnValue().get().toString();
        }catch (Exception e) {
            e.getMessage();
            e.printStackTrace();
            return "not added";
        }
        return response;
    }

    @PostMapping(value = "createAccount/{acctName}")
    public String createAccount(@PathVariable(value = "acctName") String acctName) {
        String response ="";
        try {
            response = proxy.startFlowDynamic(CreateNewAccount.class, acctName).getReturnValue().get().toString();
        } catch (Exception e){
            e.printStackTrace();
            return "Not Created";
        }
        return response;
    }

    @PostMapping(value = "shareAccountTo/{acctNameShared}/{shareTo}")
    public String shareAccountTo(@PathVariable(value = "acctNameShared") String acctNameShared, @PathVariable(value = "shareTo") String shareTo) {

        CordaX500Name x500Name = CordaX500Name.parse(shareTo);
        final Party otherParty = proxy.wellKnownPartyFromX500Name(x500Name);

        String response = "";
        try {
            response = proxy.startFlowDynamic(ShareAccountTo.class, acctNameShared, otherParty).getReturnValue().get().toString();
        } catch (Exception e){
            e.printStackTrace();
            return "Not Created";
        }
        return  response;
    }


}