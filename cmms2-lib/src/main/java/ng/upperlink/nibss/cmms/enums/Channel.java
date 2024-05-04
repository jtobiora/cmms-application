/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ng.upperlink.nibss.cmms.enums;

import ng.upperlink.nibss.cmms.dto.mandates.MandateChannelResponse;
import ng.upperlink.nibss.cmms.dto.mandates.MandateTypeResponse;

import javax.validation.constraints.Null;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Megafu Charles <noniboycharsy@gmail.com>
 */
public enum Channel {
    PORTAL("00","PORTAL"),
    MOBILE_BANKING("01","MOBILE BANKING"),
    INTERNET_BANKING("02","INTERNET BANKING"),
    USSD("03","USSD"),
    UNKNOWN("03","UNKNOWN");

    private String code;
    private String value;
    
    Channel(String code,String value) {
        this.value = value;
        this.code =code;
    }
    Channel(String code)
    {
        this.code = code;
    }
    
    public String getValue() {
        return  value;
    }
    public String getCode(){return code;}

    public static Channel findById(String  code)
    {
        Channel type = null;
        for (Channel channel : Channel.values())
        {
            if (channel.code.equals(code))
            {
                type = channel;
                break;
            }
        }
        return  type == null ? UNKNOWN : type;
    }
    public static Channel findByValue (String value)
    {
        Channel type = null;
        for (Channel channel : Channel.values())
        {
            if (channel.value.equalsIgnoreCase(value))
            {
                type = channel;
                break;
            }
        }
        return  type == null ? UNKNOWN : type;
    }
    public static synchronized Channel find(String channelName){
        try{
            return Channel.valueOf(channelName);
        }catch (Exception ex){
            return findByValue(channelName);
        }
    }

    public static List<MandateChannelResponse> getMandateChannels() {
        List<MandateChannelResponse> responseList = new ArrayList<>();
        int index = 1;
        for(Channel channel : Channel.values()) {
            MandateChannelResponse response = new MandateChannelResponse();
            response.setMandateChannel(channel.getValue());
            response.setId(index++);

            responseList.add(response);
        }
        return responseList;
    }



}
