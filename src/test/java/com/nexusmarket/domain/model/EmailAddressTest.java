package com.nexusmarket.domain.model;

import com.nexusmarket.domain.exception.BusinessRuleException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EmailAddressTest {

    @Test
    void rejectsIncompleteAddresses() {
        assertThrows(BusinessRuleException.class, () -> EmailAddress.normalize("@"));
        assertThrows(BusinessRuleException.class, () -> EmailAddress.normalize("a@"));
        assertThrows(BusinessRuleException.class, () -> EmailAddress.normalize("@b.com"));
        assertThrows(BusinessRuleException.class, () -> EmailAddress.normalize("a@b"));
        assertThrows(BusinessRuleException.class, () -> EmailAddress.normalize("a b@c.com"));
        assertThrows(BusinessRuleException.class, () -> EmailAddress.normalize("a@b..com"));
        assertThrows(BusinessRuleException.class, () -> EmailAddress.normalize(""));
        assertThrows(BusinessRuleException.class, () -> EmailAddress.normalize(" "));
        assertThrows(BusinessRuleException.class, () -> EmailAddress.normalize(null));
    }

    @Test
    void acceptsAddressWithDomainAndNormalizesCase() {
        assertEquals("ana@x.com", EmailAddress.normalize("ana@x.com"));
        assertEquals("a.b+c@mail.co.uk", EmailAddress.normalize("A.B+c@mail.co.uk"));
        assertEquals("admin@nexusmarket.local", EmailAddress.normalize("admin@nexusmarket.local"));
    }
}
