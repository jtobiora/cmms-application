/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ng.upperlink.nibss.cmms.tokenauth;

import org.apache.commons.codec.binary.Base64;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.client.ClientResponseContext;
import javax.ws.rs.client.ClientResponseFilter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;


public class ClientFilter implements ClientRequestFilter, ClientResponseFilter {
    private  String username;
    private  String password;

    private  String NONCE;
    private  String TIMESTAMP;
    private  String SIGNATURE;
    private  String SIGNATURE_METH;
    private  String Feed;

    private  String sessionKey;

    public ClientFilter(String username, String sessionKey) {
        this.username = username;
        this.sessionKey = sessionKey;
    }

    public ClientFilter(String username, String password, String nonce, String timestamp, String signature, String signature_method, String feed) {
        this.username = username;
        this.password = password;
        this.NONCE = nonce;
        this.TIMESTAMP = timestamp;
        this.SIGNATURE = signature;
        this.SIGNATURE_METH = signature_method;
        this.Feed = feed;
    }

    @Override
    public void filter(ClientRequestContext req) {
        String token = username + ":" + password;

        System.out.println("Feed = " + Feed);

        String base64Token = new String(Base64.encodeBase64(token.getBytes(StandardCharsets.UTF_8)));
        req.getHeaders().add("Authorization", Feed);
        req.getHeaders().add("APIUSER", username);
        String headerString = req.getHeaderString("Authorization");

        System.out.println("Added to HTTP Request Authorization ["+base64Token+"]"+ " headerString : "+headerString);


    }

    @Override
    public void filter(ClientRequestContext arg0, ClientResponseContext crc1)
            throws IOException {

        for (String key : crc1.getHeaders().keySet()) {
            System.out.println("Response Header: " +crc1.getHeaders().get(key));
        }

    }

}