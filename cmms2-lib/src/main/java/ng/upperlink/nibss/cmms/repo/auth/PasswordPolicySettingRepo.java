/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ng.upperlink.nibss.cmms.repo.auth;

import ng.upperlink.nibss.cmms.model.auth.PasswordPolicySetting;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


public interface PasswordPolicySettingRepo extends JpaRepository<PasswordPolicySetting, Long> {
    @Query("SELECT p FROM PasswordPolicySetting p")
    public List<PasswordPolicySetting> getPolicySetting();
}
