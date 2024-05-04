package ng.upperlink.nibss.cmms.tokenauth;

import com.google.gson.Gson;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class SecondFactorClient {
    private Client client;
    private WebTarget targetBaseUrl;

    public SecondFactorClient(){
        this.client = ClientBuilder.newClient();
        this.targetBaseUrl = client.target("http://10.7.7.104:86/2FAService/api");
    }

    public Client getClient() {
        return client;
    }

    public WebTarget getTargetBaseUrl() {
        return targetBaseUrl;
    }
}