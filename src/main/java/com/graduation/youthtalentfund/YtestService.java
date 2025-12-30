package com.graduation.youthtalentfund;

import com.graduation.youthtalentfund.entities.ProofReport;
import com.graduation.youthtalentfund.repositories.ProofReportRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

//@Component
public class YtestService implements CommandLineRunner {

    private final ProofReportRepository proofReportRepository;

    public YtestService(ProofReportRepository proofReportRepository) {
        this.proofReportRepository = proofReportRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {

        Long testId = 1L; // ID Report test của bạn
        ProofReport report = proofReportRepository.findById(testId).orElseThrow();

        System.out.println("---- STEP 1: Report loaded ----");
        System.out.println("Title = " + report.getTitle());

        System.out.println("\n---- STEP 2: Accessing attachments (Lazy Trigger Query) ----");
        report.getAttachments().forEach(a -> {
            System.out.println("Attachment: " + a.getOriginalFilename());
        });

        System.out.println("\n---- STEP 3: Try load attachment -> proofReport reverse ----");
        var att = report.getAttachments().get(0);
        System.out.println("Attachment ID = " + att.getId());

        System.out.println("Access proofReport via attachment => lazy?");
        System.out.println(att.getProofReport().getTitle());
    }
}
