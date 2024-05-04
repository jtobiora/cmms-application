/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ng.upperlink.nibss.cmms.enums;

/**
 *
 * @author Megafu Charles <noniboycharsy@gmail.com>
 */
public enum ServiceType {
    POSTPAID("POSTPAID"),
    PREPAID("PREPAID");
    private String value;
    
    ServiceType(String value) {
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }
}
