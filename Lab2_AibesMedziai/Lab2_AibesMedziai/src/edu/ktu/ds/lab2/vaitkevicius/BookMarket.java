package edu.ktu.ds.lab2.vaitkevicius;

import edu.ktu.ds.lab2.utils.BstSet;
import edu.ktu.ds.lab2.utils.Set;

public class BookMarket {

    public static Set<String> duplicateBookAuthors(Book[] books) {
        Set<Book> uni = new BstSet<>(Book.byAuthor);
        Set<String> duplicates = new BstSet<>();
        for (Book book : books) {
            int sizeBefore = uni.size();
            uni.add(book);

            if (sizeBefore == uni.size()) {
                duplicates.add(book.getAuthor());
            }
        }
        return duplicates;
    }

    public static Set<String> uniqueBookTitles(Book[] books) {
        Set<String> uniqueTitles = new BstSet<>();
        for (Book book : books) {
            uniqueTitles.add(book.getTitle());
        }
        return uniqueTitles;
    }
}
