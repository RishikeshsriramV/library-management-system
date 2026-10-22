package com.library.model;

public class Book {
    private final int id, totalCopies, availableCopies;
    private final String title, author, category, isbn;
    public Book(int id, String title, String author, String category, String isbn, int total, int available) {
        this.id = id; this.title = title; this.author = author; this.category = category;
        this.isbn = isbn; this.totalCopies = total; this.availableCopies = available;
    }
    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getCategory() { return category; }
    public String getIsbn() { return isbn; }
    public int getTotalCopies() { return totalCopies; }
    public int getAvailableCopies() { return availableCopies; }
}
