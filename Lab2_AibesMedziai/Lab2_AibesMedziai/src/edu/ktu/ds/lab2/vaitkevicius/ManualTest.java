package edu.ktu.ds.lab2.vaitkevicius;

import edu.ktu.ds.lab2.utils.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.Locale;

/*
 * Aibės testavimas be Gui
 *
 */
public class ManualTest {

    static Book[] books;
    static ParsableSortedSet<Book> cSeries = new ParsableBstSet<>(Book::new, Book.byPrice);

    public static void main(String[] args) throws CloneNotSupportedException {
        Locale.setDefault(Locale.US); // Suvienodiname skaičių formatus
        executeTest();
    }

    public static void executeTest() throws CloneNotSupportedException {
        Book c1 = new Book("AAAuotirus", "PAVAADINIMAS", 1997, 1700);
        Book c2 = new Book.Builder()
                .author("Jonauskas")
                .title("Medis")
                .year(2001)
                .price(200)
                .build();
        Book c3 = new Book.Builder().buildRandom();
        Book c4 = new Book("Gendaris Lunara 2001 115900 700");
        Book c5 = new Book("Ziusepe France 1946 365100 9500");
        Book c6 = new Book("Razda Grande 2001 36400 80.3");
        Book c7 = new Book("Ziusepe France 1946 365100 9500");
        Book c8 = new Book("Yopapa Pavvadinimas 1946 365100 950");
        Book c9 = new Book("Kopapap ahahah 2007 36400 850.3");

        Book[] booksArray = {c9, c7, c8, c5, c1, c6};

        Ks.oun("Knygų Aibė:");
        ParsableSortedSet<Book> booksSet = new ParsableBstSet<>(Book::new);

        for (Book c : booksArray) {
            booksSet.add(c);
            Ks.oun("Aibė papildoma: " + c + ". Jos dydis: " + booksSet.size());
        }
        Ks.oun("");
        Ks.oun(booksSet.toVisualizedString(""));

        ParsableSortedSet<Book> booksSetCopy = (ParsableSortedSet<Book>) booksSet.clone();

        booksSetCopy.add(c2);
        booksSetCopy.add(c3);
        booksSetCopy.add(c4);
        Ks.oun("Papildyta knygų aibės kopija:");
        Ks.oun(booksSetCopy.toVisualizedString(""));

        Ks.oun("Originalas:");
        Ks.ounn(booksSet.toVisualizedString(""));

        Ks.oun("Ar elementai egzistuoja aibėje?");
        for (Book c : booksArray) {
            Ks.oun(c + ": " + booksSet.contains(c));
        }
        Ks.oun(c2 + ": " + booksSet.contains(c2));
        Ks.oun(c3 + ": " + booksSet.contains(c3));
        Ks.oun(c4 + ": " + booksSet.contains(c4));
        Ks.oun("");

        Ks.oun("Ar elementai egzistuoja aibės kopijoje?");
        for (Book c : booksArray) {
            Ks.oun(c + ": " + booksSetCopy.contains(c));
        }
        Ks.oun(c2 + ": " + booksSetCopy.contains(c2));
        Ks.oun(c3 + ": " + booksSetCopy.contains(c3));
        Ks.oun(c4 + ": " + booksSetCopy.contains(c4));
        Ks.oun("");

        Ks.oun("Elementų šalinimas iš kopijos. Aibės dydis prieš šalinimą:  " + booksSetCopy.size());
        for (Book c : new Book[]{c2, c1, c9, c8, c5, c3, c4, c2, c7, c6, c7, c9}) {
            booksSetCopy.remove(c);
            Ks.oun("Iš knygų aibės kopijos pašalinama: " + c + ". Jos dydis: " + booksSetCopy.size());
        }
        Ks.oun("");

        Ks.oun("Knygų aibė su iteratoriumi:");
        Ks.oun("");
        for (Book c : booksSet) {
            Ks.oun(c);
        }
        Ks.oun("");
        Ks.oun("Knygų aibė AVL-medyje:");
        ParsableSortedSet<Book> booksSetAvl = new ParsableAvlSet<>(Book::new);
        for (Book c : booksArray) {
            booksSetAvl.add(c);
        }
        Ks.ounn(booksSetAvl.toVisualizedString(""));

        Ks.oun("Knygų aibė su iteratoriumi:");
        Ks.oun("");
        for (Book c : booksSetAvl) {
            Ks.oun(c);
        }

        Ks.oun("");
        Ks.oun("Knygų aibė su atvirkštiniu iteratoriumi:");
        Ks.oun("");
        Iterator iter = booksSetAvl.descendingIterator();
        while (iter.hasNext()) {
            Ks.oun(iter.next());
        }

        Ks.oun("");
        Ks.oun("Knygų aibės toString() metodas:");
        Ks.ounn(booksSetAvl);

        // Išvalome ir suformuojame aibes skaitydami iš failo
        booksSet.clear();
        booksSetAvl.clear();

        Ks.oun("");
        Ks.oun("Knygų aibė DP-medyje:");
        booksSet.load("data\\ban.txt");
        Ks.ounn(booksSet.toVisualizedString(""));
        Ks.oun("Išsiaiškinkite, kodėl medis augo tik į vieną pusę.");

        Ks.oun("");
        Ks.oun("Knygų aibė AVL-medyje:");
        booksSetAvl.load("data\\ban.txt");
        Ks.ounn(booksSetAvl.toVisualizedString(""));

        Set<String> booksSet4 = BookMarket.duplicateBookAuthors(booksArray);
        Ks.oun("Pasikartojančios knygų autoriai:\n" + booksSet4.toString());

        Set<String> booksSet5 = BookMarket.uniqueBookTitles(booksArray);
        Ks.oun("Unikalūs Knygų pavadinimai:\n" + booksSet5.toString());
    }

    static ParsableSortedSet generateSet(int kiekis, int generN) {
        books = new Book[generN];
        for (int i = 0; i < generN; i++) {
            books[i] = new Book.Builder().buildRandom();
        }
        Collections.shuffle(Arrays.asList(books));

        cSeries.clear();
        Arrays.stream(books).limit(kiekis).forEach(cSeries::add);
        return cSeries;
    }
}
