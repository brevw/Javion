package ch.epfl.javions.aircraft;

import java.io.*;
import java.util.Objects;
import java.util.zip.ZipFile;

import static java.nio.charset.StandardCharsets.UTF_8;



/**
 * This record represents an aircraft database
 *
 * @author: Bouden Omar (341381)
 * @author: Tlili Ahmed (344939)
 */
public final class AircraftDatabase {
    private static final int FILE_NAME_FIRST_DIGIT_INDEX = 4;
    private final String fileName;
    private static final int ICAO_ADDRESS_START = 0, ICAO_ADDRESS_FINISH = ICAO_ADDRESS_START + IcaoAddress.ICAO_ADDRESS_LENGTH;

    /**
     * public AircraftRegistration compact constructor
     * Returns an object representing the mictronics database
     *
     * @param fileName (string)
     * @throws IllegalArgumentException when fileName is null
     */
    public AircraftDatabase(String fileName) {
        this.fileName = Objects.requireNonNull(fileName);
    }



    /**
     * @param address(IcaoAddress) address to search with
     * @return AircraftData associated to an IcaoAddress if found in files
     * else return null
     */
    public AircraftData get(IcaoAddress address) throws IOException{
        try (ZipFile zipFile = new ZipFile(fileName);

             InputStream s = zipFile.getInputStream(zipFile.getEntry(address.string().substring(FILE_NAME_FIRST_DIGIT_INDEX) + ".csv"));
             Reader r = new InputStreamReader(s, UTF_8);
             BufferedReader b = new BufferedReader(r)) {

            return b.lines()
                    .dropWhile( l -> l.substring(ICAO_ADDRESS_START, ICAO_ADDRESS_FINISH).compareTo(address.string()) < 0)
                    .takeWhile(l -> l.startsWith(address.string()))
                    .map(l -> l.split(","))
                    .map(args -> new AircraftData(  new AircraftRegistration(args[1]),
                                                    new AircraftTypeDesignator(args[2]),
                                                    args[3],
                                                    new AircraftDescription(args[4]),
                                                    WakeTurbulenceCategory.of(args[5])))
                    .findFirst()
                    .orElse(null);
        }
    }


}
