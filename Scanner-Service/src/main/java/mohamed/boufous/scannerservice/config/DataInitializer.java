package mohamed.boufous.scannerservice.config;

import mohamed.boufous.scannerservice.entity.ScanResult;
import mohamed.boufous.scannerservice.entity.ScanTarget;
import mohamed.boufous.scannerservice.enums.ScanStatus;
import mohamed.boufous.scannerservice.repository.ScanResultRepository;
import mohamed.boufous.scannerservice.repository.ScanTargetRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    @Bean
    CommandLineRunner initDatabase(
            ScanTargetRepository targetRepository,
            ScanResultRepository resultRepository) {

        return args -> {

            if (targetRepository.count() > 0 || resultRepository.count() > 0) {
                return;
            }

            // -----------------------
            // Scan Targets
            // -----------------------

            ScanTarget target1 = new ScanTarget("https://google.com");
            target1.setStatus(ScanStatus.FINISHED);

            ScanTarget target2 = new ScanTarget("https://github.com");
            target2.setStatus(ScanStatus.ARRIVED);

            ScanTarget target3 = new ScanTarget("https://spring.io");
            target3.setStatus(ScanStatus.ARRIVED);

            targetRepository.save(target1);
            targetRepository.save(target2);
            targetRepository.save(target3);

            // -----------------------
            // Scan Results
            // -----------------------

            resultRepository.save(new ScanResult(
                    "Nuclei",
                    "https://google.com",
                    "LOW",
                    "Missing Security Headers",
                    "Some recommended HTTP security headers are missing.",
                    "Configure security headers.",
                    "X-Frame-Options missing",
                    "CWE-693",
                    3.7
            ));

            resultRepository.save(new ScanResult(
                    "Nikto",
                    "https://google.com",
                    "MEDIUM",
                    "Directory Listing Enabled",
                    "Directory listing is enabled.",
                    "Disable directory indexing.",
                    "/images/",
                    "CWE-548",
                    5.6
            ));

            resultRepository.save(new ScanResult(
                    "Subfinder",
                    "https://github.com",
                    "INFO",
                    "Subdomains Discovered",
                    "Several subdomains were discovered.",
                    "Review exposed subdomains.",
                    "docs.github.com",
                    "N/A",
                    0.0
            ));

            resultRepository.save(new ScanResult(
                    "Naabu",
                    "https://spring.io",
                    "LOW",
                    "Open Port",
                    "Port 443 is open.",
                    "Verify exposed services.",
                    "443/tcp",
                    "N/A",
                    2.5
            ));

            resultRepository.save(new ScanResult(
                    "Nuclei",
                    "https://spring.io",
                    "HIGH",
                    "Outdated Component",
                    "Detected outdated server component.",
                    "Update to the latest version.",
                    "Apache 2.4.49",
                    "CWE-1104",
                    8.2
            ));

            log.info("------------------------------------");
            log.info(" Demo data initialized successfully.");
            log.info("------------------------------------");
        };
    }
}