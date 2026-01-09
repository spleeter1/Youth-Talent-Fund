package com.graduation.youthtalentfund.scheduler;

import com.graduation.youthtalentfund.entities.Campaign;
import com.graduation.youthtalentfund.enums.CampaignStatus;
import com.graduation.youthtalentfund.repositories.CampaignRepository;
import com.graduation.youthtalentfund.services.CampaignService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CampaignStatusScheduler {
    private final CampaignRepository campaignRepository;
    private final CampaignService campaignService;

    @Scheduled(cron = "0 */1 * * * ?")
    @Transactional
    public void updateCampaignStatuses() {
        List<CampaignStatus> excluded = List.of(CampaignStatus.COMPLETED, CampaignStatus.CANCELLED);
        List<Campaign> campaigns = campaignRepository.findAllActiveCampaigns(excluded);

        for (Campaign c : campaigns) {
            CampaignStatus newStatus = campaignService.determineStatus(c);
            if (newStatus != c.getStatus()) {
                c.setStatus(newStatus);
            }
        }

        campaignRepository.saveAll(campaigns);
        System.out.println("updt status");
    }
}
