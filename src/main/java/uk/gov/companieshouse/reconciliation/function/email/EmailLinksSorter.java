package uk.gov.companieshouse.reconciliation.function.email;

import org.apache.camel.Exchange;
import org.springframework.beans.factory.annotation.Autowired;
import uk.gov.companieshouse.reconciliation.config.ComparisonEmailLinkOrderingConfiguration;
import uk.gov.companieshouse.reconciliation.config.ComparisonGroupModel;
import uk.gov.companieshouse.reconciliation.config.LinkModel;
import uk.gov.companieshouse.reconciliation.model.ResourceLink;
import uk.gov.companieshouse.reconciliation.model.ResourceLinksWrapper;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * TODO: Redo Javadoc
 **/
public class EmailLinksSorter {

    private Map<String, Map<String, LinkModel>> comparisonEmailLinkConfigMap;

    @Autowired
    public EmailLinksSorter(Map<String, Map<String, LinkModel>> comparisonEmailLinkConfigMap) {
        this.comparisonEmailLinkConfigMap = comparisonEmailLinkConfigMap;
    }

    public void map(Exchange exchange) {
        String comparisonGroup = exchange.getIn().getHeader("ComparisonGroup", String.class);

        ResourceLinksWrapper resourceLinksWrapper = (ResourceLinksWrapper) exchange.getIn().getHeaders().get("ResourceLinks");
        List<ResourceLink> resourceLinks = resourceLinksWrapper.getDownloadLinkList();

        // Key: Link Id, Value: LinkModel
        Map<String, LinkModel> linkModelMap = comparisonEmailLinkConfigMap.get(resourceLinksWrapper.getEmailId());

        List<ResourceLink> sortedResourceLinks = resourceLinks.stream()
                .sorted(Comparator.comparingInt(a -> linkModelMap.get(a.getLinkId()).getRank()))
                .collect(Collectors.toList());
        ResourceLinksWrapper sortedResourceLinksWrapper = new ResourceLinksWrapper(resourceLinksWrapper.getEmailId(), sortedResourceLinks);

        exchange.getIn().setHeader("ResourceLinks", sortedResourceLinksWrapper);
    }
}
