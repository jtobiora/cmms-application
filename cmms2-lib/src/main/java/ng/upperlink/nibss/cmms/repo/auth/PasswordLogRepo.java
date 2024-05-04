/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ng.upperlink.nibss.cmms.repo.auth;

import ng.upperlink.nibss.cmms.model.auth.PasswordLog;
import ng.upperlink.nibss.cmms.model.User;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Transactional
@Repository
public interface PasswordLogRepo extends JpaRepository<PasswordLog, Long> {
    @Query("SELECT p FROM PasswordLog p WHERE p.user = ?1 AND p.password = ?2")
    public PasswordLog findPassword(User user, String password);
    
    @Query("SELECT p FROM PasswordLog p WHERE p.user = ?1")
    public List<PasswordLog> getUserPasswordLogs(User user, Sort sort);
}
