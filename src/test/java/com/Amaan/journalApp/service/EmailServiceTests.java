package com.Amaan.journalApp.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class EmailServiceTests {

    @Autowired
    private EmailService emailService;

    //    @Test
//    void testSendEmail(){
//        emailService.sendEmail("theamaan619@gmail.com","Testing Java Mail Sender Services","Hi Kaise Hooo");
//    }
    @Test
    void testSendEmail() {
        try {
            emailService.sendEmail("theamaan619@gmail.com", "Testing Java Mail Sender Services", "Hue Hue From JMS {Java Mail Service}");
            System.out.println("✅ Email sent successfully!");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("❌ Email sending failed: " + e.getMessage());
        }
    }
}
