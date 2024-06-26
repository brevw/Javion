package ch.epfl.javions.gui;

import ch.epfl.javions.aircraft.IcaoAddress;


/**
 * Represents country flags with corresponding ICAO address ranges and image filenames.
 * @author: Tlili Ahmed (344939)
 * @author: Bouden Omar (341381)
 */
public enum CountryFlags {
    AFGHANISTAN(0x700000, 0x700FFF, "Afghanistan.png"),
    ALBANIA(0x501000, 0x5013FF, "Albania.png"),
    ALGERIA(0x0A0000, 0x0A7FFF, "Algeria.png"),
    ANGOLA(0x090000, 0x090FFF, "Angola.png"),
    ANTIGUA_AND_BARBUDA(0x0CA000, 0x0CA3FF, "Antigua_and_Barbuda.png"),
    ARGENTINA(0xE00000, 0xE3FFFF, "Argentina.png"),
    ARMENIA(0x600000, 0x6003FF, "Armenia.png"),
    AUSTRALIA(0x7C0000, 0x7FFFFF, "Australia.png"),
    AUSTRIA(0x440000, 0x447FFF, "Austria.png"),
    AZERBAIJAN(0x600800, 0x600BFF, "Azerbaijan.png"),
    BAHAMAS(0x0A8000, 0x0A8FFF, "Bahamas.png"),
    BAHRAIN(0x894000, 0x894FFF, "Bahrain.png"),
    BANGLADESH(0x702000, 0x702FFF, "Bangladesh.png"),
    BARBADOS(0x0AA000, 0x0AA3FF, "Barbados.png"),
    BELARUS(0x510000, 0x5103FF, "Belarus.png"),
    BELGIUM(0x448000, 0x44FFFF, "Belgium.png"),
    BELIZE(0x0AB000, 0x0AB3FF, "Belize.png"),
    BENIN(0x094000, 0x0943FF, "Benin.png"),
    BHUTAN(0x680000, 0x6803FF, "Bhutan.png"),
    BOLIVIA(0xE94000, 0xE94FFF, "Bolivia.png"),
    BOSNIA_AND_HERZEGOVINA(0x513000, 0x5133FF, "Bosnia.png"),
    BOTSWANA(0x030000, 0x0303FF, "Botswana.png"),
    BRAZIL(0xE40000, 0xE7FFFF, "Brazil.png"),
    BRUNEI_DARUSSALAM(0x895000, 0x8953FF, "Brunei.png"),
    BULGARIA(0x450000, 0x457FFF, "Bulgaria.png"),
    BURKINA_FASO(0x09C000, 0x09CFFF, "Burkina_Faso.png"),
    BURUNDI(0x032000, 0x032FFF, "Burundi.png"),
    CAMBODIA(0x70E000, 0x70EFFF, "Cambodia.png"),
    CAMEROON(0x034000, 0x034FFF, "Cameroon.png"),
    CANADA(0xC00000, 0xC3FFFF, "Canada.png"),
    CAPE_VERDE(0x096000, 0x0963FF, "Cape_Verde.png"),
    CENTRAL_AFRICAN_REPUBLIC(0x06C000, 0x06CFFF, "Central_African_Republic.png"),
    CHAD(0x084000, 0x084FFF, "Chad.png"),
    CHILE(0xE80000, 0xE80FFF, "Chile.png"),
    CHINA(0x780000, 0x7BFFFF, "China.png"),
    COLOMBIA(0x0AC000, 0x0ACFFF, "Colombia.png"),
    COMOROS(0x035000, 0x0353FF, "Comoros.png"),
    CONGO(0x036000, 0x036FFF, "Republic_of_the_Congo.png"),
    COOK_ISLANDS(0x901000, 0x9013FF, "Cook_Islands.png"),
    COSTA_RICA(0x0AE000, 0x0AEFFF, "Costa_Rica.png"),
    COTE_D_IVOIRE(0x038000, 0x038FFF, "Cote_d_Ivoire.png"),
    CROATIA(0x501C00, 0x501FFF, "Croatia.png"),
    CUBA(0x0B0000, 0x0B0FFF, "Cuba.png"),
    CYPRUS(0x4C8000, 0x4C83FF, "Cyprus.png"),
    CZECH_REPUBLIC(0x498000, 0x49FFFF, "Czech_Republic.png"),
    DEMOCRATIC_PEOPLE_S_REPUBLIC_OF_KOREA(0x720000, 0x727FFF, "North_Korea.png"),
    DEMOCRATIC_REPUBLIC_OF_THE_CONGO(0x08C000, 0x08CFFF, "Democratic_Republic_of_the_Congo.png"),
    DENMARK(0x458000, 0x45FFFF, "Denmark.png"),
    DJIBOUTI(0x098000, 0x0983FF, "Djibouti.png"),
    DOMINICAN_REPUBLIC(0x0C4000, 0x0C4FFF, "Dominican_Republic.png"),
    ECUADOR(0xE84000, 0xE84FFF, "Ecuador.png"),
    EGYPT(0x010000, 0x017FFF, "Egypt.png"),
    EL_SALVADOR(0x0B2000, 0x0B2FFF, "El_Salvador.png"),
    EQUATORIAL_GUINEA(0x042000, 0x042FFF, "Equatorial_Guinea.png"),
    ERITREA(0x202000, 0x2023FF, "Eritrea.png"),
    ESTONIA(0x511000, 0x5113FF, "Estonia.png"),
    ETHIOPIA(0x040000, 0x040FFF, "Ethiopia.png"),
    FIJI(0xC88000, 0xC88FFF, "Fiji.png"),
    FINLAND(0x460000, 0x467FFF, "Finland.png"),
    FRANCE(0x380000, 0x3BFFFF, "France.png"),
    GABON(0x03E000, 0x03EFFF, "Gabon.png"),
    GAMBIA(0x09A000, 0x09AFFF, "Gambia.png"),
    GEORGIA(0x514000, 0x5143FF, "Georgia.png"),
    GERMANY(0x3C0000, 0x3FFFFF, "Germany.png"),
    GHANA(0x044000, 0x044FFF, "Ghana.png"),
    GREECE(0x468000, 0x46FFFF, "Greece.png"),
    GRENADA(0x0CC000, 0x0CC3FF, "Grenada.png"),
    GUATEMALA(0x0B4000, 0x0B4FFF, "Guatemala.png"),
    GUINEA(0x046000, 0x046FFF, "Guinea.png"),
    GUINEA_BISSAU(0x048000, 0x0483FF, "Guinea_Bissau.png"),
    GUYANA(0x0B6000, 0x0B6FFF, "Guyana.png"),
    HAITI(0x0B8000, 0x0B8FFF, "Haiti.png"),
    HONDURAS(0x0BA000, 0x0BAFFF, "Honduras.png"),
    HUNGARY(0x470000, 0x477FFF, "Hungary.png"),
    ICELAND(0x4CC000, 0x4CCFFF, "Iceland.png"),
    INDIA(0x800000, 0x83FFFF, "India.png"),
    INDONESIA(0x8A0000, 0x8A7FFF, "Indonesia.png"),
    IRAN_ISLAMIC_REPUBLIC_OF(0x730000, 0x737FFF, "Iran.png"),
    IRAQ(0x728000, 0x72FFFF, "Iraq.png"),
    IRELAND(0x4CA000, 0x4CAFFF, "Ireland.png"),
    ISRAEL(0x738000, 0x73FFFF, "Israel.png"),
    ITALY(0x300000, 0x33FFFF, "Italy.png"),
    JAMAICA(0x0BE000, 0x0BEFFF, "Jamaica.png"),
    JAPAN(0x840000, 0x87FFFF, "Japan.png"),
    JORDAN(0x740000, 0x747FFF, "Jordan.png"),
    KAZAKHSTAN(0x683000, 0x6833FF, "Kazakhstan.png"),
    KENYA(0x04C000, 0x04CFFF, "Kenya.png"),
    KIRIBATI(0xC8E000, 0xC8E3FF, "Kiribati.png"),
    KUWAIT(0x706000, 0x706FFF, "Kuwait.png"),
    KYRGYZSTAN(0x601000, 0x6013FF, "Kyrgyzstan.png"),
    LAO_PEOPLE_S_DEMOCRATIC_REPUBLIC(0x708000, 0x708FFF, "Laos.png"),
    LATVIA(0x502C00, 0x502FFF, "Latvia.png"),
    LEBANON(0x748000, 0x74FFFF, "Lebanon.png"),
    LESOTHO(0x04A000, 0x04A3FF, "Lesotho.png"),
    LIBERIA(0x050000, 0x050FFF, "Liberia.png"),
    LIBYAN_ARAB_JAMAHIRIYA(0x018000, 0x01FFFF, "Libya.png"),
    LITHUANIA(0x503C00, 0x503FFF, "Lithuania.png"),
    LUXEMBOURG(0x4D0000, 0x4D03FF, "Luxembourg.png"),
    MADAGASCAR(0x054000, 0x054FFF, "Madagascar.png"),
    MALAWI(0x058000, 0x058FFF, "Malawi.png"),
    MALAYSIA(0x750000, 0x757FFF, "Malaysia.png"),
    MALDIVES(0x05A000, 0x05A3FF, "Maldives.png"),
    MALI(0x05C000, 0x05CFFF, "Mali.png"),
    MALTA(0x4D2000, 0x4D23FF, "Malta.png"),
    MARSHALL_ISLANDS(0x900000, 0x9003FF, "Marshall_Islands.png"),
    MAURITANIA(0x05E000, 0x05E3FF, "Mauritania.png"),
    MAURITIUS(0x060000, 0x0603FF, "Mauritius.png"),
    MEXICO(0x0D0000, 0x0D7FFF, "Mexico.png"),
    MICRONESIA_FEDERATED_STATES_OF(0x681000, 0x6813FF, "Micronesia.png"),
    MONACO(0x4D4000, 0x4D43FF, "Monaco.png"),
    MONGOLIA(0x682000, 0x6823FF, "Mongolia.png"),
    MONTENEGRO(0x516000, 0x5163FF, "Montenegro.png"),
    MOROCCO(0x020000, 0x027FFF, "Morocco.png"),
    MOZAMBIQUE(0x006000, 0x006FFF, "Mozambique.png"),
    MYANMAR(0x704000, 0x704FFF, "Myanmar.png"),
    NAMIBIA(0x201000, 0x2013FF, "Namibia.png"),
    NAURU(0xC8A000, 0xC8A3FF, "Nauru.png"),
    NEPAL(0x70A000, 0x70AFFF, "Nepal.png"),
    NETHERLANDS_KINGDOM_OF_THE(0x480000, 0x487FFF, "Netherlands.png"),
    NEW_ZEALAND(0xC80000, 0xC87FFF, "New_Zealand.png"),
    NICARAGUA(0x0C0000, 0x0C0FFF, "Nicaragua.png"),
    NIGER(0x062000, 0x062FFF, "Niger.png"),
    NIGERIA(0x064000, 0x064FFF, "Nigeria.png"),
    NORWAY(0x478000, 0x47FFFF, "Norway.png"),
    OMAN(0x70C000, 0x70C3FF, "Oman.png"),
    PAKISTAN(0x760000, 0x767FFF, "Pakistan.png"),
    PALAU(0x684000, 0x6843FF, "Palau.png"),
    PANAMA(0x0C2000, 0x0C2FFF, "Panama.png"),
    PAPUA_NEW_GUINEA(0x898000, 0x898FFF, "Papua_New_Guinea.png"),
    PARAGUAY(0xE88000, 0xE88FFF, "Paraguay.png"),
    PERU(0xE8C000, 0xE8CFFF, "Peru.png"),
    PHILIPPINES(0x758000, 0x75FFFF, "Philippines.png"),
    POLAND(0x488000, 0x48FFFF, "Poland.png"),
    PORTUGAL(0x490000, 0x497FFF, "Portugal.png"),
    QATAR(0x06A000, 0x06A3FF, "Qatar.png"),
    REPUBLIC_OF_KOREA(0x718000, 0x71FFFF, "South_Korea.png"),
    REPUBLIC_OF_MOLDOVA(0x504C00, 0x504FFF, "Moldova.png"),
    ROMANIA(0x4A0000, 0x4A7FFF, "Romania.png"),
    RUSSIAN_FEDERATION(0x100000, 0x1FFFFF, "Russian_Federation.png"),
    RWANDA(0x06E000, 0x06EFFF, "Rwanda.png"),
    SAINT_LUCIA(0xC8C000, 0xC8C3FF, "Saint_Lucia.png"),
    SAINT_VINCENT_AND_THE_GRENADINES(0x0BC000, 0x0BC3FF, "Saint_Vincent_and_the_Grenadines.png"),
    SAMOA(0x902000, 0x9023FF, "Samoa.png"),
    SAN_MARINO(0x500000, 0x5003FF, "San_Marino.png"),
    SAO_TOME_AND_PRINCIPE(0x09E000, 0x09E3FF, "Sao_Tome_and_Principe.png"),
    SAUDI_ARABIA(0x710000, 0x717FFF, "Saudi_Arabia.png"),
    SENEGAL(0x070000, 0x070FFF, "Senegal.png"),
    SERBIA(0x4C0000, 0x4C7FFF, "Serbia.png"),
    SEYCHELLES(0x074000, 0x0743FF, "Seychelles.png"),
    SIERRA_LEONE(0x076000, 0x0763FF, "Sierra_Leone.png"),
    SINGAPORE(0x768000, 0x76FFFF, "Singapore.png"),
    SLOVAKIA(0x505C00, 0x505FFF, "Slovakia.png"),
    SLOVENIA(0x506C00, 0x506FFF, "Slovenia.png"),
    SOLOMON_ISLANDS(0x897000, 0x8973FF, "Soloman_Islands.png"),
    SOMALIA(0x078000, 0x078FFF, "Somalia.png"),
    SOUTH_AFRICA(0x008000, 0x00FFFF, "South_Africa.png"),
    SPAIN(0x340000, 0x37FFFF, "Spain.png"),
    SRI_LANKA(0x770000, 0x777FFF, "Sri_Lanka.png"),
    SUDAN(0x07C000, 0x07CFFF, "Sudan.png"),
    SURINAME(0x0C8000, 0x0C8FFF, "Suriname.png"),
    SWAZILAND(0x07A000, 0x07A3FF, "Swaziland.png"),
    SWEDEN(0x4A8000, 0x4AFFFF, "Sweden.png"),
    SWITZERLAND(0x4B0000, 0x4B7FFF, "Switzerland.png"),
    SYRIAN_ARAB_REPUBLIC(0x778000, 0x77FFFF, "Syria.png"),
    TAJIKISTAN(0x515000, 0x5153FF, "Tajikistan.png"),
    THAILAND(0x880000, 0x887FFF, "Thailand.png"),
    THE_FORMER_YUGOSLAV_REPUBLIC_OF_MACEDONIA(0x512000, 0x5123FF, "Macedonia.png"),
    TOGO(0x088000, 0x088FFF, "Togo.png"),
    TONGA(0xC8D000, 0xC8D3FF, "Tonga.png"),
    TRINIDAD_AND_TOBAGO(0x0C6000, 0x0C6FFF, "Trinidad_and_Tobago.png"),
    TUNISIA(0x028000, 0x02FFFF, "Tunisia.png"),
    TURKEY(0x4B8000, 0x4BFFFF, "Turkey.png"),
    TURKMENISTAN(0x601800, 0x601BFF, "Turkmenistan.png"),
    UGANDA(0x068000, 0x068FFF, "Uganda.png"),
    UKRAINE(0x508000, 0x50FFFF, "Ukraine.png"),
    UNITED_ARAB_EMIRATES(0x896000, 0x896FFF, "UAE.png"),
    UNITED_KINGDOM(0x400000, 0x43FFFF, "United_Kingdom.png"),
    UNITED_REPUBLIC_OF_TANZANIA(0x080000, 0x080FFF, "Tanzania.png"),
    UNITED_STATES(0xA00000, 0xAFFFFF, "United_States_of_America.png"),
    URUGUAY(0xE90000, 0xE90FFF, "Uruguay.png"),
    UZBEKISTAN(0x507C00, 0x507FFF, "Uzbekistan.png"),
    VANUATU(0xC90000, 0xC903FF, "Vanuatu.png"),
    VENEZUELA(0x0D8000, 0x0DFFFF, "Venezuela.png"),
    VIET_NAM(0x888000, 0x88FFFF, "Vietnam.png"),
    YEMEN(0x890000, 0x890FFF, "Yemen.png"),
    ZAMBIA(0x08A000, 0x08AFFF, "Zambia.png"),
    ZIMBABWE(0x004000, 0x0043FF, "Zimbabwe.png"),
    NONE(0xF00000, 0xFFFFFF, null);
    private final int fromHex, toHex;
    private final String fileName;
    CountryFlags(int fromHex, int toHex, String fileName) {
        this.fromHex = fromHex;
        this.toHex = toHex;
        this.fileName = fileName;
    }


    public static String fileNameOf(IcaoAddress icaoAddress){
        var icao = Integer.parseInt(icaoAddress.string(), 16);
        for(CountryFlags flag : CountryFlags.values()){
            if(flag.fromHex<=icao && icao<=flag.toHex) return flag.fileName;
        }
        return NONE.fileName;
    }
}
