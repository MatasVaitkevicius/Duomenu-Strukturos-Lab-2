package edu.ktu.ds.lab2.vaitkevicius;

import edu.ktu.ds.lab2.gui.ValidationException;

import java.util.Arrays;
import java.util.Collections;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class BooksGenerator {

    private int startIndex = 0, lastIndex = 0;
    private boolean isStart = true;

    private Book[] books;

    public Book[] generateShuffle(int setSize,
                                 double shuffleCoef) throws ValidationException {

        return generateShuffle(setSize, setSize, shuffleCoef);
    }

    /**
     * @param setSize
     * @param setTake
     * @param shuffleCoef
     * @return Gražinamas aibesImtis ilgio masyvas
     * @throws ValidationException
     */
    public Book[] generateShuffle(int setSize,
                                 int setTake,
                                 double shuffleCoef) throws ValidationException {

        Book[] books = IntStream.range(0, setSize)
                .mapToObj(i -> new Book.Builder().buildRandom())
                .toArray(Book[]::new);
        return shuffle(books, setTake, shuffleCoef);
    }

    public Book takeBook() throws ValidationException {
        if (lastIndex < startIndex) {
            throw new ValidationException(String.valueOf(lastIndex - startIndex), 4);
        }
        // Vieną kartą Automobilis imamas iš masyvo pradžios, kitą kartą - iš galo.
        isStart = !isStart;
        return books[isStart ? startIndex++ : lastIndex--];
    }

    private Book[] shuffle(Book[] books, int amountToReturn, double shuffleCoef) throws ValidationException {
        if (books == null) {
            throw new IllegalArgumentException("Knygų nėra (null)");
        }
        if (amountToReturn <= 0) {
            throw new ValidationException(String.valueOf(amountToReturn), 1);
        }
        if (books.length < amountToReturn) {
            throw new ValidationException(books.length + " >= " + amountToReturn, 2);
        }
        if ((shuffleCoef < 0) || (shuffleCoef > 1)) {
            throw new ValidationException(String.valueOf(shuffleCoef), 3);
        }

        int amountToLeave = books.length - amountToReturn;
        int startIndex = (int) (amountToLeave * shuffleCoef / 2);

        Book[] takeToReturn = Arrays.copyOfRange(books, startIndex, startIndex + amountToReturn);
        Book[] takeToLeave = Stream
                .concat(Arrays.stream(Arrays.copyOfRange(books, 0, startIndex)),
                        Arrays.stream(Arrays.copyOfRange(books, startIndex + amountToReturn, books.length)))
                .toArray(Book[]::new);

        Collections.shuffle(Arrays.asList(takeToReturn)
                .subList(0, (int) (takeToReturn.length * shuffleCoef)));
        Collections.shuffle(Arrays.asList(takeToLeave)
                .subList(0, (int) (takeToLeave.length * shuffleCoef)));

        this.startIndex = 0;
        this.lastIndex = takeToLeave.length - 1;
        this.books = takeToLeave;
        return takeToReturn;
    }
}
