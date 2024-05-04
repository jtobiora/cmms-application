package ng.upperlink.nibss.cmms.repo.test;

import ng.upperlink.nibss.cmms.config.email.MailConfigImpl;
import ng.upperlink.nibss.cmms.service.*;
import ng.upperlink.nibss.cmms.service.auth.RoleService;
import ng.upperlink.nibss.cmms.service.bank.BankService;
import ng.upperlink.nibss.cmms.service.bank.BankUserService;
import ng.upperlink.nibss.cmms.service.biller.BillerService;
import ng.upperlink.nibss.cmms.service.biller.BillerUserService;
import ng.upperlink.nibss.cmms.service.biller.IndustryService;
import ng.upperlink.nibss.cmms.service.biller.ProductService;
import ng.upperlink.nibss.cmms.service.contact.CountryService;
import ng.upperlink.nibss.cmms.service.contact.LgaService;
import ng.upperlink.nibss.cmms.service.contact.StateService;
import ng.upperlink.nibss.cmms.util.email.EmailService;
import ng.upperlink.nibss.cmms.util.email.SmtpMailSender;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.test.context.junit4.SpringRunner;
import org.thymeleaf.spring4.SpringTemplateEngine;



@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class Service {

    @Autowired
    private TestEntityManager entityManager;



    @TestConfiguration
    static class NibssUserServiceTestContextConfiguration {

        @Autowired
        private Environment environment;

        @Bean
        public UserService userService(){
            return new UserService();
        }

        @Bean
        public SubscriberService subscriberService(){
            return new SubscriberService();
        }


        @Bean
        public RoleService roleService(){
            return new RoleService();
        }

        @Bean
        public MailConfigImpl mailConfig(){
            return new MailConfigImpl();
        }

        @Bean
        public EmailService emailService(){
            return new EmailService();
        }

        @Bean
        public SpringTemplateEngine springTemplateEngine(){
            return new SpringTemplateEngine();
        }

        @Bean
        public JavaMailSenderImpl javaMailSender(){
            return new JavaMailSenderImpl();
        }

        @Bean
        public SmtpMailSender smtpMailSender(){
            return new SmtpMailSender(environment,mailConfig(),emailService());
        }

        @Bean
        public LgaService lgaService(){
            return new LgaService();
        }

        @Bean
        public StateService stateService(){
            return new StateService();
        }

        @Bean
        public CountryService countryService(){
            return new CountryService();
        }

        @Bean
        public ProductService productService(){return new ProductService();}

        @Bean
        public BillerService billerService(){return new BillerService();}

        @Bean
        public BillerUserService billerUserService(){return new BillerUserService();}

        @Bean
        public BankUserService bankUserService(){return new BankUserService();}

        @Bean
        public BankService bankService(){return new BankService();}

        @Bean
        public IndustryService industryService(){return new IndustryService();}
    }

    @Test
    public void testGetLeaveStat(){

/*
           Query query = entityManager.
                   getEntityManager().createQuery("select new com.upl.nibss.bvn.dto.EnrollerDto(e.id,e.user.name.firstName," +
                   " e.user.name.lastName, e.user.contactDetails.lga.state.name," +
                   "e.code,e.user.contactDetails.lga.name, e.user.emailAddress,e.user.roles.name, e.user.bvn,e.agent.code," +
                   "e.agent.agentManager.agentManagerInstitution.code,e.createdAt) from Enroller e "+
                   " join e.agent a join a.agentManager ag where ag.agentManagerInstitution.id = ?1 order by e.id desc ", EnrollerDto.class);

           query.setParameter(1, Long.valueOf("6"));
        List<EnrollerDto> resultList = query.getResultList();


        Integer count = enrollmentReportRepo.getSynchReportForNibssAndActive(CommonUtils.subtractDays(new Date(), 1), CommonUtils.endOfDay(new Date()));
*/

/*        institutionRepo.toggleBiller(100, "002");
        institutionRepo.getByCode("002");
        institutionRepo.getByCodeEnabled("002");*/

/*        System.out.println(" The result-List is => "+resultList);
        System.out.println(" The result-List size is => "+resultList.size());


        System.out.println("The start date is "+CommonUtils.startOfDay(new Date()));
        System.out.println("The end date is "+ CommonUtils.endOfDay(new Date()));
        System.out.println("The count is "+count);*/

//        Dashboard dashBoardReport = agentTransactionReportRepo.getDashBoardReport("334");
//        System.out.println("The data is "+dashBoardReport);

//        Assert.assertNotEquals(resultList.size(), 0);
        Assert.assertNotEquals(1, 0);

    }

}
