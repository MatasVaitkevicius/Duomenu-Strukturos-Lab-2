/** @author Eimutis Karčiauskas, KTU IF Programų inžinerijos katedra, 2014 09 23
 *
 * Tai yra darbo su sąrašais greitaveikos tyrimo klasė.
 * Pavyzdyje pateiktas rikiavimo metodų tyrimas.
 * Tyrimo metu pateiktais metodais naudojamasi kaip šablonais,
 * išbandant įvairius rūšiavimo aspektus.
 *  IŠSIAIŠKINKITE metodų sudarymą, jų paskirtį.
 *  SUDARYKITE sąrašo peržiūros antišablono efektyvumo tyrimą.
 *  PASIRINKITE savo objektų klasę ir sudarykite jų generavimo metodą.
 *************************************************************************** */
package edu.ktu.ds.lab2.vaitkevicius;

import edu.ktu.ds.lab2.utils.Ks;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Locale;
import java.util.Random;
import edu.ktu.ds.lab2.utils.BstSet;
import java.util.HashSet;
import java.util.TreeSet;

/*
 */
public class Benchmark {

    Book[] books;
    BstSet<Book> bookSeries = new BstSet<Book>();
    static Random rg = new Random();  // atsitiktinių generatorius
    int[] counts = {2_000, 4_000, 8_000, 16_000};
    static TreeSet<Integer> treeSet = new TreeSet<Integer>();
    static HashSet<Integer> hashSet = new HashSet<Integer>();
    static Collection<Integer> col = new ArrayList<Integer>();
    
    void clearInts()
    {
        hashSet.clear();
        treeSet.clear();
        rg.setSeed(123);
    }
    static Collection<Integer> genContainable()
    {
        for(int i = 0; i < 1000; i++)
        {
            col.add(rg.nextInt());
        }
        return col;
    }
    static TreeSet<Integer> genTree(int kiek)
    {
        treeSet.clear();
        for(int i = 0; i < kiek; i++)
        {
            treeSet.add(rg.nextInt());
        }
        return treeSet;
    }
    static HashSet<Integer> genHash(int kiek)
    {
        hashSet.clear();
        for(int i = 0; i < kiek; i++)
        {
            hashSet.add(rg.nextInt());
        }
        return hashSet;
    }
    
    void addRun(int elemCount)
    {
        //preparation
        long t0 = System.nanoTime();
        long t1 = System.nanoTime();
        System.gc();
        long t2 = System.nanoTime();
        for(int i = 0; i < elemCount; i++)
        {
            hashSet.add(rg.nextInt());
        }
        long t3 = System.nanoTime();
        for(int i = 0; i < elemCount; i++)
        {
            treeSet.add(rg.nextInt());
        }
        long t4 = System.nanoTime();
        Ks.ouf("%7d %7.4f %7.4f %7.4f %7.4f \n", elemCount,
                (t1 - t0) / 1e9, (t2 - t1) / 1e9, (t3 - t2) / 1e9, (t4 - t3) / 1e9);
    }
    void containsRun(int elemCount)
    {
        long t0 = System.nanoTime();
        long t1 = System.nanoTime();
        System.gc();
        long t2 = System.nanoTime();
        for(int i = 0; i < elemCount; i++)
        {
            hashSet.remove(rg.nextInt());
        }
        long t3 = System.nanoTime();
        for(int i = 0; i < elemCount; i++)
        {
            treeSet.remove(rg.nextInt());
        }
        long t4 = System.nanoTime();
        Ks.ouf("%7d %7.4f %7.4f %7.4f %7.4f \n", elemCount,
                (t1 - t0) / 1e9, (t2 - t1) / 1e9, (t3 - t2) / 1e9, (t4 - t3) / 1e9);
    }
    void generateBooks(int count) {
        String[][] authorsAndNames = { 
            {"Yayay yayaya", "1", "3", "2", "7"},
            {"Dama", "Fiesta", "Escort", "Focus", "Sierra", "Mondeo"},
            {"Mada", "92", "96"},
            {"Alejandro", "Accord", "Civic", "Jazz"},
            {"Gaga", "Laguna", "Megane", "Twingo", "Scenic"},
            {"Boo", "206", "207", "307"}
        };
        books = new Book[count];
        rg.setSeed(2019);
        for (int i = 0; i < count; i++) {
            int makeIndex = rg.nextInt(authorsAndNames.length);        // indeksas  0..
            int modelIndex = rg.nextInt(authorsAndNames[makeIndex].length - 1) + 1;// indeksas 1..
            books[i] = new Book(authorsAndNames[makeIndex][0], authorsAndNames[makeIndex][modelIndex],
                    1994 + rg.nextInt(20), // metai tarp 1994 ir 2013
                    1000 + rg.nextDouble() * 350_000); // kaina tarp 1000 ir 351_000
        }
        Collections.shuffle(Arrays.asList(books));
        bookSeries.clear();
        for (Book b : books) {
            bookSeries.add(b);
        }
    }


    void run() {
        long memTotal = Runtime.getRuntime().totalMemory();
        Ks.oun("memTotal= " + memTotal);
        // Pasižiūrime kaip generuoja automobilius (20) vienetų)
        generateBooks(20);
        
        long mem1 = Runtime.getRuntime().totalMemory();
        Ks.oun("1 - Pasiruošimas tyrimui - duomenų generavimas");
        Ks.oun("2 - Pasiruošimas tyrimui - šiukšlių surinkimas");
        Ks.oun("3 - HashSet.Add");
        Ks.oun("4 - TreeSet.Add");
        Ks.ouf("%6d %7d %7d %7d %7d \n", 0, 1, 2, 3, 4);
        for(int n : counts)
        {
            addRun(n);
        }
        long mem2 = Runtime.getRuntime().freeMemory();
        Ks.oun("Used memory: " + (mem1-mem2));
        mem1 = Runtime.getRuntime().totalMemory();
        Ks.oun("1 - Pasiruošimas tyrimui - duomenų generavimas");
        Ks.oun("2 - Pasiruošimas tyrimui - šiukšlių surinkimas");
        Ks.oun("3 - HashSet.Remove");
        Ks.oun("4 - TreeSet.Remove");
        Ks.ouf("%6d %7d %7d %7d %7d \n", 0, 1, 2, 3, 4);
        for(int n : counts)
        {
            containsRun(n);
        }
        mem2 = Runtime.getRuntime().freeMemory();
        Ks.oun("Used memory: " + (mem1-mem2));
    }

    public static void main(String[] args) {
        // suvienodiname skaičių formatus pagal LT lokalę (10-ainis kablelis)
        Locale.setDefault(new Locale("LT"));
        new Benchmark().run();
    }
}
