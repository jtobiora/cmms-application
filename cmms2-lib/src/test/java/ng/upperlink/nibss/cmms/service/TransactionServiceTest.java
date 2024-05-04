/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ng.upperlink.nibss.cmms.service;

import ng.upperlink.nibss.cmms.model.Transaction;
import ng.upperlink.nibss.cmms.repo.TransactionRepo;
import ng.upperlink.nibss.cmms.service.bank.BankUserService;
import ng.upperlink.nibss.cmms.service.biller.BillerUserService;
import ng.upperlink.nibss.cmms.service.pssp.PsspUserService;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 *
 * @author Megafu Charles <noniboycharsy@gmail.com>
 */
public class TransactionServiceTest {
    private TransactionService transactionService;
    private TransactionRepo transactionRepo;
    private BillerUserService billerUserService;
    private BankUserService bankUserService;
    private PsspUserService psspUserService;

    @Before
    public void setup() {
        this.transactionRepo = mock(TransactionRepo.class);
        this.billerUserService = mock(BillerUserService.class);
        this.transactionService = new TransactionService(transactionRepo, billerUserService,bankUserService,psspUserService);
    }

    /**
     * Test successful transaction saving
     * @throws Exception
     */
    @Test
    public void when_save_transaction_return_successful() throws Exception {
        when(transactionRepo.saveAndFlush(any())).thenReturn(new Transaction());
        transactionService.saveTransaction(new Transaction());
        verify(transactionRepo, times(1)).saveAndFlush(any()); // ensure that save transaction is called once
    }
}
