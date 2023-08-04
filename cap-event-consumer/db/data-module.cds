using {
    managed,
    sap,
} from '@sap/cds/common';

namespace sap.aem.integration;

entity businessPartner {
    key id                  : UUID;
        partnerId           : String;
        firstName           : String;
        lastName            : String;
        businessPartnerType : String;
        adressLink          : composition of many adressLink
                                  on adressLink.businessPartner = $self;

}

entity adressLink {
    key id              : UUID;
        dateFrom        : Date;
        address         : composition of many adress
                              on address.adressLink = $self;
        businessPartner : composition of businessPartner;
}

entity adress {
    key id          : UUID;
        nation      : String;
        city        : String;
        street      : String;
        houseNumber : String;
        postalCode  : String;
        country     : String;
        adressLink  : composition of adressLink;
}
