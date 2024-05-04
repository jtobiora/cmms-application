/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ng.upperlink.nibss.cmms.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ng.upperlink.nibss.cmms.util.JsonDateSerializer;
import org.springframework.data.domain.Page;

/**
 *
 * @author Megafu Charles <noniboycharsy@gmail.com>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionDetailResponse<T> {
    @JsonSerialize(using = JsonDateSerializer.class)
    private Date startDate;
    @JsonSerialize(using = JsonDateSerializer.class)
    private Date endDate;
    Page<T> result;
}
