/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ng.upperlink.nibss.cmms.repo;

import ng.upperlink.nibss.cmms.model.TransactionParam;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author Megafu Charles <noniboycharsy@gmail.com>
 */
public interface TransactionParamRepo extends JpaRepository<TransactionParam, Long> {
    
}
