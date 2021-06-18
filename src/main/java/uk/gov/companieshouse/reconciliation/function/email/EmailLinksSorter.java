package uk.gov.companieshouse.reconciliation.function.email;

import org.apache.camel.Exchange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import uk.gov.companieshouse.reconciliation.config.EmailLinkModel;
import uk.gov.companieshouse.reconciliation.model.ResourceLink;
import uk.gov.companieshouse.reconciliation.model.ResourceLinksWrapper;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Uses a predefined model to sort the {@link ResourceLink}'s contained in the {@ResourceLinksWrapper} provided in
 * the supplied {@link Exchange} ResourceLinks header.
 *
 * <p>The resulting sorted {@ResourceLinksWrapper} replaces the unsorted original in the {@link Exchange} ResourceLinks
 * header.</p>
 */
public class EmailLinksSorter {
    /** Key to access {@link ResourceLinksWrapper} in {@link Exchange} */
    public static final String RESOURCE_LINKS_HEADER = "ResourceLinks";

    private Map<String, Map<String, EmailLinkModel>> emailLinksModelMap;

    /**
     * Creates a new EmailLinksSorter using a model that defines the ordering of email links.
     *
     * <p>Note: the supplied model map must contain an {@link EmailLinkModel} corresponding to the supplied
     * {@link ResourceLinksWrapper#getEmailId()}.</p>
     *
     * @param emailLinkOrderModel model defining the ordering of email links
     */
    @Autowired
    public EmailLinksSorter(@Qualifier("emailLinksModelMap") Map<String, Map<String, EmailLinkModel>> emailLinksModelMap) {
        this.emailLinksModelMap = emailLinksModelMap;
    }

    public void map(Exchange exchange) {
        ResourceLinksWrapper resourceLinksWrapper = (ResourceLinksWrapper) exchange.getIn().getHeaders().get(RESOURCE_LINKS_HEADER);
        List<ResourceLink> resourceLinks = resourceLinksWrapper.getDownloadLinkList();
        if (resourceLinks == null) {
            throw new IllegalStateException(String.format("Mandatory Exchange header not present: %s", RESOURCE_LINKS_HEADER));
        }

        // Key: Link Id, Value: EmailLinkModel
        Map<String, EmailLinkModel> emailLinkModelMap = this.emailLinksModelMap.get(resourceLinksWrapper.getEmailId());
        if (emailLinkModelMap == null) {
            throw new IllegalStateException(String.format("Mandatory EmailLinkModel not present, emailId: %s", resourceLinksWrapper.getEmailId()));
        }

        List<ResourceLink> sortedResourceLinks = resourceLinks.stream()
                .sorted(Comparator.comparingInt(a -> emailLinkModelMap.get(a.getLinkId()).getRank()))
                .collect(Collectors.toList());
        ResourceLinksWrapper sortedResourceLinksWrapper = new ResourceLinksWrapper(resourceLinksWrapper.getEmailId(), sortedResourceLinks);

        exchange.getIn().setHeader(RESOURCE_LINKS_HEADER, sortedResourceLinksWrapper);
    }
}
