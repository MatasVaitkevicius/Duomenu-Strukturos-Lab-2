package edu.ktu.ds.lab2.vaitkevicius;

import edu.ktu.ds.lab2.utils.Ks;
import edu.ktu.ds.lab2.utils.Parsable;

import java.time.LocalDate;
import java.util.*;

/**
 * @author EK
 */
public final class Book implements Parsable<Book> {

    // bendri duomenys visiems automobiliams (visai klasei)
    private static final int minYear = 1990;
    private static final int currentYear = LocalDate.now().getYear();
    private static final double minPrice = 100.0;
    private static final double maxPrice = 333000.0;
    private static final String idCode = "TA";   //  ***** nauja
    private static int serNr = 100;               //  ***** nauja

    private final String bookRegNr;

    private String author = "";
    private String title = "";
    private int year = -1;
    private double price = -1.0;

    public Book() {
        bookRegNr = idCode + (serNr++);    // suteikiamas originalus bookRegNr
    }

    public Book(String author, String title, int year, double price) {
        bookRegNr = idCode + (serNr++);    // suteikiamas originalus bookRegNr
        this.author = author;
        this.title = title;
        this.year = year;
        this.price = price;
        validate();
    }

    public Book(String dataString) {
        bookRegNr = idCode + (serNr++);    // suteikiamas originalus bookRegNr
        this.parse(dataString);
        validate();
    }

    public Book(Builder builder) {
        bookRegNr = idCode + (serNr++);    // suteikiamas originalus bookRegNr
        this.author = builder.author;
        this.title = builder.title;
        this.year = builder.year;
        this.price = builder.price;
        validate();
    }
    
        public Book(String dataString, boolean RandomReg) {
        if (RandomReg) {
            bookRegNr = idCode + (serNr++);    // suteikiamas originalus bookRegNr
        } else {
            Scanner scanner = new Scanner(dataString);
            bookRegNr = scanner.next();
        }
        this.parseAlternative(dataString, RandomReg);

        validate();
    }

    public Book create(String dataString) {
        return new Book(dataString);
    }

    private void validate() {
        String errorType = "";
        if (year < minYear || year > currentYear) {
            errorType = "Netinkami gamybos metai, turi būti ["
                    + minYear + ":" + currentYear + "]";
        }
        if (price < minPrice || price > maxPrice) {
            errorType += " Kaina už leistinų ribų [" + minPrice
                    + ":" + maxPrice + "]";
        }
        
        if (!errorType.isEmpty()) {
            Ks.ern("Knyga yra blogai sugeneruotas: " + errorType);
        }
    }

    @Override
    public void parse(String dataString) {
        try {   // duomenys, atskirti tarpais
            Scanner scanner = new Scanner(dataString);
            author = scanner.next();
            title = scanner.next();
            year = scanner.nextInt();
            setPrice(scanner.nextDouble());
        } catch (InputMismatchException e) {
            Ks.ern("Blogas duomenų formatas -> " + dataString);
        } catch (NoSuchElementException e) {
            Ks.ern("Trūksta duomenų -> " + dataString);
        }
    }

    public final void parseAlternative(String data, boolean random) {
        try {   // duomenys, atskirti tarpais
            Scanner scanner = new Scanner(data);
            if(!random)scanner.next();
            author = scanner.next();
            title = scanner.next();
            year = scanner.nextInt();
            setPrice(scanner.nextDouble());

        } catch (InputMismatchException e) {
            Ks.ern("Blogas duomenų formatas -> " + data);
        } catch (NoSuchElementException e) {
            Ks.ern("Trūksta duomenų -> " + data);
        }
    }

    @Override
    public String toString() {  // papildyta su bookRegNr
        return bookRegNr + "=" + author + "_" + title + ":" + year + " " + String.format("%4.1f", price);
    }

    public String ToString_data1() {  // papildyta su bookRegNr
        return bookRegNr + " " + author + " " + title + " " + year + " " + String.format("%4.1f", price);
    }
    public String ToString_data2() {  // papildyta su bookRegNr
        return author + " " + title + " " + year + " " + String.format("%4.1f", price);
    }

    public String getAuthor() {
        return author;
    }

    public String getTitle() {
        return title;
    }

    public int getYear() {
        return year;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getBookRegNr() {  //** nauja.
        return bookRegNr;
    }

    @Override
    public int compareTo(Book book) {
        return getBookRegNr().compareTo(book.getBookRegNr());
    }

    public static Comparator<Book> byAuthor = (Book c1, Book c2) -> c1.author.compareTo(c2.author);

    public static Comparator<Book> byPrice = (Book c1, Book c2) -> {
        // didėjanti tvarka, pradedant nuo mažiausios
        if (c1.price < c2.price) {
            return -1;
        }
        if (c1.price > c2.price) {
            return +1;
        }
        return 0;
    };

    public static Comparator<Book> byYearPrice = (Book c1, Book c2) -> {
        // metai mažėjančia tvarka, esant vienodiems lyginama kaina
        if (c1.year > c2.year) {
            return +1;
        }
        if (c1.year < c2.year) {
            return -1;
        }
        if (c1.price > c2.price) {
            return +1;
        }
        if (c1.price < c2.price) {
            return -1;
        }
        return 0;
    };

    // Car klases objektų gamintojas (builder'is)
    public static class Builder {

        private final static Random RANDOM = new Random(1949);  // Atsitiktinių generatorius
        private final static String[][] TITLES = { // galimų automobilių markių ir jų modelių masyvas
            {"Jonas Biliūnas", "Kliudžiau", "Liūdna Pasaka", "Ir Rados stebuklas",
                "Laimės žiburys"},
            {"Maironis", "Kur bėga Šešupė",
                "Lietuva", "Mūsu vargai", "Pavasario Balsai", "Nuo Birutės kalno"},
            {"Balys Sruoga", "Dievų miškas", "Dievų takais"},
            {"Salomėja Nėris", "Anksti rytą", "Pėdos smėly", "Laumės dovanos"},
            {"Kazys Binkis", "Eilėraščiai", "Atžalynas", "Meškeriotojas",
                "100 pavasarių"},
            {"Vincas Krėvė", "Skirgaila", "Skerdžius", "Miglose"}
        };

        private String author = "";
        private String title = "";
        private int year = -1;
        private double price = -1.0;

        public Book build() {
            return new Book(this);
        }

        public Book buildRandom() {
            int ma = RANDOM.nextInt(TITLES.length);
            int mo = RANDOM.nextInt(TITLES[ma].length - 1) + 1;
            return new Book(TITLES[ma][0],
                    TITLES[ma][mo],
                    1990 + RANDOM.nextInt(20),// metai tarp 1990 ir 2009
                    800 + RANDOM.nextDouble() * 88000);// kaina tarp 800 ir 88800
        }

        public Builder year(int year) {
            this.year = year;
            return this;
        }

        public Builder author(String author) {
            this.author = author;
            return this;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder price(double price) {
            this.price = price;
            return this;
        }
    }
}
