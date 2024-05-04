/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ng.upperlink.nibss.cmms.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import ng.upperlink.nibss.cmms.enums.Constants;

/**
 *
 * @author Megafu Charles <noniboycharsy@gmail.com>
 */
public class TransactionTypeSerializer extends JsonSerializer<Integer> {

    @Override
    public void serialize(Integer t, JsonGenerator jg, SerializerProvider sp) throws IOException, JsonProcessingException {
        if (t == 1)
            jg.writeString(Constants.PORTAL_TRANSACTION);
        else
           jg.writeString(Constants.API_TRANSACTION); 
    }
    
}
